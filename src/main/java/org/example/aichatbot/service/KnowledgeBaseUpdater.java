package org.example.aichatbot.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseUpdater {

    private final InMemoryEmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;
    private final LiveCustomsFetcher customsFetcher;

    public void ingestLiveCustomsUpdates() {
        List<String> updates = customsFetcher.fetchLatestUpdates();
        if (!updates.isEmpty()) {
            EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                    .embeddingModel(embeddingModel)
                    .embeddingStore(embeddingStore)
                    .build();
            updates.forEach(text -> ingestor.ingest(Document.from(text)));
            log.info("Ingested " + updates.size() + " live updates from customs API.");
        }
    }
}

