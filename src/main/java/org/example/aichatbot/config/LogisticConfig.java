package org.example.aichatbot.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.example.aichatbot.service.LiveCustomsFetcher;
import org.example.aichatbot.service.LogisticAssistant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class LogisticConfig {

    @Value("${langchain4j.google-ai-gemini.chat-model.api-key}")
    private String geminiApiKey;

    @Value("${langchain4j.google-ai-gemini.chat-model.model-name}")
    private String geminiModel;

    private final LiveCustomsFetcher liveCustomsFetcher;

    /**
     * Lazy-loaded embedding model to reduce startup memory usage
     */
    @Bean
    @Lazy
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    /**
     * In-memory embedding store
     */
    @Bean
    public InMemoryEmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

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
            ChatLanguageModel chatLanguageModel,
            ChatMemoryProvider chatMemoryProvider,
            EmbeddingModel embeddingModel,
            InMemoryEmbeddingStore<TextSegment> embeddingStore
    ) {
        // Initialize content retriever lazily
        ContentRetriever contentRetriever = createKnowledgeBase(embeddingModel, embeddingStore);

        return AiServices.builder(LogisticAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(chatMemoryProvider)
                .contentRetriever(contentRetriever)
                .build();
    }

    /**
     * Builds the content retriever with static Creseada info + live updates
     */
    private ContentRetriever createKnowledgeBase(EmbeddingModel embeddingModel, InMemoryEmbeddingStore<TextSegment> embeddingStore) {

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        // Static Creseada info
        ingestor.ingest(Document.from(getCreseadaText()));
        ingestor.ingest(Document.from(updatedLogisticsText));

        // Fetch live updates asynchronously in another thread (non-blocking)
        CompletableFuture.runAsync(() -> {
            List<String> liveUpdates = liveCustomsFetcher.fetchLatestUpdates();
            for (String update : liveUpdates) {
                ingestor.ingest(Document.from(update));
            }
        });

        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(5)
                .minScore(0.6)
                .build();
    }

    private String getCreseadaText() {
        return "CRESEADA INTERNATIONAL LIMITED PROFILE\n" +
                "Creseada International Limited is a Nigerian logistics company founded in 1985.\n" +
                "Headquarters: Matori, Lagos, Nigeria.\n" +
                "Core services include:\n" +
                "- Air freight forwarding\n" +
                "- Sea freight forwarding\n" +
                "- Customs clearing and forwarding\n" +
                "- Import and export logistics\n" +
                "- Inland haulage and cargo handling\n" +
                "Creseada supports businesses with end-to-end logistics solutions.\n" +
                "Contact: https://www.creseada.com/contact-us";
    }

    private final String updatedLogisticsText =
            "Logistics Update – April 2026:\n" +
                    "Nigeria Customs Service introduced new documentation requirements " +
                    "for electronics imports. Importers must provide detailed invoices. " +
                    "Tariff rates remain subject to HS code classification.";
}




