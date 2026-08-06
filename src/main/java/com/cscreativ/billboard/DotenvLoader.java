package com.cscreativ.billboard;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Charge .env (racine du projet) dans les System properties, avant SpringApplication.run(...).
 *
 * Ni me.paulschwarz:spring-dotenv (SpringApplicationRunListener via META-INF/spring.factories)
 * ni un EnvironmentPostProcessor auto-découvert (META-INF/spring/*.imports) ne s'enregistrent
 * sous cette build de Spring Boot 4.1.0 : les deux mécanismes de découverte automatique ont été
 * vérifiés et n'atteignent jamais l'Environment. Un appel explicite depuis main() ne dépend
 * d'aucune découverte de classpath et fonctionne à coup sûr.
 *
 * Une vraie variable d'environnement système garde toujours la priorité sur .env.
 */
final class DotenvLoader {

    private DotenvLoader() {
    }

    static void load() {
        Path envFile = Path.of(".env");
        if (!Files.isRegularFile(envFile)) {
            return;
        }

        try {
            for (String line : Files.readAllLines(envFile)) {
                String trimmed = line.strip();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int separator = trimmed.indexOf('=');
                if (separator <= 0) {
                    continue;
                }
                String key = trimmed.substring(0, separator).strip();
                String value = trimmed.substring(separator + 1).strip();
                if (System.getenv(key) == null && System.getProperty(key) == null) {
                    System.setProperty(key, value);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible de lire " + envFile, e);
        }
    }
}
