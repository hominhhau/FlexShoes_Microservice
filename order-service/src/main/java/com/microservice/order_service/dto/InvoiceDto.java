package com.microservice.order_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
	@NotNull(message = "Issue date là bắt buộc")
	private LocalDate issueDate;

	@NotBlank(message = "Receiver number là bắt buộc")
	private String receiverNumber;

	@NotBlank(message = "Receiver name là bắt buộc")
	@Size(max = 105, message = "Receiver name không được vượt quá 105 ký tự")
	private String receiverName;

	@NotBlank(message = "Receiver address là bắt buộc")
	@Size(max = 105, message = "Receiver address không được vượt quá 105 ký tự")
	private String receiverAddress;

	@NotBlank(message = "Payment method là bắt buộc")
	@Size(max = 50, message = "Payment method không được vượt quá 50 ký tự")
	private String paymentMethod;

	@NotBlank(message = "Delivery method là bắt buộc")
	@Size(max = 50, message = "Delivery method không được vượt quá 50 ký tự")
	private String deliveryMethod;

	@NotBlank(message = "Order status là bắt buộc")
	@Size(max = 50, message = "Order status không được vượt quá 50 ký tự")
	private String orderStatus;

	@NotNull(message = "Total là bắt buộc")
	@Min(value = 0, message = "Total phải lớn hơn hoặc bằng 0")
	private double total;

	private Long customerId;
	private CustomerDto customer;
	private List<InvoiceDetailDto> invoiceDetails;
}
