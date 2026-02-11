package org.example.aichatbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogisticDispatcher {
        private final LogisticAssistant logisticAssistant;

        public String handleInquiry(String message) {
            if (message == null || message.isBlank()) {
                throw new IllegalArgumentException("Message cannot be empty.");
            }
            return logisticAssistant.chat(message);
        }
    }


