package technixc.sse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.Sender;
import technixc.sse.configuration.SSEConfiguration;
import technixc.sse.repository.RegisteredUsersRedisRepository;

import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitSsePushServiceImpl implements SsePushService {

    private static final String SSE_PUSH_EXCHANGE = "sse-push-";

    private final SSEConfiguration sseConfiguration;
    private final RegisteredUsersRedisRepository redisRepository;
    private final Sender sender;
    private final Receiver receiver;

    public void sendMessage(String queueId, String content) {
        String routingKey = SSE_PUSH_EXCHANGE + queueId;

        OutboundMessage message = new OutboundMessage("", routingKey, content.getBytes());

        sender.declareQueue(
                        QueueSpecification.queue(routingKey).exclusive(false).autoDelete(true))
                .doOnSuccess(q -> log.debug("Queue {} declared", routingKey))
                .thenMany(sender.sendWithPublishConfirms(Mono.fromSupplier(() -> message)))
                .doOnError(e -> log.error("Failed to send message for {}", routingKey, e))
                .subscribe(m -> {
                    if (m.isAck()) {
                        log.debug("Message sent for {}", routingKey);
                    }
                });
    }

    public Flux<String> receiveStream(String queueId, String userId) {
        sendMessage(queueId, "queueId:" + queueId);
        var routingKey = SSE_PUSH_EXCHANGE + queueId;

        return sender.declareQueue(QueueSpecification.queue(routingKey)
                        .exclusive(false)
                        .autoDelete(true))
                .doOnNext(q -> log.debug("Queue {} declared", routingKey))
                .thenMany(
                        receiver.consumeAutoAck(routingKey)
                                .doOnSubscribe(sub -> {
                                    sendMessage(queueId, "userId:" + userId);
                                })
                                .doOnNext(msg -> log.debug("Received message to queue {}: {}", routingKey, new String(msg.getBody())))
                                .map(msg -> new String(msg.getBody()))
                )
                .timeout(sseConfiguration.getTimeout(),
                        Flux.just("No new messages during 1 hour, closing Streaming"))
                .publishOn(Schedulers.boundedElastic())
                .doOnCancel(() -> {
                    log.debug("Streaming cancelling for queueId {}", queueId);
                    redisRepository.findByQueueId(queueId).ifPresent(x -> redisRepository.deleteById(x.getId()));
                })
                .doOnError(
                        TimeoutException.class,
                        (timeoutException) -> {
                            log.debug("Queue {} is closed by timeout", queueId);
                            redisRepository.findByQueueId(queueId).ifPresent(x -> redisRepository.deleteById(x.getId()));
                        });
    }
}
