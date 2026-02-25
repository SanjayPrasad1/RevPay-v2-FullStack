package com.revpay.repository;

import com.revpay.entity.Invoice;
import com.revpay.entity.User;
import com.revpay.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Page<Invoice> findByBusinessUserOrderByCreatedAtDesc(User user, Pageable pageable);
    List<Invoice> findByBusinessUserAndStatus(User user, InvoiceStatus status);
    Optional<Invoice> findByInvoiceNumberAndBusinessUser(String invoiceNumber, User user);

    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.businessUser = :user AND i.status = 'PAID'")
    BigDecimal sumPaidInvoices(@Param("user") User user);

    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.businessUser = :user AND i.status IN ('SENT', 'OVERDUE')")
    BigDecimal sumOutstandingInvoices(@Param("user") User user);

    Page<Invoice> findByCustomerEmailOrderByCreatedAtDesc(String cutomerEmail, Pageable pageable);
}
