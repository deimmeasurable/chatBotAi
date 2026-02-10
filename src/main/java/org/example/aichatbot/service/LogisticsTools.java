package org.example.aichatbot.service;


import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;

import org.example.aichatbot.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogisticsTools {

    @Autowired
    private final ShipmentRepository shipmentRepo;

    @Tool("Look up the real-time status of a shipment using its tracking number")
    public String getShipmentStatus(String trackingNumber) {
        return shipmentRepo.findByTrackingNumber(trackingNumber)
                .map(s -> String.format("Shipment %s is currently '%s' in %s. Estimated delivery: %s.",
                        s.getTrackingNumber(), s.getStatus(), s.getCurrentCity(), s.getEstimatedDelivery()))
                .orElse("I'm sorry, I couldn't find a shipment with that tracking number.");
    }
}
