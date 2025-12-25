package com.saveops.goal;

import com.saveops.common.web.CommonWebConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(CommonWebConfig.class)
public class GoalServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoalServiceApplication.class, args);
    }
}

