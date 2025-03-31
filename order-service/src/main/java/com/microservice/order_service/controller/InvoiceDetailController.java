package com.microservice.order_service.controller;


import com.microservice.order_service.service.InvoiceDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invoiceDetail")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InvoiceDetailController {
	InvoiceDetailService invoiceDetailService;
	 @DeleteMapping("delete/{detailId}")
	    public ResponseEntity<String> deleteInvoiceDetail(@PathVariable Integer detailId) {
	        invoiceDetailService.deleteInvoiceDetailById(detailId);
	        return ResponseEntity.ok("InvoiceDetail with ID " + detailId + " has been deleted successfully.");
	    }


}
