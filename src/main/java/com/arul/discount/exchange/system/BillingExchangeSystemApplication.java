package com.arul.discount.exchange.system;

import com.arul.discount.exchange.system.config.DiscountConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableConfigurationProperties(DiscountConfig.class)
@EnableCaching
public class BillingExchangeSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingExchangeSystemApplication.class, args);
	}

}
