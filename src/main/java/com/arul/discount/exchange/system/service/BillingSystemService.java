package com.arul.discount.exchange.system.service;

import com.arul.discount.exchange.system.model.BillDetailsRequest;
import com.arul.discount.exchange.system.model.BillDetailsResponse;

public interface BillingSystemService {
    public BillDetailsResponse calculateAndApplyDiscount(BillDetailsRequest billDetailsRequest) throws Exception;
}
