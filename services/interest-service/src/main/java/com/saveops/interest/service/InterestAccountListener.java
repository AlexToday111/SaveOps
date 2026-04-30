package com.saveops.interest.service;

import com.saveops.common.event.DomainEvent;
import com.saveops.common.logging.CorrelationScope;
import com.saveops.interest.config.InterestRabbitConfig;
import com.saveops.interest.entity.TrackedAccountEntity;
import com.saveops.interest.repository.TrackedAccountRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
public class InterestAccountListener {
    private final TrackedAccountRepository repository;

    public InterestAccountListener(TrackedAccountRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = InterestRabbitConfig.ACCOUNT_OPENED_QUEUE)
    @Transactional
    public void onAccountOpened(DomainEvent event) {
        try (CorrelationScope ignored = CorrelationScope.open(event.correlationId())) {
            UUID accountId = UUID.fromString(event.aggregateId());
            if (repository.existsById(accountId)) {
                return;
            }
            repository.save(new TrackedAccountEntity(
                    accountId,
                    String.valueOf(event.payload().get("ownerId")),
                    String.valueOf(event.payload().get("currency")),
                    Instant.now()
            ));
        }
    }
}
