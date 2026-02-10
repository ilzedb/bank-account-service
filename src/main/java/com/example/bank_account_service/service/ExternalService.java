package com.example.bank_account_service.service;

import com.example.bank_account_service.exception.ExternalServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class ExternalService {
    private final RestClient restClient;
    private static final String[] POSSIBLE_STATUSES = {"/200", "/201", "/200", "/400", "/200", "/403", "/200", "/500", "/200", "/503"};

    public ExternalService(RestClient.Builder builder, @Value("${application.external.url}") String baseUrl){
        this.restClient = builder.baseUrl(baseUrl)
                .defaultHeader("User-Agent", "Java/SpringBoot-RestClient")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    public void logExternalPreDebit(String accountNumber) {
        log.info("Initiating external audit call for account: {}", accountNumber);

        // Randomly pick a status from the array
        int randomIndex = ThreadLocalRandom.current().nextInt(POSSIBLE_STATUSES.length);
        String chosenStatus = POSSIBLE_STATUSES[randomIndex];

        try {
            restClient.get()
                    .uri(chosenStatus)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new ExternalServiceUnavailableException("External Audit System rejected the request: " + response.getStatusCode());
                    })
                    .toBodilessEntity();

            log.info("External audit call successful {}.", accountNumber);
        } catch (Exception e) {
            log.error("External audit call failed: {}", e.getMessage());
            throw new ExternalServiceUnavailableException("Transaction aborted: External audit system is unreachable or denied access.");
        }
    }
}
