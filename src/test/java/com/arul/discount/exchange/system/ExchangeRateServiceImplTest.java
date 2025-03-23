package com.arul.discount.exchange.system;

import com.arul.discount.exchange.system.config.ExchangeRateProperties;
import com.arul.discount.exchange.system.exception.ExchangeRateException;
import com.arul.discount.exchange.system.model.ExchangeRateResponse;
import com.arul.discount.exchange.system.serviceImpl.ExchangeRateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceImplTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    ExchangeRateServiceImpl exchangeRateService;


    @BeforeEach
    void setUp() {

        ExchangeRateProperties rateProperties = new ExchangeRateProperties();
        rateProperties.setBaseUrl("https://v6.exchangerate-api.com/v6");
        rateProperties.setApiKey("5d34caa6708dab82f5c65af7");

        // Mock WebClient.Builder
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        exchangeRateService = new ExchangeRateServiceImpl(webClientBuilder,rateProperties);
    }

    @Test
    void testGetExchangeRate_Success() {

        // Mock WebClient
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        ExchangeRateResponse response = new ExchangeRateResponse();
        Map<String, BigDecimal> conversionRates = new HashMap<>();
        conversionRates.put("USD", BigDecimal.valueOf(1.0));
        conversionRates.put("EUR", BigDecimal.valueOf(0.85));
        response.setConversionRates(conversionRates);

        when(responseSpec.bodyToMono(ExchangeRateResponse.class)).thenReturn(Mono.just(response));

        BigDecimal exchangeRate = exchangeRateService.getExchangeRate("USD", "EUR");
        assertNotNull(exchangeRate, "Exchange rate should not be null");
        assertEquals(BigDecimal.valueOf(0.85), exchangeRate, "Exchange rate from USD to EUR should be 0.85");
    }
    @Test
    void testGetExchangeRate_EmptyResponse() {
        // Mock WebClient
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        ExchangeRateResponse response = new ExchangeRateResponse();
        when(responseSpec.bodyToMono(ExchangeRateResponse.class)).thenReturn(Mono.just(response));
        ExchangeRateException exception = assertThrows(ExchangeRateException.class, () ->
                exchangeRateService.getExchangeRate("USD", "INR")
        );
        assertTrue(exception.getMessage().contains("Empty response received from exchange rate API."));
    }

    @Test
    void testGetExchangeRate_WebClientException() {
        // Mock WebClient
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(ExchangeRateResponse.class)).thenThrow(
                new WebClientResponseException(500, "Internal Server Error", null, null, null)
        );
        ExchangeRateException exception = assertThrows(ExchangeRateException.class, () ->
                exchangeRateService.getExchangeRate("USD", "INR")
        );

        assertTrue(exception.getMessage().contains("Exchange API responded with error"));
    }
    @Test
    void testGetExchangeRate_GeneralException() {

        when(webClient.get()).thenThrow(new RuntimeException("Unexpected error"));
        ExchangeRateException exception = assertThrows(ExchangeRateException.class, () ->
                exchangeRateService.getExchangeRate("USD", "INR")
        );
        assertTrue(exception.getMessage().contains("Unexpected error occurred"));
    }
    @Test
    void testGetDefaultExchangeRate() {
        BigDecimal fallbackRate = exchangeRateService.getDefaultExchangeRate("USD", "INR", new Exception("Test Exception"));
        assertEquals(BigDecimal.ZERO, fallbackRate);
    }

    @Test
    void testGetExchangeRate_ApiError() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        ClientResponse mockErrorResponse = ClientResponse
                .create(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal Server Error")
                .build();

        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ExchangeRateResponse.class)).thenReturn(Mono.error(new ExchangeRateException("Failed to fetch exchange rates")));

        ExchangeRateException thrown = assertThrows(ExchangeRateException.class, () -> {
            exchangeRateService.getExchangeRate("USD", "INR");
        });

        assertTrue(thrown.getMessage().contains("Failed to fetch exchange rates"));
    }
}