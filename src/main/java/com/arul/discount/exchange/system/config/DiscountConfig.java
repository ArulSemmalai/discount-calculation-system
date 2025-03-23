package com.arul.discount.exchange.system.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "discounts")
public class DiscountConfig {
    private int employee;
    private int affiliate;
    private int longTermCustomer;
    private int perHundredDiscount;
    private List<String> excludeCategories;
}
