package com.saveops.simulator.messaging;

import com.saveops.common.event.DomainEvent;
import com.saveops.common.event.DomainEventFactory;
import com.saveops.common.event.EventConstants;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PurchaseEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public PurchaseEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void purchaseRoundedUp(String accountId, String correlationId, Map<String, Object> payload) {
        DomainEvent event = DomainEventFactory.create("PurchaseRoundedUp", accountId, correlationId, payload);
        rabbitTemplate.convertAndSend(EventConstants.EXCHANGE, EventConstants.PURCHASE_ROUNDED_UP, event);
    }
}
