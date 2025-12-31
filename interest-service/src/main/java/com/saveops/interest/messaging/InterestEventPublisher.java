package com.saveops.interest.messaging;

import com.saveops.common.event.DomainEvent;
import com.saveops.common.event.EventConstants;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class InterestEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public InterestEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void interestAccrued(String accountId, String correlationId, Map<String, Object> payload) {
        DomainEvent event = new DomainEvent(UUID.randomUUID().toString(), "InterestAccrued", accountId, Instant.now(), correlationId, payload);
        rabbitTemplate.convertAndSend(EventConstants.EXCHANGE, EventConstants.INTEREST_ACCRUED, event);
    }
}

