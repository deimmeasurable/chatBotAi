package org.example.aichatbot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.aichatbot.service.LogisticDispatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Tag(name = "Logistics AI", description = "Chat with Creseada Logistics AI")
public class ChatBotAiController {
    private final LogisticDispatcher logisticDispatcher;

    @PostMapping("")
    @Operation(summary = "Chat with the logistics AI")
    public String chat(@RequestBody String message) {
        return logisticDispatcher.handleInquiry(message);
    }

}
