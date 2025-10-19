package technixc.sse.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import technixc.sse.dto.Event;
import technixc.sse.processor.EventProcessor;
import technixc.sse.service.RegistrationService;
import technixc.sse.service.SsePushService;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class SseController {

    private final SsePushService ssePushService;
    private final RegistrationService registrationService;
    private final EventProcessor eventProcessor;

    public final static String HEADER_NAME = "x-user";

    @GetMapping(path = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> receive(@RequestHeader(name = HEADER_NAME) String userId) {
        var queueId = UUID.randomUUID().toString();
        registrationService.registerUser(userId, queueId);
        return ssePushService.receiveStream(queueId, userId);
    }

    @PostMapping(path = "/send-message")
    public ResponseEntity<Void> sendMessage(@RequestBody Event message) {
        eventProcessor.process(message);
        return ResponseEntity.ok().build();
    }
}
