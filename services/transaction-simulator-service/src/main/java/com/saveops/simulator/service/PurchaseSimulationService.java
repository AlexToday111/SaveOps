package com.saveops.simulator.service;

import com.saveops.common.logging.CorrelationId;
import com.saveops.simulator.dto.PurchaseDtos;
import com.saveops.simulator.messaging.PurchaseEventPublisher;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class PurchaseSimulationService {
    private final RoundUpCalculator calculator;
    private final PurchaseEventPublisher publisher;
    private final Counter roundUps;

    public PurchaseSimulationService(RoundUpCalculator calculator, PurchaseEventPublisher publisher, MeterRegistry meterRegistry) {
        this.calculator = calculator;
        this.publisher = publisher;
        this.roundUps = Counter.builder("saveops_purchase_roundups_total").register(meterRegistry);
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
        roundUps.increment();
        return new PurchaseDtos.SimulatePurchaseResponse(purchaseId, request.accountId(), request.amount(), result.roundedAmount(), result.roundUpAmount());
    }
}
