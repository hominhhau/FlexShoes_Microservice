package com.microservice.order_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "INVOICE")
public class Invoice implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INVOICE_ID", updatable = false, insertable = false)
    private Integer invoiceId;

    @Column(name = "ISSUE_DATE")
    private LocalDate issueDate;

    @Column(name = "RECEIVER_NUMBER", length = 12)
    private String receiverNumber;

    @Column(name = "RECEIVER_NAME", length = 105)
    private String receiverName;

    @Column(name = "RECEIVER_ADDRESS", length = 105)
    private String receiverAddress;

    @Column(name = "PAYMENT_METHOD", length = 50)
    private String paymentMethod;

    @Column(name = "DELIVERY_METHOD", length = 50)
    private String deliveryMethod;

    @Column(name = "ORDER_STATUS", length = 50)
    private String orderStatus;

    @Column(name = "TOTAL")
    private double total;

    // Chỉ lưu customerId, không ánh xạ trực tiếp Customer
    @Column(name = "CUSTOMER_ID", nullable = false)
    private Integer customerId;

    // Một đơn hàng có nhiều chi tiết hóa đơn
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InvoiceDetail> invoiceDetails;
}