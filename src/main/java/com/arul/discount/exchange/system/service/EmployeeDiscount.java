package com.arul.discount.exchange.system.service;

import com.arul.discount.exchange.system.config.DiscountConfig;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class EmployeeDiscount implements DiscountStrategy{

    private final DiscountConfig discountConfig;

    public EmployeeDiscount(DiscountConfig discountConfig) {
        this.discountConfig = discountConfig;
    }

    @Override
    public BigDecimal applyDiscount(BigDecimal amount) {
        log.info("Entered into the method of applying discount to employee: ");
        BigDecimal empDiscount = amount.subtract(amount.multiply(BigDecimal.valueOf(discountConfig.getEmployee())).divide(BigDecimal.valueOf(100)));
        log.info("Calculated amount after applying {} discount is {} ::",discountConfig.getEmployee(),empDiscount);
        return empDiscount;

    }
}
