package com.saveops.account.messaging;

import com.saveops.common.event.DomainEvent;
import com.saveops.common.event.DomainEventFactory;
import com.saveops.common.event.EventConstants;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AccountEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public AccountEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void accountOpened(String accountId, String correlationId, Map<String, Object> payload) {
        publish(EventConstants.ACCOUNT_OPENED, "AccountOpened", accountId, correlationId, payload);
    }

    public void accountClosed(String accountId, String correlationId, Map<String, Object> payload) {
        publish(EventConstants.ACCOUNT_CLOSED, "AccountClosed", accountId, correlationId, payload);
    }

    public void moneyTransferred(String accountId, String correlationId, Map<String, Object> payload) {
        publish(EventConstants.MONEY_TRANSFERRED, "MoneyTransferred", accountId, correlationId, payload);
    }

    private void publish(String routingKey, String eventType, String aggregateId, String correlationId, Map<String, Object> payload) {
        DomainEvent event = DomainEventFactory.create(eventType, aggregateId, correlationId, payload);
        rabbitTemplate.convertAndSend(EventConstants.EXCHANGE, routingKey, event);
    }
}
