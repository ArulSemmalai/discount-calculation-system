package com.arul.discount.exchange.system.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class BillResponse implements Serializable {

    private BigDecimal totalAmount;
    private String currency;

    @Override
    public String toString() {
        return "Total Amount to be paid: " + totalAmount + " " + currency;
    }
}
