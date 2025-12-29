package com.saveops.audit.controller;

import com.saveops.audit.dto.AuditEventResponse;
import com.saveops.audit.service.AuditEventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/audit")
public class AuditController {
    private final AuditEventService service;

    public AuditController(AuditEventService service) {
        this.service = service;
    }

    @GetMapping("/{aggregateId}")
    public List<AuditEventResponse> findByAggregateId(@PathVariable String aggregateId) {
        return service.findByAggregateId(aggregateId);
    }
}

