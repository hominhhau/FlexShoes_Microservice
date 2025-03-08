package com.microservice.order_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InvoiceDto {
	Integer invoiceId;
	LocalDate issueDate;
	String receiverNumber;
	String receiverName;
	String receiverAddress;
	String paymentMethod;
	String deliveryMethod;
	String orderStatus;
	double total;
	Integer customerId;
	List<InvoiceDetailDto> invoiceDetails;
}
