package org.example.aichatbot.Scheduler;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.aichatbot.service.KnowledgeBaseUpdater;
import org.example.aichatbot.service.LiveCustomsFetcher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class KnowledgeBaseScheduler {
    private final KnowledgeBaseUpdater updater;

    @Scheduled(fixedRate = 3600000) // every hour
    public void updateKnowledgeBase() {
        updater.ingestLiveCustomsUpdates();
    }
    }

