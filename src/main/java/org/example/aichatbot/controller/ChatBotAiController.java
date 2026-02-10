package org.example.aichatbot.controller;

import lombok.RequiredArgsConstructor;
import org.example.aichatbot.service.LogisticDispatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatBotAiController {
    private final LogisticDispatcher logisticDispatcher;

    @PostMapping("/prompt")
    public String chat(@RequestBody String message) {
        return logisticDispatcher.handleInquiry(message);
    }

}
