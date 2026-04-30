package com.saveops.simulator.controller;

import com.saveops.simulator.dto.PurchaseDtos;
import com.saveops.simulator.service.PurchaseSimulationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/purchases")
public class PurchaseSimulationController {
    private final PurchaseSimulationService service;

    public PurchaseSimulationController(PurchaseSimulationService service) {
        this.service = service;
    }

    @PostMapping("/simulate")
    public PurchaseDtos.SimulatePurchaseResponse simulate(@Valid @RequestBody PurchaseDtos.SimulatePurchaseRequest request) {
        return service.simulate(request);
    }
}

