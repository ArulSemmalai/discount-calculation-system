package com.arul.discount.exchange.system;

import com.arul.discount.exchange.system.config.DiscountConfig;
import com.arul.discount.exchange.system.exception.ExchangeRateException;
import com.arul.discount.exchange.system.exception.InvalidBillDetailsException;
import com.arul.discount.exchange.system.model.BillDetails;
import com.arul.discount.exchange.system.model.BillResponse;
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

    private BillDetails billDetails;

    @BeforeEach
    void setUp() {

        when(discountConfig.getEmployee()).thenReturn(30);
        lenient().when(discountConfig.getEmployee()).thenReturn(30);
        lenient().when(discountConfig.getAffiliate()).thenReturn(10);
        lenient().when(discountConfig.getPerHundredDiscount()).thenReturn(5);
        lenient().when(exchangeRateServiceImpl.getExchangeRate("USD", "AED")).thenReturn(new BigDecimal("3.67"));
        //when(exchangeRateService.getExchangeRate("USD", "AED")).thenReturn(new BigDecimal("3.67"));

        billingSystemService = new BillingSystemServiceImpl(discountConfig, exchangeRateServiceImpl);

        billDetails = new BillDetails();
        billDetails.setCustomerTenure(3);
        billDetails.setOriginalCurrency("USD");
        billDetails.setTargetCurrency("AED");
        billDetails.setBillAmount(new BigDecimal("500"));

        billDetails.setItems(List.of(
                new BillDetails.Item("electronics", new BigDecimal("200")),
                new BillDetails.Item("groceries", new BigDecimal("300"))
        ));
    }


    @Test
    void testApplyEmployeeDiscountAndConvertCurrency() throws Exception {
        billDetails.setUserType("EMPLOYEE");
        BillResponse response = billingSystemService.calculateAndApplyDiscount(billDetails);

        assertNotNull(response);
        assertEquals("AED", response.getCurrency());
        assertTrue(response.getTotalAmount().compareTo(BigDecimal.ZERO) > 0);
        verify(exchangeRateServiceImpl, times(1)).getExchangeRate("USD", "AED");
    }
    @Test
    void testApplyAffiliateDiscountAndConvertCurrency() throws Exception {
        billDetails.setUserType("AFFILIATE");
        BillResponse response = billingSystemService.calculateAndApplyDiscount(billDetails);
        assertNotNull(response);
        assertEquals("AED", response.getCurrency());
        assertTrue(response.getTotalAmount().compareTo(BigDecimal.ZERO) > 0);
        verify(exchangeRateServiceImpl, times(1)).getExchangeRate("USD", "AED");
    }

    @Test
    void testThrowExceptionForInvalidCurrencyConversion() {
        billDetails.setUserType("AFFILIATE");
        when(exchangeRateServiceImpl.getExchangeRate("USD", "AED"))
                .thenThrow(new ExchangeRateException("Failed to fetch exchange rate"));

        assertThrows(ExchangeRateException.class, () -> billingSystemService.calculateAndApplyDiscount(billDetails));
        verify(exchangeRateServiceImpl, times(1)).getExchangeRate("USD", "AED");
    }

    @Test
    void testApplyCustomerTenureDiscount() throws Exception {
        billDetails.setUserType("CUSTOMER");
        billDetails.setCustomerTenure(3);

        BillResponse response = billingSystemService.calculateAndApplyDiscount(billDetails);

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
        billDetails.setUserType("EMPLOYEE");
        billDetails.setTargetCurrency("INR");
        BillResponse response = billingSystemService.calculateAndApplyDiscount(billDetails);
        assertNotNull(response);
        assertEquals("INR", response.getCurrency());
        verify(exchangeRateServiceImpl, times(1)).getExchangeRate("USD", "INR");
    }
    @Test
    void testCalculateAndApplyDiscount_GeneralException() throws Exception {
        when(exchangeRateServiceImpl.getExchangeRate(anyString(), anyString())).thenThrow(new RuntimeException("Unexpected error"));
        billDetails.setUserType("EMPLOYEE");
        Exception exception = assertThrows(Exception.class, () -> {
            billingSystemService.calculateAndApplyDiscount(billDetails);
        });
        assertTrue(exception.getMessage().contains("Error in calculating billing. Please try again later:"));
    }


}
