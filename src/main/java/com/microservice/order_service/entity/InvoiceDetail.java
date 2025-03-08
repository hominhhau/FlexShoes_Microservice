package com.microservice.order_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "INVOICE_DETAIL")
public class InvoiceDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "DETAIL_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer detailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVOICE_ID", nullable = false)
    private Invoice invoice;

    // Chỉ lưu productId, không ánh xạ trực tiếp Product
    @Column(name = "PRODUCT_ID", nullable = false)
    private Integer productId;

    @Column(name = "QUANTITY")
    private int quantity;
//

}
