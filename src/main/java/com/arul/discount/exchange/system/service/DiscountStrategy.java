package com.arul.discount.exchange.system.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;


public interface DiscountStrategy {
    BigDecimal applyDiscount(BigDecimal amount);
}
