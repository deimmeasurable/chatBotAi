package org.example.aichatbot.controller;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.aichatbot.domain.ChatRequestDto;
import org.example.aichatbot.domain.ChatResponseDto;
import org.example.aichatbot.service.LogisticDispatcher;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> chat(@RequestBody ChatRequestDto request) {
        String reply = logisticDispatcher.handleInquiry(request.getMessage());
//        ChatResponseDto response = new ChatResponseDto(reply);
        return ResponseEntity.ok(reply);
    }
    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Creseada Logistics AI is running");
    }
}
