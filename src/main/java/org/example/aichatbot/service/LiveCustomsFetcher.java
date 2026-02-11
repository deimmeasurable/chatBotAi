package org.example.aichatbot.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LiveCustomsFetcher {

    private final RestTemplate restTemplate;

    @Value("${logistics.customs.api-url}")
    private String customsApiUrl;

    public List<String> fetchLatestUpdates() {
        try {
            // Fetch as plain string
            String response = restTemplate.getForObject(customsApiUrl, String.class);

            if (response != null && !response.isEmpty()) {
                // Split lines or paragraphs into list items
                return Arrays.asList(response.split("\n"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}



