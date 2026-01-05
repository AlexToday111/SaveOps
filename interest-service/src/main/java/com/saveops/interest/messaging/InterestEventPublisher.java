package com.saveops.interest.messaging;

import com.saveops.common.event.DomainEvent;
import com.saveops.common.event.DomainEventFactory;
import com.saveops.common.event.EventConstants;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InterestEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public InterestEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void interestAccrued(String accountId, String correlationId, Map<String, Object> payload) {
        DomainEvent event = DomainEventFactory.create("InterestAccrued", accountId, correlationId, payload);
        rabbitTemplate.convertAndSend(EventConstants.EXCHANGE, EventConstants.INTEREST_ACCRUED, event);
    }
}
