package com.arul.discount.exchange.system.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;


    public class ExchangeRateResponse implements Serializable {

        private String response;

        @Getter
        @Setter
        @JsonProperty("conversion_rates")
        private Map<String, BigDecimal> conversionRates;

        public String getResult() {
            return response;
        }

        public void setResult(String result) {
            this.response = result;
        }

    }
