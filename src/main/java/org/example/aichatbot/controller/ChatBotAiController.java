package org.example.aichatbot.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.aichatbot.domain.ChatRequestDto;
import org.example.aichatbot.domain.ProductResponse;
import org.example.aichatbot.service.LogisticDispatcher;
import org.example.aichatbot.service.ProductIdentificationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/")
@Tag(name = "Logistics AI", description = "Chat with Creseada Logistics AI")
public class ChatBotAiController {

    private final LogisticDispatcher logisticDispatcher;
    private final ProductIdentificationService productIdentificationService;


    public ChatBotAiController(LogisticDispatcher logisticDispatcher, ProductIdentificationService productIdentificationService) {
        this.logisticDispatcher = logisticDispatcher;
        this.productIdentificationService = productIdentificationService;
    }

    @PostMapping("chat")
    @Operation(summary = "Chat with the logistics AI")
    public ResponseEntity<String> chat(@RequestBody ChatRequestDto request) {
        String reply = logisticDispatcher.handleInquiry(request.getMessage());
        return ResponseEntity.ok(reply);
    }
    @PostMapping(
            value = "product/identify",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Identify product using image or barcode")
    public ResponseEntity<ProductResponse> identifyProduct(

            @RequestPart(required = false) MultipartFile image,

            @RequestParam(required = false) String barcode,

            @RequestParam() String destinationCountry
    ) {

        ProductResponse response = productIdentificationService.identify(image, barcode, destinationCountry);

        return ResponseEntity.ok(response);
    }
}

