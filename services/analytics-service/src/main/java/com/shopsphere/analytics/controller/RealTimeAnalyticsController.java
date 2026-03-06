package com.shopsphere.analytics.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class RealTimeAnalyticsController {

    // This controller handles incoming messages from WebSocket clients.
    // In many real-time analytics scenarios, data is pushed OUT from the server to
    // clients via SimpMessagingTemplate,
    // rather than clients sending messages IN.

    // As an example, a client could request an initial sync:
    @MessageMapping("/stream/sync")
    public void handleSyncRequest() {
        // Handle client sync request if needed
    }
}
