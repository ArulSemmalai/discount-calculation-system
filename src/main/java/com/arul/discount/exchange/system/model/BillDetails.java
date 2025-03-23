package com.arul.discount.exchange.system.model;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Validated
public class BillDetails implements Serializable {

    private static final long serialVersionUID = 1L;
    @NonNull
    private BigDecimal billAmount;
    @NonNull
    private String userType;
    @NonNull
    private int customerTenure;
    @NonNull
    private String originalCurrency;
    @NonNull// fixed typo
    private String targetCurrency;
    @NotBlank
    private List<Item> items;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class Item implements Serializable {

        private static final long serialVersionUID = 1L;

        private String category;
        private BigDecimal price;
    }
}
