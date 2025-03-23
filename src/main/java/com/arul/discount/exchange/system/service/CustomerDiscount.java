package com.arul.discount.exchange.system.service;

import com.arul.discount.exchange.system.config.DiscountConfig;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class CustomerDiscount implements DiscountStrategy{

    private final DiscountConfig discountConfig;

    public CustomerDiscount(DiscountConfig discountConfig) {
        this.discountConfig = discountConfig;
    }

    @Override
    public BigDecimal applyDiscount(BigDecimal amount) {
        log.info("Entered into the method of applying discount to LongTerm Customer: ");
        BigDecimal custDiscount = amount.subtract(amount.multiply(BigDecimal.valueOf(discountConfig.getLongTermCustomer())).divide(BigDecimal.valueOf(100)));
        log.info("Calculated amount after applying {} discount is {} ::",discountConfig.getLongTermCustomer(),custDiscount);
        return custDiscount;
    }
}
