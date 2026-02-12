package org.example.aichatbot.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class VisionService {

    @Value("${langchain4j.google-ai-gemini.chat-model.api-key}")
    private String apiKey;
    @Value("gemini.api.url")
    private  String API_URL ;



    // Hardcoding the base URL ensures you never get "URI is not absolute"
    // if the properties file fails to load.
    private final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    public String detectProduct(MultipartFile file) {
        try {
            // Verify the API Key is loaded
            if (apiKey == null || apiKey.isEmpty()) {
                throw new RuntimeException("API Key is missing from application.properties");
            }

            RestTemplate restTemplate = new RestTemplate();
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

            // Correct JSON structure for Gemini 1.5
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(Map.of(
                            "parts", List.of(
                                    Map.of("text", "Identify this product from its packaging. Provide the brand and model number."),
                                    Map.of("inline_data", Map.of(
                                            "mime_type", "image/jpeg",
                                            "data", base64Image
                                    ))
                            )
                    ))
            );

            // The URL is built dynamically here
            String fullUrl = BASE_URL + apiKey;

            ResponseEntity<JsonNode> response = restTemplate.postForEntity(fullUrl, requestBody, JsonNode.class);

            // Safe extraction of the result
            return response.getBody()
                    .path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

        } catch (Exception e) {
            // Log the actual error to see why the URI or request failed
            System.err.println("Gemini Error: " + e.getMessage());
            throw new RuntimeException("Gemini Vision failed: " + e.getMessage());
        }
    }
}










