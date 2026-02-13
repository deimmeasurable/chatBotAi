package org.example.aichatbot.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import org.example.aichatbot.service.LiveCustomsFetcher;
import org.example.aichatbot.service.LogisticAssistant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class LogisticConfig {

    @Value("${GEMINI_API_KEY}")
    private String geminiApiKey;

    @Value("${langchain4j.google-ai-gemini.chat-model.model-name}")
    private String geminiModel;

    private final LiveCustomsFetcher liveCustomsFetcher;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(geminiApiKey)
                .modelName(geminiModel)
                .logRequestsAndResponses(true)
                .build();
    }

    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        return memoryId -> MessageWindowChatMemory.withMaxMessages(10);
    }

    @Bean
    public LogisticAssistant logisticAssistantBean(
            ChatLanguageModel model,
            ChatMemoryProvider chatMemoryProvider
    ) {

        String liveUpdates = String.join("\n", liveCustomsFetcher.fetchLatestUpdates());

        return AiServices.builder(LogisticAssistant.class)
                .chatLanguageModel(model)
                .chatMemoryProvider(chatMemoryProvider)
                .systemMessageProvider(memoryId ->
                        """
                        You are the Creseada Logistics AI.

                        Company Info:
                        Creseada International Limited is a Nigerian logistics company founded in 1985.
                        Headquarters: Matori, Lagos.
                        Services: Air freight, Sea freight, Customs clearing, Import/export logistics.

                        Latest Customs Updates:
                        """ + liveUpdates
                )
                .build();
    }
}



