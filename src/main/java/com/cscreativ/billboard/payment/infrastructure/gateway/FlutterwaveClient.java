package com.cscreativ.billboard.payment.infrastructure.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Client HTTP minimal pour l'API "Standard" de Flutterwave (paiement hébergé) :
 * initialisation (retourne un lien de paiement à ouvrir chez Flutterwave) et vérification
 * a posteriori d'une transaction par son identifiant (source de vérité, jamais le statut
 * renvoyé tel quel par le navigateur ou un webhook non vérifié).
 */
@Component
public class FlutterwaveClient {

    private final String secretKey;
    private final String baseUrl;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FlutterwaveClient(@Value("${flutterwave.secret-key}") String secretKey,
                              @Value("${flutterwave.base-url}") String baseUrl) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
    }

    public String initializePayment(String txRef, BigDecimal amount, String currency, String redirectUrl,
                                     String customerEmail, String customerName, String customerPhone) {
        Map<String, Object> customer = new LinkedHashMap<>();
        customer.put("email", customerEmail);
        customer.put("name", customerName);
        if (customerPhone != null && !customerPhone.isBlank()) {
            customer.put("phonenumber", customerPhone);
        }

        Map<String, Object> customizations = new LinkedHashMap<>();
        customizations.put("title", "Guen's Pub Billboard");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("tx_ref", txRef);
        body.put("amount", amount.toPlainString());
        body.put("currency", currency);
        body.put("redirect_url", redirectUrl);
        body.put("customer", customer);
        body.put("customizations", customizations);

        JsonNode root = post("/payments", body);
        if (!"success".equals(root.path("status").asText())) {
            throw new FlutterwaveException("Échec de l'initialisation du paiement Flutterwave : " + root);
        }
        String link = root.path("data").path("link").asText(null);
        if (link == null) {
            throw new FlutterwaveException("Réponse Flutterwave invalide : lien de paiement manquant");
        }
        return link;
    }

    public FlutterwaveVerificationResult verifyTransaction(String flutterwaveTransactionId) {
        JsonNode root = get("/transactions/" + flutterwaveTransactionId + "/verify");
        JsonNode data = root.path("data");
        boolean successful = "success".equals(root.path("status").asText())
                && "successful".equalsIgnoreCase(data.path("status").asText());
        return new FlutterwaveVerificationResult(
                successful,
                data.path("tx_ref").asText(null),
                data.path("id").asText(null),
                data.has("amount") ? data.path("amount").decimalValue() : null,
                data.path("currency").asText(null)
        );
    }

    private JsonNode post(String path, Map<String, Object> body) {
        try {
            String json = objectMapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .header("Authorization", "Bearer " + secretKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readTree(response.body());
        } catch (IOException e) {
            throw new FlutterwaveException("Erreur de communication avec Flutterwave", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FlutterwaveException("Erreur de communication avec Flutterwave", e);
        }
    }

    private JsonNode get(String path) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .header("Authorization", "Bearer " + secretKey)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readTree(response.body());
        } catch (IOException e) {
            throw new FlutterwaveException("Erreur de communication avec Flutterwave", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FlutterwaveException("Erreur de communication avec Flutterwave", e);
        }
    }
}
