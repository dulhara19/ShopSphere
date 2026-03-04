package com.shopsphere.inventory.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Slf4j
public class StockStreamService {

    private final Set<SseEmitter> emitters = ConcurrentHashMap.newKeySet();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(ex -> emitters.remove(emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data(Map.of("timestamp", LocalDateTime.now(), "message", "Stock stream connected")));
        } catch (IOException e) {
            emitters.remove(emitter);
            log.warn("Failed to send initial SSE event", e);
        }

        return emitter;
    }

    public void publish(Map<String, Object> eventPayload) {
        emitters.removeIf(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("stock-event")
                        .data(eventPayload));
                return false;
            } catch (IOException e) {
                return true;
            }
        });
    }
}
