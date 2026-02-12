package org.example.aichatbot.service;

import org.example.aichatbot.domain.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProductIdentificationService {

    ProductResponse identify(MultipartFile image,
                             String barcode,
                             String destinationCountry);
}
