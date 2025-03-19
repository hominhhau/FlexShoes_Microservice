package com.microservice.order_service.repository;

import com.microservice.order_service.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    // Đếm tổng số đơn đặt hàng
    long count();

    // Đếm tổng số đơn theo trạng thái cụ thể
    long countByOrderStatus(String status);

    // Tính tổng số tiền của tất cả hóa đơn
    @Query("SELECT COALESCE(SUM(i.total), 0) FROM Invoice i")
    double sumTotalAmount();

    // Tìm kiếm hóa đơn theo ID, trạng thái đơn hàng và customerId
    @Query("SELECT i FROM Invoice i " +
            "WHERE " +
            "(:id IS NULL OR i.invoiceId = :id) AND " +
            "(:customerId IS NULL OR i.customerId = :customerId) AND " +
            "(:orderStatus IS NULL OR LOWER(i.orderStatus) LIKE LOWER(CONCAT('%', :orderStatus, '%'))) ")
    List<Invoice> searchInvoices(
            @Param("id") Integer id,
            @Param("customerId") Integer customerId,
            @Param("orderStatus") String orderStatus);

}
