package org.example.aichatbot.config;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.example.aichatbot.service.LogisticAssistant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogisticConfig {

    @Value("${langchain4j.google-ai-gemini.chat-model.api-key}")
    private String geminiApiKey;

    @Value("${langchain4j.google-ai-gemini.chat-model.model-name}")
    private String geminiModel;

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
        // Keeps the last 10 messages in memory per user
        return memoryId -> MessageWindowChatMemory.withMaxMessages(10);
    }

    @Bean
    public LogisticAssistant logisticAssistant(
            ChatLanguageModel model,
            ChatMemoryProvider chatMemoryProvider) {

        return AiServices.builder(LogisticAssistant.class)
                .chatLanguageModel(model)
                .contentRetriever(createCreseadaKnowledgeBase())
                .chatMemoryProvider(chatMemoryProvider)
               // .tools(logisticsTools)
                .build();
    }
    String updatedLogisticsText =
            "Logistics Update – April 2026:\n" +
                    "Nigeria Customs Service introduced new documentation requirements " +
                    "for electronics imports. Importers must provide detailed invoices. " +
                    "Tariff rates remain subject to HS code classification.";


    private ContentRetriever createCreseadaKnowledgeBase() {
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();


        ingestor.ingest(Document.from(getCreseadaText()));
        ingestor.ingest(Document.from(updatedLogisticsText));

        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2)
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

}
