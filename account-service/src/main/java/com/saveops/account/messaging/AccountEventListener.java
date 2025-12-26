package com.saveops.account.messaging;

import com.saveops.account.service.AccountApplicationService;
import com.saveops.common.event.DomainEvent;
import com.saveops.common.event.EventConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class AccountEventListener {
    private static final Logger log = LoggerFactory.getLogger(AccountEventListener.class);
    private final AccountApplicationService accountService;

    public AccountEventListener(AccountApplicationService accountService) {
        this.accountService = accountService;
    }

    @RabbitListener(queues = EventConstants.ACCOUNT_ROUND_UP_QUEUE)
    public void onPurchaseRoundedUp(DomainEvent event) {
        String accountId = String.valueOf(event.payload().get("accountId"));
        BigDecimal roundUpAmount = new BigDecimal(String.valueOf(event.payload().get("roundUpAmount")));
        if (roundUpAmount.signum() <= 0) {
            log.info("Skipping zero round-up event {}", event.eventId());
            return;
        }
        accountService.roundUp(UUID.fromString(accountId), roundUpAmount, event.eventId(), event.correlationId());
    }
}
