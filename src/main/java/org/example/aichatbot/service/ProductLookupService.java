package org.example.aichatbot.service;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductLookupService {
    private final RestTemplate restTemplate = new RestTemplate();

    public String lookupProduct(String barcode) {
        log.info("Starting lookup for barcode: {}", barcode);

        // 1. Try General Goods (UPCitemdb)
        String generalInfo = lookupGeneralGoods(barcode);
        if (generalInfo != null) return generalInfo;

        // 2. Try Food Facts (OpenFoodFacts)
        String foodInfo = lookupFoodFacts(barcode);
        if (foodInfo != null) return foodInfo;

        // 3. If both failed, throw a custom exception
        throw new IllegalArgumentException("Product not found: Barcode " + barcode + " does not exist in our databases.");
    }

    private String lookupGeneralGoods(String barcode) {
        try {
            String url = "https://api.upcitemdb.com/prod/trial/lookup?upc=" + barcode;
            JsonNode root = restTemplate.getForEntity(url, JsonNode.class).getBody();

            if (root != null && root.path("items").size() > 0) {
                JsonNode item = root.path("items").get(0);
                return String.format("Product: %s, Brand: %s, Description: %s",
                        item.path("title").asText(),
                        item.path("brand").asText(),
                        item.path("description").asText());
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Barcode {} not found in UPCitemdb. Falling back...", barcode);
        } catch (Exception e) {
            log.error("UPCitemdb API Error: {}", e.getMessage());
            // We do NOT throw here because we want to try the next API (Food Facts)
        }
        return null;
    }

    private String lookupFoodFacts(String barcode) {
        try {
            String url = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";
            JsonNode root = restTemplate.getForEntity(url, JsonNode.class).getBody();

            if (root != null && root.path("status").asInt() == 1) {
                JsonNode p = root.path("product");
                return String.format("Food Product: %s, Brand: %s, Categories: %s",
                        p.path("product_name").asText(),
                        p.path("brands").asText(),
                        p.path("categories").asText());
            }
        } catch (Exception e) {
            log.error("OpenFoodFacts API Error: {}", e.getMessage());
        }
        return null;
    }
}

