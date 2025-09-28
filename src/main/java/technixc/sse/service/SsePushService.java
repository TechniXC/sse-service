package technixc.sse.service;

import reactor.core.publisher.Flux;

public interface SsePushService {

    /**
     * Sends an SSE message to the client.
     *
     * @param queueId User identifier in the RabbitMQ queue
     * @param content JSON string to be sent to the client via SSE
     * @return Mono Publisher that sends a single message to the client; you must call subscribe()
     */
    void sendMessage(String queueId, String content);

    /**
     * Method used only in UserMessageController.class to subscribe to pushes.
     *
     * @param queueId
     * @return
     */
    Flux<String> receiveStream(String queueId, String userId);
}
