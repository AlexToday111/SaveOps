package com.saveops.simulator;

import com.saveops.common.event.RabbitEventConfig;
import com.saveops.common.web.CommonWebConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({CommonWebConfig.class, RabbitEventConfig.class})
public class TransactionSimulatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransactionSimulatorApplication.class, args);
    }
}

