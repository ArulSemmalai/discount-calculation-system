package com.arul.discount.exchange.system.serviceImpl;

import com.arul.discount.exchange.system.config.ExchangeRateProperties;
import com.arul.discount.exchange.system.exception.ExchangeRateException;
import com.arul.discount.exchange.system.model.ExchangeRateResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
public class ExchangeRateServiceImpl {

    private final WebClient webClient;
    private final ExchangeRateProperties exchangeRateProperties;

    public ExchangeRateServiceImpl(WebClient.Builder webClientBuilder, ExchangeRateProperties exchangeRateProperties) {
        this.exchangeRateProperties = exchangeRateProperties;

        String baseUrl = String.format("%s/%s/latest",
                exchangeRateProperties.getBaseUrl(),
                exchangeRateProperties.getApiKey());
        System.out.println("baseUrl  ==> "+ baseUrl);
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();

        log.info("Exchange API base URL initialized: {}", baseUrl);
    }

    @CircuitBreaker(name = "exchangeRateService", fallbackMethod = "getDefaultExchangeRate")
    @Retry(name = "exchangeRateService")
    @Cacheable(value = "exchangeRates", key = "#baseCurrency + #targetCurrency")
    public BigDecimal getExchangeRate(String baseCurrency, String targetCurrency) {
        log.info("Fetching exchange rate from '{}' to '{}'", baseCurrency, targetCurrency);

        String uri = "/" + baseCurrency;

        try {
            ExchangeRateResponse response = webClient
                    .get()
                    .uri(uri)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        String message = "Failed to fetch exchange rates. HTTP Status: " + clientResponse.statusCode();
                        log.error(message);
                        return Mono.error(new ExchangeRateException(message));
                    })
                    .bodyToMono(ExchangeRateResponse.class)
                    .block();

            if (response == null || response.getConversionRates() == null) {
                String message = "Empty response received from exchange rate API.";
                log.error(message);
                throw new ExchangeRateException(message);
            }

            BigDecimal exchangeRate = Optional.ofNullable(response.getConversionRates().get(targetCurrency))
                    .orElse(BigDecimal.ZERO);

            log.info("Exchange rate from '{}' to '{}' is: {}", baseCurrency, targetCurrency, exchangeRate);
            return exchangeRate;

        } catch (WebClientResponseException ex) {
            String errorMessage = "Exchange API responded with error: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString();
            log.error(errorMessage, ex);
            throw new ExchangeRateException(errorMessage, ex);

        } catch (Exception ex) {
            String errorMessage = "Unexpected error occurred while fetching exchange rates: " + ex.getMessage();
            log.error(errorMessage, ex);
            throw new ExchangeRateException(errorMessage, ex);
        }
    }

    public BigDecimal getDefaultExchangeRate(String baseCurrency, String targetCurrency, Throwable throwable) {
        log.warn("Fallback method triggered due to: {}", throwable.getMessage());
        return BigDecimal.ZERO;
    }
}
