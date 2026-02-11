package org.example.aichatbot.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.aichatbot.service.LogisticDispatcher;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/chat")
@Tag(name = "Logistics AI", description = "Chat with Creseada Logistics AI")
public class ChatBotAiController {

    private final LogisticDispatcher logisticDispatcher;


    public ChatBotAiController(LogisticDispatcher logisticDispatcher) {
        this.logisticDispatcher = logisticDispatcher;
    }

    @PostMapping("")
    @Operation(summary = "Chat with the logistics AI")
    public String chat(@RequestBody String message) {
        return logisticDispatcher.handleInquiry(message);
    }
}
