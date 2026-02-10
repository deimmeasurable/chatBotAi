package org.example.aichatbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogisticDispatcher {
        // Spring injects the proxy implementation created by LangChain4j
        private final LogisticAssistant logisticAssistant;

        public String handleInquiry(String message) {
            return logisticAssistant.chat(message);
        }
    }


