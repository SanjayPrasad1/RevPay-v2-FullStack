package com.revpay.repository;

import com.revpay.entity.Transaction;
import com.revpay.entity.User;
import com.revpay.enums.TransactionStatus;
import com.revpay.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionId(String transactionId);

    List<Transaction> findBySenderOrReceiverOrderByCreatedAtDesc(User sender, User receiver);

    @Query("SELECT t FROM Transaction t WHERE (t.sender = :user OR t.receiver = :user) " +
           "AND (:type IS NULL OR t.type = :type) " +
           "AND (:status IS NULL OR t.status = :status) " +
           "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR t.createdAt <= :endDate) " +
           "AND (:minAmount IS NULL OR t.amount >= :minAmount) " +
           "AND (:maxAmount IS NULL OR t.amount <= :maxAmount) " +
           "ORDER BY t.createdAt DESC")
    Page<Transaction> findByUserWithFilters(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("status") TransactionStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE (t.sender = :user OR t.receiver = :user) " +
           "AND (LOWER(t.sender.fullName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(t.receiver.fullName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(t.transactionId) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY t.createdAt DESC")
    List<Transaction> searchTransactions(@Param("user") User user, @Param("query") String query);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.receiver = :user AND t.status = 'COMPLETED' " +
           "AND t.createdAt >= :startDate AND t.createdAt <= :endDate")
    BigDecimal sumReceivedBetween(@Param("user") User user,
                                  @Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.sender = :user AND t.status = 'COMPLETED' " +
           "AND t.createdAt >= :startDate AND t.createdAt <= :endDate")
    BigDecimal sumSentBetween(@Param("user") User user,
                              @Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate);

    List<Transaction> findTop10BySenderOrReceiverOrderByCreatedAtDesc(User sender, User receiver);

    @Query("SELECT t.sender, SUM(t.amount) as total FROM Transaction t " +
           "WHERE t.receiver = :user AND t.status = 'COMPLETED' " +
           "GROUP BY t.sender ORDER BY total DESC")
    List<Object[]> findTopCustomers(@Param("user") User user, Pageable pageable);
}
