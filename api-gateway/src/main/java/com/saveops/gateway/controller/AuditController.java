package com.saveops.gateway.controller;

import com.saveops.common.logging.CorrelationId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {
    private final RestTemplate restTemplate;
    private final String auditUrl;

    public AuditController(RestTemplate restTemplate, @Value("${saveops.audit-url}") String auditUrl) {
        this.restTemplate = restTemplate;
        this.auditUrl = auditUrl;
    }

    @GetMapping("/{aggregateId}")
    public List<?> getAudit(@PathVariable String aggregateId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CorrelationId.HEADER, CorrelationId.currentOrNew());
        return restTemplate.exchange(auditUrl + "/internal/audit/" + aggregateId, HttpMethod.GET, new HttpEntity<>(headers), List.class).getBody();
    }
}

