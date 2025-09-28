package technixc.sse.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import technixc.sse.dto.Event;
import technixc.sse.repository.RegisteredUsersRedisRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProcessor {

    private final SsePushService ssePushService;
    private final RegisteredUsersRedisRepository registeredUsersRedisRepository;

    private final ObjectMapper objectMapper;

    public void process(Event event) {
        Optional.ofNullable(event)
                .map(Event::getUserId)
                .map(registeredUsersRedisRepository::findByUserId)
                .ifPresentOrElse(
                        registeredList -> registeredList.forEach(registeredUser -> {
                            try {
                                var content = objectMapper.writeValueAsString(event);
                                log.debug("Message for sending: {}", content);
                                ssePushService.sendMessage(registeredUser.getQueueId(), content);
                            } catch (JsonProcessingException e) {
                                log.debug("Bad push message!");
                            }
                        }),
                        () -> log.debug("No queue registered for message: {}", event));
    }

}
