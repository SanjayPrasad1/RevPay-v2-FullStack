package com.revpay.entity;

import com.revpay.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String loanNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_user_id", nullable = false)
    private User businessUser;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal requestedAmount;

    @Column(precision = 19, scale = 4)
    private BigDecimal approvedAmount;

    private String purpose;

    // Tenure in months
    private Integer tenure;

    @Column(precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(precision = 19, scale = 4)
    private BigDecimal emi;

    @Column(precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal repaidAmount = BigDecimal.ZERO;

    private String businessRevenue;
    private String businessDescription;
    private String documentsPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LoanStatus status = LoanStatus.PENDING;

    private String rejectionReason;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime approvedAt;
}
