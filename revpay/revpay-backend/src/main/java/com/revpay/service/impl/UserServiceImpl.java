package com.revpay.service.impl;

import com.revpay.dto.request.UpdateProfileRequest;
import com.revpay.dto.response.DashboardResponse;
import com.revpay.dto.response.TransactionResponse;
import com.revpay.dto.response.UserProfileResponse;
import com.revpay.entity.Transaction;
import com.revpay.entity.User;
import com.revpay.enums.AccountType;
import com.revpay.enums.InvoiceStatus;
import com.revpay.enums.LoanStatus;
import com.revpay.enums.MoneyRequestStatus;
import com.revpay.exception.BadRequestException;
import com.revpay.repository.*;
import com.revpay.service.NotificationService;
import com.revpay.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final MoneyRequestRepository moneyRequestRepository;
    private final NotificationService notificationService;
    private final InvoiceRepository invoiceRepository;
    private final LoanRepository loanRepository;

    @Override
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("User not found"));
        return mapToProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("User not found"));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getBusinessName() != null) user.setBusinessName(request.getBusinessName());
        if (request.getBusinessType() != null) user.setBusinessType(request.getBusinessType());
        if (request.getBusinessAddress() != null) user.setBusinessAddress(request.getBusinessAddress());

        user = userRepository.save(user);
        return mapToProfileResponse(user);
    }

    @Override
    public DashboardResponse getDashboard(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("User not found"));

        List<Transaction> recentTx = transactionRepository
            .findTop10BySenderOrReceiverOrderByCreatedAtDesc(user, user);

        long pendingRequests = moneyRequestRepository
            .findByRecipientAndStatusOrderByCreatedAtDesc(user, MoneyRequestStatus.PENDING).size();

        long unreadNotifs = notificationService.getUnreadCount(userId);

        List<TransactionResponse> recentTxResponses = recentTx.stream()
            .map(this::mapTransactionToResponse).toList();

        DashboardResponse.DashboardResponseBuilder builder = DashboardResponse.builder()
            .walletBalance(user.getWalletBalance())
            .recentTransactions(recentTxResponses)
            .pendingRequests(pendingRequests)
            .unreadNotifications(unreadNotifs);

        if (user.getAccountType() == AccountType.BUSINESS) {
            LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime now = LocalDateTime.now();

            BigDecimal totalReceived = transactionRepository.sumReceivedBetween(user, monthStart, now);
            BigDecimal totalSent = transactionRepository.sumSentBetween(user, monthStart, now);
            BigDecimal pendingInvoices = invoiceRepository.sumOutstandingInvoices(user);
            BigDecimal paidInvoices = invoiceRepository.sumPaidInvoices(user);
            long totalInvoices = invoiceRepository.findByBusinessUserOrderByCreatedAtDesc(user,
                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements();
            long activeLoans = loanRepository.findByBusinessUserAndStatus(user, LoanStatus.ACTIVE).size();

            builder
                .totalReceived(totalReceived != null ? totalReceived : BigDecimal.ZERO)
                .totalSent(totalSent != null ? totalSent : BigDecimal.ZERO)
                .pendingInvoicesAmount(pendingInvoices)
                .paidInvoicesAmount(paidInvoices)
                .totalInvoices(totalInvoices)
                .activeLoans(activeLoans);
        }

        return builder.build();
    }

    private UserProfileResponse mapToProfileResponse(User user) {
        return UserProfileResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .phoneNumber(user.getPhoneNumber())
            .accountType(user.getAccountType())
            .walletBalance(user.getWalletBalance())
            .verified(user.isVerified())
            .businessName(user.getBusinessName())
            .businessType(user.getBusinessType())
            .taxId(user.getTaxId())
            .businessAddress(user.getBusinessAddress())
            .createdAt(user.getCreatedAt())
            .build();
    }

    private TransactionResponse mapTransactionToResponse(Transaction t) {
        return TransactionResponse.builder()
            .id(t.getId())
            .transactionId(t.getTransactionId())
            .senderName(t.getSender() != null ? t.getSender().getFullName() : null)
            .senderUsername(t.getSender() != null ? t.getSender().getUsername() : null)
            .receiverName(t.getReceiver() != null ? t.getReceiver().getFullName() : null)
            .receiverUsername(t.getReceiver() != null ? t.getReceiver().getUsername() : null)
            .amount(t.getAmount())
            .type(t.getType())
            .status(t.getStatus())
            .note(t.getNote())
            .description(t.getDescription())
            .createdAt(t.getCreatedAt())
            .completedAt(t.getCompletedAt())
            .build();
    }
}
