package org.example.aichatbot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResponse {

    @JsonProperty("1. Official Product Name")
    private String official_product_name;

    @JsonProperty("2. Short Description")
    private String short_description;

    @JsonProperty("3. Category")
    private String category;

    @JsonProperty("4. International HS Code (6-digit)")
    private String international_hs_code;

    @JsonProperty("5. Country-Specific HS Code (8 or 10 digit)")
    private String country_specific_hs_code;

    // CHANGED TO JsonNode to handle both Strings and Lists
    @JsonProperty("6. Import Requirements")
    private JsonNode import_requirements;

    // CHANGED TO JsonNode to handle both Strings and Objects
    @JsonProperty("7. Estimated Duties & VAT")
    private JsonNode estimated_duties_vat;

    // CHANGED TO JsonNode
    @JsonProperty("8. Required Certifications")
    private JsonNode required_certifications;

    @JsonProperty("9. AI Confidence Score")
    private String ai_confidence_score;

    @JsonProperty("10. notes")
    private String notes;

    @JsonProperty("11. barcode")
    private String barcode_from_ai;




    public static ProductResponse from(String aiRawResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // Use a more robust regex to strip markdown blocks
            String cleaned = aiRawResponse.replaceAll("(?s)```json(.*?)```", "$1").trim();

            // Backup: if regex fails, use your brace logic
            if (!cleaned.startsWith("{")) {
                int firstBrace = cleaned.indexOf("{");
                int lastBrace = cleaned.lastIndexOf("}");
                if (firstBrace != -1 && lastBrace != -1) {
                    cleaned = cleaned.substring(firstBrace, lastBrace + 1);
                }
            }

            return mapper.readValue(cleaned, ProductResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response. Check field types!", e);
        }
    }


}
