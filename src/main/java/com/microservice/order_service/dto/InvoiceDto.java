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
	private Integer invoiceId;
	private LocalDate issueDate;
	private String receiverNumber;
	private String receiverName;
	private String receiverAddress;
	private String paymentMethod;
	private String deliveryMethod;
	private String orderStatus;
	private double total;
	private Integer customerId;
	private CustomerDto customer; // Thêm thuộc tính CustomerDto
	private List<InvoiceDetailDto> invoiceDetails;
}
