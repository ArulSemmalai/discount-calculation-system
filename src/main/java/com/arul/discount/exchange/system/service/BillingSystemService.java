package com.arul.discount.exchange.system.service;

import com.arul.discount.exchange.system.model.BillDetails;
import com.arul.discount.exchange.system.model.BillResponse;

public interface BillingSystemService {
    public BillResponse calculateAndApplyDiscount(BillDetails billDetails) throws Exception;
}
