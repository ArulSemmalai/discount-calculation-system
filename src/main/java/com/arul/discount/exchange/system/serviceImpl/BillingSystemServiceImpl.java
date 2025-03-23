package com.arul.discount.exchange.system.serviceImpl;

import com.arul.discount.exchange.system.config.DiscountConfig;
import com.arul.discount.exchange.system.exception.ExchangeRateException;
import com.arul.discount.exchange.system.exception.InvalidBillDetailsException;
import com.arul.discount.exchange.system.model.BillDetailsRequest;
import com.arul.discount.exchange.system.model.BillDetailsResponse;
import com.arul.discount.exchange.system.service.*;
import com.arul.discount.exchange.system.utils.UserType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
@Slf4j
public class BillingSystemServiceImpl implements BillingSystemService {

    private ExchangeRateServiceImpl exchangeRateServiceImpl;
    private final Map<UserType, DiscountStrategy> discountStrategies;
    private final PerHundredDiscount perHundredDiscount;
    private final DiscountConfig discountConfig;

    public BillingSystemServiceImpl(DiscountConfig discountConfig, ExchangeRateServiceImpl exchangeRateServiceImpl) {
        this.discountConfig = discountConfig;
        this.exchangeRateServiceImpl = exchangeRateServiceImpl;
        this.discountStrategies = Map.of(
                UserType.EMPLOYEE, new EmployeeDiscount(discountConfig),
                UserType.AFFILIATE, new AffiliateDiscount(discountConfig)
        );
        this.perHundredDiscount = new PerHundredDiscount(discountConfig);
    }


    @Override
    public BillDetailsResponse calculateAndApplyDiscount(BillDetailsRequest billDetailsRequest) throws Exception {
        log.info("Entered Calculate Billing method in service class");

        if (billDetailsRequest == null) {
            throw new InvalidBillDetailsException("Bill details cannot be null");
        }

        BigDecimal nonGroceryAmount = calculateNonGroceryAmount(billDetailsRequest);
        BigDecimal groceryAmount = billDetailsRequest.getBillAmount().subtract(nonGroceryAmount);

        log.info("Total amount: {}", billDetailsRequest.getBillAmount());
        log.info("Total calculated amount excluding groceries: {}", nonGroceryAmount);
        log.info("Total calculated amount for groceries: {}", groceryAmount);

        try {
            UserType userType = UserType.fromString(billDetailsRequest.getUserType());

            DiscountStrategy applicableDiscount = getApplicableDiscountStrategy(userType, billDetailsRequest.getCustomerTenure());
            if (applicableDiscount != null) {
                log.info("Applying discount strategy for user type: {}", userType);
                nonGroceryAmount = applicableDiscount.applyDiscount(nonGroceryAmount);
                log.info("Amount after applying discount: {}", nonGroceryAmount);
            }
            //Apply $5 discount applying to nongrocery and grocery total amount
            BigDecimal finalBillAmount = perHundredDiscount.applyDiscount(nonGroceryAmount.add(groceryAmount));
            log.info("Final amount after per-hundred discount on Grocery + Non-Grocery: {}", finalBillAmount);

            BigDecimal targetCurrencyRate = exchangeRateServiceImpl.getExchangeRate(billDetailsRequest.getOriginalCurrency(), billDetailsRequest.getTargetCurrency());
            log.info("Target Currency: {} Rate: {}", billDetailsRequest.getTargetCurrency(), targetCurrencyRate);

            BigDecimal finalPayableAmount = finalBillAmount.multiply(targetCurrencyRate);

            log.info("Final Payment amount converting to target currency rate : {}", finalPayableAmount);
            return new BillDetailsResponse(finalPayableAmount.setScale(2, RoundingMode.HALF_DOWN), billDetailsRequest.getTargetCurrency());

        } catch (ExchangeRateException ex) {
            log.error("Exchange API error: {}", ex.getMessage(), ex);
            throw new ExchangeRateException("Exchange API error: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Exception occurred while calculating billing: {}", ex.getMessage(), ex);
            throw new Exception("Error in calculating billing. Please try again later: " + ex.getMessage(), ex);
        }
    }

    /**
     * Determines the applicable discount strategy based on user type and tenure.
     */
    private DiscountStrategy getApplicableDiscountStrategy(UserType userType, int customerTenure) {
        if (userType == UserType.CUSTOMER && customerTenure >= 2) {
            return new CustomerDiscount(discountConfig);
        }
        return discountStrategies.get(userType);
    }

    /**
     * Calculates the total non-grocery amount from the bill.
     */
    private BigDecimal calculateNonGroceryAmount(BillDetailsRequest billDetailsRequest) {
        return billDetailsRequest.getItems().stream()
                .filter(item -> !"groceries".equalsIgnoreCase(item.getCategory()))
                .map(BillDetailsRequest.Item::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
