package com.cscreativ.billboard;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class BillboardApplication {
    public static void main(String[] args) {
        // 1. Charger .env (voir DotenvLoader : les mécanismes d'auto-intégration
        // de spring-dotenv / EnvironmentPostProcessor ne fonctionnent pas sous Boot 4.1.0)
        DotenvLoader.load();
        // 2. Démarrer l'application Spring Boot
        SpringApplication.run(BillboardApplication.class, args);
    }
}
