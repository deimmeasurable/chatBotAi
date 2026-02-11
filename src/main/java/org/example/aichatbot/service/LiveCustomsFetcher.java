package org.example.aichatbot.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class LiveCustomsFetcher {

    private final RestTemplate restTemplate;

    @Value("${logistics.customs.api-url}")
    private String customsApiUrl;

    public List<String> fetchLatestUpdates() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(customsApiUrl, String.class);
            String body = response.getBody();

            if (body == null || body.isEmpty()) {
                log.warn("Customs API returned empty response");
                return Collections.emptyList();
            }

            String contentType = response.getHeaders().getContentType() != null ?
                    response.getHeaders().getContentType().toString() : "";

            // If JSON array, parse it
            if (contentType.contains("application/json") || body.trim().startsWith("[")) {
                try {
                    String[] updates = restTemplate.getForObject(customsApiUrl, String[].class);
                    return updates != null ? Arrays.asList(updates) : Collections.emptyList();
                } catch (Exception e) {
                    log.warn("Failed parsing JSON, falling back to HTML parser: {}", e.getMessage());
                }
            }

            // Otherwise, parse HTML
            return parseHtmlUpdates(body);

        } catch (Exception e) {
            log.error("Failed to fetch customs updates: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<String> parseHtmlUpdates(String html) {
        Document doc = Jsoup.parse(html);

        // Adjust selector to match real page structure
        Elements updates = doc.select("div.update");

        if (updates.isEmpty()) {
            log.warn("No updates found in HTML");
            return Collections.emptyList();
        }

        return updates.stream()
                .map(Element::text)
                .collect(Collectors.toList());
    }
}






