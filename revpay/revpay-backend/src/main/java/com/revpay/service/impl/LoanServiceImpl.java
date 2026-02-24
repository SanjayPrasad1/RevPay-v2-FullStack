package com.revpay.service.impl;

import com.revpay.dto.request.LoanApplicationRequest;
import com.revpay.dto.response.LoanResponse;
import com.revpay.entity.Loan;
import com.revpay.entity.User;
import com.revpay.enums.AccountType;
import com.revpay.enums.LoanStatus;
import com.revpay.enums.NotificationType;
import com.revpay.exception.BadRequestException;
import com.revpay.repository.LoanRepository;
import com.revpay.repository.UserRepository;
import com.revpay.service.LoanService;
import com.revpay.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private static final BigDecimal INTEREST_RATE = new BigDecimal("12.0"); // 12% annually

    @Override
    @Transactional
    public LoanResponse applyForLoan(Long userId, LoanApplicationRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("User not found"));
        if (user.getAccountType() != AccountType.BUSINESS) {
            throw new BadRequestException("Only business accounts can apply for loans");
        }

        BigDecimal monthlyRate = INTEREST_RATE.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        int n = request.getTenure();
        BigDecimal emi = calculateEMI(request.getRequestedAmount(), monthlyRate, n);

        Loan loan = Loan.builder()
            .loanNumber("LN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .businessUser(user)
            .requestedAmount(request.getRequestedAmount())
            .purpose(request.getPurpose())
            .tenure(request.getTenure())
            .interestRate(INTEREST_RATE)
            .emi(emi)
            .businessRevenue(request.getBusinessRevenue())
            .businessDescription(request.getBusinessDescription())
            .build();

        loan = loanRepository.save(loan);

        notificationService.createNotification(user, "Loan Application Submitted",
            "Your loan application for " + request.getRequestedAmount() + " has been submitted.",
            NotificationType.LOAN, String.valueOf(loan.getId()));

        return mapToResponse(loan);
    }

    @Override
    public List<LoanResponse> getLoans(Long userId) {
        User user = userRepository.getReferenceById(userId);
        return loanRepository.findByBusinessUserOrderByCreatedAtDesc(user)
            .stream().map(this::mapToResponse).toList();
    }

    @Override
    public LoanResponse getLoan(Long userId, Long loanId) {
        User user = userRepository.getReferenceById(userId);
        Loan loan = loanRepository.findByIdAndBusinessUser(loanId, user)
            .orElseThrow(() -> new BadRequestException("Loan not found"));
        return mapToResponse(loan);
    }

    @Override
    @Transactional
    public LoanResponse makeRepayment(Long userId, Long loanId, BigDecimal amount) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("User not found"));
        Loan loan = loanRepository.findByIdAndBusinessUser(loanId, user)
            .orElseThrow(() -> new BadRequestException("Loan not found"));

        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new BadRequestException("Loan is not active");
        }
        if (user.getWalletBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Insufficient balance");
        }

        user.setWalletBalance(user.getWalletBalance().subtract(amount));
        loan.setRepaidAmount(loan.getRepaidAmount().add(amount));

        BigDecimal totalLoanAmount = loan.getApprovedAmount().add(
            loan.getApprovedAmount().multiply(loan.getInterestRate())
                .multiply(BigDecimal.valueOf(loan.getTenure()))
                .divide(BigDecimal.valueOf(1200), 2, RoundingMode.HALF_UP)
        );

        if (loan.getRepaidAmount().compareTo(totalLoanAmount) >= 0) {
            loan.setStatus(LoanStatus.CLOSED);
        }

        userRepository.save(user);
        loanRepository.save(loan);

        return mapToResponse(loan);
    }

    private BigDecimal calculateEMI(BigDecimal principal, BigDecimal monthlyRate, int tenure) {
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRPowN = onePlusR.pow(tenure, new MathContext(10));
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRPowN);
        BigDecimal denominator = onePlusRPowN.subtract(BigDecimal.ONE);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private LoanResponse mapToResponse(Loan loan) {
        BigDecimal outstanding = BigDecimal.ZERO;
        if (loan.getApprovedAmount() != null) {
            BigDecimal total = loan.getApprovedAmount().multiply(
                BigDecimal.ONE.add(loan.getInterestRate()
                    .multiply(BigDecimal.valueOf(loan.getTenure()))
                    .divide(BigDecimal.valueOf(1200), 2, RoundingMode.HALF_UP)));
            outstanding = total.subtract(loan.getRepaidAmount()).max(BigDecimal.ZERO);
        }

        return LoanResponse.builder()
            .id(loan.getId())
            .loanNumber(loan.getLoanNumber())
            .requestedAmount(loan.getRequestedAmount())
            .approvedAmount(loan.getApprovedAmount())
            .purpose(loan.getPurpose())
            .tenure(loan.getTenure())
            .interestRate(loan.getInterestRate())
            .emi(loan.getEmi())
            .repaidAmount(loan.getRepaidAmount())
            .outstandingAmount(outstanding)
            .businessRevenue(loan.getBusinessRevenue())
            .status(loan.getStatus())
            .rejectionReason(loan.getRejectionReason())
            .createdAt(loan.getCreatedAt())
            .approvedAt(loan.getApprovedAt())
            .build();
    }
}
