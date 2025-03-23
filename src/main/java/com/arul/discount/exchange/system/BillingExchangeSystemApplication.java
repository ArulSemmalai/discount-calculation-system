package com.arul.discount.exchange.system;

import com.arul.discount.exchange.system.config.DiscountConfig;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableConfigurationProperties(DiscountConfig.class)
@EnableCaching
public class BillingExchangeSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingExchangeSystemApplication.class, args);
	}

}
