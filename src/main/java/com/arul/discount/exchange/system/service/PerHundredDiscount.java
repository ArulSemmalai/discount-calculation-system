package com.arul.discount.exchange.system.service;

import com.arul.discount.exchange.system.config.DiscountConfig;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class PerHundredDiscount implements DiscountStrategy {

    private final DiscountConfig discountConfig;

    public PerHundredDiscount(DiscountConfig discountConfig) {
        this.discountConfig = discountConfig;
    }

    @Override
    public BigDecimal applyDiscount(BigDecimal amount) {
        log.info("Entered into the method of applying discount to Per-Hundred on the bill: ");
        BigDecimal discount = BigDecimal.valueOf(amount.intValue() / 100).multiply(BigDecimal.valueOf(discountConfig.getPerHundredDiscount()));
        log.info("Calculated amount after applying {} discount is {} ::",discountConfig.getPerHundredDiscount(),discount);
        return amount.subtract(discount);
    }
}
