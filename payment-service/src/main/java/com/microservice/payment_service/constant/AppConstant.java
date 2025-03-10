package com.microservice.payment_service.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class AppConstant {
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public abstract class DiscoveredDomainsApi {
        public static final String ORDER_SERVICE_HOST ="http://localhost:8080/api/invoices/findById/";
    }
}
