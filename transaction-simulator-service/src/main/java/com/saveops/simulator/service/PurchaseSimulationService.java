package com.saveops.simulator.service;

import com.saveops.common.logging.CorrelationId;
import com.saveops.simulator.dto.PurchaseDtos;
import com.saveops.simulator.messaging.PurchaseEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class PurchaseSimulationService {
    private final RoundUpCalculator calculator;
    private final PurchaseEventPublisher publisher;

    public PurchaseSimulationService(RoundUpCalculator calculator, PurchaseEventPublisher publisher) {
        this.calculator = calculator;
        this.publisher = publisher;
    }

    public PurchaseDtos.SimulatePurchaseResponse simulate(PurchaseDtos.SimulatePurchaseRequest request) {
        RoundUpCalculator.Result result = calculator.calculate(request.amount(), request.roundTo());
        String purchaseId = UUID.randomUUID().toString();
        publisher.purchaseRoundedUp(request.accountId(), CorrelationId.currentOrNew(), Map.of(
                "purchaseId", purchaseId,
                "userId", request.userId(),
                "accountId", request.accountId(),
                "amount", request.amount().toPlainString(),
                "roundedAmount", result.roundedAmount().toPlainString(),
                "roundUpAmount", result.roundUpAmount().toPlainString()
        ));
        return new PurchaseDtos.SimulatePurchaseResponse(purchaseId, request.accountId(), request.amount(), result.roundedAmount(), result.roundUpAmount());
    }
}

