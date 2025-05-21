package com.microservice.order_service.repository;

import com.microservice.order_service.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
            @Param("customerId") Long customerId,
            @Param("orderStatus") String orderStatus);
    List<Invoice> findByCustomerId(Long customerId);

    @Query("SELECT MONTH(i.issueDate), COUNT(i) FROM Invoice i WHERE YEAR(i.issueDate) = :year GROUP BY MONTH(i.issueDate)")
    List<Object[]> countOrdersByMonthInYear(@Param("year") int year);

    @Query("SELECT MONTH(i.issueDate), COALESCE(SUM(i.total), 0) FROM Invoice i WHERE YEAR(i.issueDate) = :year GROUP BY MONTH(i.issueDate)")
    List<Object[]> sumRevenueByMonthInYear(@Param("year") int year);

//    @Query("SELECT MONTH(i.issueDate), COUNT(i) FROM Invoice i WHERE i.issueDate BETWEEN :startDate AND :endDate GROUP BY MONTH(i.issueDate)")
//    List<Object[]> countOrdersByMonthInRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
//
//    @Query("SELECT MONTH(i.issueDate), COALESCE(SUM(i.total), 0) FROM Invoice i WHERE i.issueDate BETWEEN :startDate AND :endDate GROUP BY MONTH(i.issueDate)")
//    List<Object[]> sumRevenueByMonthInRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    ////    @Query("SELECT i FROM Invoice i WHERE (:id IS NULL OR i.invoiceId = :id) AND (:orderStatus IS NULL OR i.orderStatus = :orderStatus)")
    ////    List<Invoice> searchInvoices(@Param("id") Integer id, @Param("customerName") String customerName, @Param("orderStatus") String orderStatus);
//
//    long countByIssueDateBetween(LocalDate startDate, LocalDate endDate);
//
//    long countByOrderStatusAndIssueDateBetween(String orderStatus, LocalDate startDate, LocalDate endDate);
//
//    @Query("SELECT COALESCE(SUM(i.total), 0) FROM Invoice i WHERE i.issueDate BETWEEN :startDate AND :endDate")
//    double sumTotalAmountByIssueDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Count orders by month in a date range
    @Query("SELECT MONTH(i.issueDate), COUNT(i) FROM Invoice i WHERE i.issueDate BETWEEN :startDate AND :endDate GROUP BY MONTH(i.issueDate)")
    List<Object[]> countOrdersByMonthInRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Sum revenue by month in a date range
    @Query("SELECT MONTH(i.issueDate), COALESCE(SUM(i.total), 0) FROM Invoice i WHERE i.issueDate BETWEEN :startDate AND :endDate GROUP BY MONTH(i.issueDate)")
    List<Object[]> sumRevenueByMonthInRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Count orders between dates
    long countByIssueDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Count orders by status and date range
    long countByOrderStatusAndIssueDateBetween(String orderStatus, LocalDateTime startDate, LocalDateTime endDate);

    // Sum total amount between dates
    @Query("SELECT COALESCE(SUM(i.total), 0) FROM Invoice i WHERE i.issueDate BETWEEN :startDate AND :endDate")
    double sumTotalAmountByIssueDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


    @Query("SELECT YEAR(i.issueDate) AS year, COUNT(i) FROM Invoice i WHERE i.issueDate BETWEEN :startDate AND :endDate GROUP BY YEAR(i.issueDate)")
    List<Object[]> countOrdersByYears(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT YEAR(i.issueDate) AS year, COALESCE(SUM(i.total), 0) FROM Invoice i WHERE i.issueDate BETWEEN :startDate AND :endDate GROUP BY YEAR(i.issueDate)")
    List<Object[]> sumRevenueByYears(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Sửa countOrdersByDays để dùng COUNT_BIG
    @Query(value = "SELECT CAST(i.issue_date AS DATE) as date, COUNT_BIG(i.invoice_id) as count " +
            "FROM invoice i WHERE i.issue_date BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(i.issue_date AS DATE)", nativeQuery = true)
    List<Object[]> countOrdersByDays(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);
    // Sửa sumRevenueByDays thành native SQL
    @Query(value = "SELECT CAST(i.issue_date AS DATE) as date, COALESCE(SUM(i.total), 0) as revenue " +
            "FROM invoice i WHERE i.issue_date BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(i.issue_date AS DATE)", nativeQuery = true)
    List<Object[]> sumRevenueByDays(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);




}




