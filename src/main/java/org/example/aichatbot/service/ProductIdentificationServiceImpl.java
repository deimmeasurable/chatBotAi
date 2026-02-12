package org.example.aichatbot.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import org.example.aichatbot.domain.ProductResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductIdentificationServiceImpl implements ProductIdentificationService {

    private final VisionService visionService;
    private final ProductLookupService productLookupService;
    private final ChatLanguageModel chatModel;

    public ProductResponse identify(MultipartFile image, String barcode, String destinationCountry) {
        try {
            String aiRawResponse;

            // 1. Get the raw product description (from Image or Barcode)
            if (image != null && !image.isEmpty()) {
                aiRawResponse = visionService.detectProduct(image);

                if (aiRawResponse.contains("{")) {
                    return ProductResponse.from(aiRawResponse);
                }
            } else if (barcode != null && !barcode.isBlank()) {
                String productInfo = productLookupService.lookupProduct(barcode);
                String prompt = createPromptForBarCode(productInfo, destinationCountry);
                aiRawResponse = chatModel.chat(prompt);
            } else {
                throw new IllegalArgumentException("Provide image or barcode.");
            }

            // 2. If we reach here, we need to process the description into JSON
            String finalPrompt = createPrompt(aiRawResponse, destinationCountry);
            String finalJson = chatModel.chat(finalPrompt);

            return ProductResponse.from(finalJson);

        } catch (Exception e) {
            throw new RuntimeException("Product identification failed: " + e.getMessage());
        }
    }

    // This is the missing method
    private String createPrompt(String productDescription, String destinationCountry) {
        String countryContext = (destinationCountry != null && !destinationCountry.isBlank())
                ? "for " + destinationCountry + " customs regulations"
                : "general customs guidelines";

        return """
            Based on this product info: %s
            
            Return a JSON object strictly following these EXACT keys:
            "1. Official Product Name"
            "2. Short Description"
            "3. Category"
            "4. International HS Code (6-digit)"
            "5. Country-Specific HS Code (8 or 10 digit)"
            "6. Import Requirements"
            "7. Estimated Duties & VAT"
            "8. Required Certifications"
            "9. AI Confidence Score"
            "10. notes"
            "11. barcode"
            
            Context: %s.
            Return ONLY the JSON. No conversational text.
            """.formatted(productDescription, countryContext);
    }
    private String createPromptForBarCode(String productDescription, String destinationCountry) {
        return """
        Analyze this product: %s
        Return ONLY JSON. Use ONLY these 9 keys:
        "1. Official Product Name", "2. Short Description", "3. Category", 
        "4. International HS Code (6-digit)", "5. Country-Specific HS Code (8 or 10 digit)", 
        "6. Import Requirements", "7. Estimated Duties & VAT", "8. Required Certifications", 
        "9. AI Confidence Score".
        
        Do not add "10. notes" or "11. barcode". Put any extra info inside "7. Estimated Duties & VAT".
        """.formatted(productDescription);
    }
}





