package com.saveops.audit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saveops.audit.dto.AuditEventResponse;
import com.saveops.audit.entity.AuditEventEntity;
import com.saveops.audit.repository.AuditEventRepository;
import com.saveops.common.event.DomainEvent;
import com.saveops.common.event.EventConstants;
import com.saveops.common.logging.CorrelationScope;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class AuditEventService {
    private final AuditEventRepository repository;
    private final ObjectMapper objectMapper;

    public AuditEventService(AuditEventRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = EventConstants.AUDIT_QUEUE)
    @Transactional
    public void save(DomainEvent event) throws JsonProcessingException {
        try (CorrelationScope ignored = CorrelationScope.open(event.correlationId())) {
            if (repository.existsById(event.eventId())) {
                return;
            }
            repository.save(new AuditEventEntity(
                    event.eventId(),
                    event.eventType(),
                    event.aggregateId(),
                    event.occurredAt(),
                    event.correlationId(),
                    objectMapper.writeValueAsString(event.payload()),
                    Instant.now()
            ));
        }
    }

    @Transactional(readOnly = true)
    public List<AuditEventResponse> findByAggregateId(String aggregateId) {
        return repository.findByAggregateIdOrderByOccurredAtAsc(aggregateId).stream()
                .map(event -> new AuditEventResponse(
                        event.getEventId(),
                        event.getEventType(),
                        event.getAggregateId(),
                        event.getOccurredAt(),
                        event.getCorrelationId(),
                        event.getPayload(),
                        event.getSavedAt()
                ))
                .toList();
    }
}
