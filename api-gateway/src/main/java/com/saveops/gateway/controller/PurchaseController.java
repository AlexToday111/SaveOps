package com.saveops.gateway.controller;

import com.saveops.common.logging.CorrelationId;
import com.saveops.gateway.dto.PurchaseDtos;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {
    private final RestTemplate restTemplate;
    private final String simulatorUrl;

    public PurchaseController(RestTemplate restTemplate, @Value("${saveops.simulator-url}") String simulatorUrl) {
        this.restTemplate = restTemplate;
        this.simulatorUrl = simulatorUrl;
    }

    @PostMapping("/simulate")
    public Map<?, ?> simulate(@Valid @RequestBody PurchaseDtos.SimulatePurchaseRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CorrelationId.HEADER, CorrelationId.currentOrNew());
        try {
            return restTemplate.postForObject(simulatorUrl + "/internal/purchases/simulate", new HttpEntity<>(request, headers), Map.class);
        } catch (RestClientException ex) {
            return Map.of("status", "accepted_degraded", "message", "Purchase simulation service is temporarily unavailable");
        }
    }
}
