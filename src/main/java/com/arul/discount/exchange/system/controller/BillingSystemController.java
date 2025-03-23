package com.arul.discount.exchange.system.controller;

import com.arul.discount.exchange.system.model.BillDetailsRequest;
import com.arul.discount.exchange.system.service.BillingSystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
public class BillingSystemController {

    private final BillingSystemService billingSystemService;

    public BillingSystemController(BillingSystemService billingSystemService) {
        this.billingSystemService = billingSystemService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<?> calculateBilling(@RequestBody BillDetailsRequest billDetailsRequest) throws Exception {
       return ResponseEntity.ok(billingSystemService.calculateAndApplyDiscount(billDetailsRequest).toString());
    }
    @GetMapping("/test")
    public String testAPi(){
        return "api is working";
    }
}
