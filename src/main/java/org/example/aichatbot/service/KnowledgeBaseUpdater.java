package org.example.aichatbot.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseUpdater {

    private final InMemoryEmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;
    private final LiveCustomsFetcher liveCustomsFetcher;

    /**
     * Ingest live updates safely without blocking application startup.
     */
    @Async
    public void ingestLiveCustomsUpdates() {
        List<String> updates = liveCustomsFetcher.fetchLatestUpdates();
        if (updates.isEmpty()) {
            log.info("No live customs updates to ingest.");
            return;
        }

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        updates.forEach(text -> ingestor.ingest(Document.from(text)));
        log.info("Ingested {} live updates from customs API.", updates.size());
    }
}




