package com.arul.discount.exchange.system;

import com.arul.discount.exchange.system.config.DiscountConfig;
import com.arul.discount.exchange.system.exception.ExchangeRateException;
import com.arul.discount.exchange.system.exception.InvalidBillDetailsException;
import com.arul.discount.exchange.system.model.BillDetailsRequest;
import com.arul.discount.exchange.system.model.BillDetailsResponse;
import com.arul.discount.exchange.system.serviceImpl.BillingSystemServiceImpl;
import com.arul.discount.exchange.system.serviceImpl.ExchangeRateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BillingSystemServiceImplTest {

    @Mock
    private ExchangeRateServiceImpl exchangeRateServiceImpl;

    @Mock
    private DiscountConfig discountConfig;

    private BillingSystemServiceImpl billingSystemService;

    private BillDetailsRequest billDetailsRequest;

    @BeforeEach
    void setUp() {

        when(discountConfig.getEmployee()).thenReturn(30);
        lenient().when(discountConfig.getEmployee()).thenReturn(30);
        lenient().when(discountConfig.getAffiliate()).thenReturn(10);
        lenient().when(discountConfig.getPerHundredDiscount()).thenReturn(5);
        lenient().when(exchangeRateServiceImpl.getExchangeRate("USD", "AED")).thenReturn(new BigDecimal("3.67"));
        //when(exchangeRateService.getExchangeRate("USD", "AED")).thenReturn(new BigDecimal("3.67"));

        billingSystemService = new BillingSystemServiceImpl(discountConfig, exchangeRateServiceImpl);

        billDetailsRequest = new BillDetailsRequest();
        billDetailsRequest.setCustomerTenure(3);
        billDetailsRequest.setOriginalCurrency("USD");
        billDetailsRequest.setTargetCurrency("AED");
        billDetailsRequest.setBillAmount(new BigDecimal("500"));

        billDetailsRequest.setItems(List.of(
                new BillDetailsRequest.Item("electronics", new BigDecimal("200")),
                new BillDetailsRequest.Item("groceries", new BigDecimal("300"))
        ));
    }


    @Test
    void testApplyEmployeeDiscountAndConvertCurrency() throws Exception {
        billDetailsRequest.setUserType("EMPLOYEE");
        BillDetailsResponse response = billingSystemService.calculateAndApplyDiscount(billDetailsRequest);

        assertNotNull(response);
        assertEquals("AED", response.getCurrency());
        assertTrue(response.getTotalAmount().compareTo(BigDecimal.ZERO) > 0);
        verify(exchangeRateServiceImpl, times(1)).getExchangeRate("USD", "AED");
    }
    @Test
    void testApplyAffiliateDiscountAndConvertCurrency() throws Exception {
        billDetailsRequest.setUserType("AFFILIATE");
        BillDetailsResponse response = billingSystemService.calculateAndApplyDiscount(billDetailsRequest);
        assertNotNull(response);
        assertEquals("AED", response.getCurrency());
        assertTrue(response.getTotalAmount().compareTo(BigDecimal.ZERO) > 0);
        verify(exchangeRateServiceImpl, times(1)).getExchangeRate("USD", "AED");
    }

    @Test
    void testThrowExceptionForInvalidCurrencyConversion() {
        billDetailsRequest.setUserType("AFFILIATE");
        when(exchangeRateServiceImpl.getExchangeRate("USD", "AED"))
                .thenThrow(new ExchangeRateException("Failed to fetch exchange rate"));

        assertThrows(ExchangeRateException.class, () -> billingSystemService.calculateAndApplyDiscount(billDetailsRequest));
        verify(exchangeRateServiceImpl, times(1)).getExchangeRate("USD", "AED");
    }

    @Test
    void testApplyCustomerTenureDiscount() throws Exception {
        billDetailsRequest.setUserType("CUSTOMER");
        billDetailsRequest.setCustomerTenure(3);

        BillDetailsResponse response = billingSystemService.calculateAndApplyDiscount(billDetailsRequest);

        assertNotNull(response);
        assertTrue(response.getTotalAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testThrowExceptionForNullBillDetails() {
        Exception exception = assertThrows(InvalidBillDetailsException.class, () -> billingSystemService.calculateAndApplyDiscount(null));
        assertEquals("Bill details cannot be null", exception.getMessage());
    }
    @Test
    void testCalculateAndApplyDiscount_WithApplicableDiscount() throws Exception {

        when(exchangeRateServiceImpl.getExchangeRate("USD", "INR")).thenReturn(BigDecimal.valueOf(75));
        billDetailsRequest.setUserType("EMPLOYEE");
        billDetailsRequest.setTargetCurrency("INR");
        BillDetailsResponse response = billingSystemService.calculateAndApplyDiscount(billDetailsRequest);
        assertNotNull(response);
        assertEquals("INR", response.getCurrency());
        verify(exchangeRateServiceImpl, times(1)).getExchangeRate("USD", "INR");
    }
    @Test
    void testCalculateAndApplyDiscount_GeneralException() throws Exception {
        when(exchangeRateServiceImpl.getExchangeRate(anyString(), anyString())).thenThrow(new RuntimeException("Unexpected error"));
        billDetailsRequest.setUserType("EMPLOYEE");
        Exception exception = assertThrows(Exception.class, () -> {
            billingSystemService.calculateAndApplyDiscount(billDetailsRequest);
        });
        assertTrue(exception.getMessage().contains("Error in calculating billing. Please try again later:"));
    }


}
