package com.revpay.service.impl;

import com.opencsv.CSVWriter;
import com.revpay.dto.request.SendMoneyRequest;
import com.revpay.dto.request.TransactionFilterRequest;
import com.revpay.dto.request.WalletOperationRequest;
import com.revpay.dto.response.TransactionResponse;
import com.revpay.entity.Transaction;
import com.revpay.entity.User;
import com.revpay.enums.NotificationType;
import com.revpay.enums.TransactionStatus;
import com.revpay.enums.TransactionType;
import com.revpay.exception.BadRequestException;
import com.revpay.repository.TransactionRepository;
import com.revpay.repository.UserRepository;
import com.revpay.service.NotificationService;
import com.revpay.service.TransactionService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public TransactionResponse sendMoney(Long senderId, SendMoneyRequest request) {
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new BadRequestException("Sender not found"));

        // Find recipient
        User recipient = userRepository.findByEmail(request.getRecipientIdentifier())
            .or(() -> userRepository.findByPhoneNumber(request.getRecipientIdentifier()))
            .or(() -> userRepository.findByUsername(request.getRecipientIdentifier()))
            .orElseThrow(() -> new BadRequestException("Recipient not found"));

        if (sender.getId().equals(recipient.getId())) {
            throw new BadRequestException("Cannot send money to yourself");
        }

        if (sender.getWalletBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("Insufficient wallet balance");
        }

        if (sender.getTransactionPin() != null && request.getTransactionPin() != null) {
            if (!passwordEncoder.matches(request.getTransactionPin(), sender.getTransactionPin())) {
                throw new BadRequestException("Invalid transaction PIN");
            }
        }

        sender.setWalletBalance(sender.getWalletBalance().subtract(request.getAmount()));
        recipient.setWalletBalance(recipient.getWalletBalance().add(request.getAmount()));

        userRepository.save(sender);
        userRepository.save(recipient);

        Transaction transaction = Transaction.builder()
            .transactionId(generateTransactionId())
            .sender(sender)
            .receiver(recipient)
            .amount(request.getAmount())
            .type(TransactionType.SEND)
            .status(TransactionStatus.COMPLETED)
            .note(request.getNote())
            .completedAt(LocalDateTime.now())
            .build();

        transaction = transactionRepository.save(transaction);

        notificationService.createNotification(sender, "Money Sent",
            "You sent " + request.getAmount() + " to " + recipient.getFullName(),
            NotificationType.TRANSACTION, transaction.getTransactionId());

        notificationService.createNotification(recipient, "Money Received",
            "You received " + request.getAmount() + " from " + sender.getFullName(),
            NotificationType.TRANSACTION, transaction.getTransactionId());

        // Low balance check
        if (sender.getWalletBalance().compareTo(new BigDecimal("100")) < 0) {
            notificationService.createNotification(sender, "Low Balance Alert",
                "Your wallet balance is low: " + sender.getWalletBalance(),
                NotificationType.LOW_BALANCE, null);
        }

        return mapToResponse(transaction);
    }

    @Override
    @Transactional
    public TransactionResponse addFunds(Long userId, WalletOperationRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("User not found"));

        user.setWalletBalance(user.getWalletBalance().add(request.getAmount()));
        userRepository.save(user);

        Transaction transaction = Transaction.builder()
            .transactionId(generateTransactionId())
            .receiver(user)
            .amount(request.getAmount())
            .type(TransactionType.ADD_FUNDS)
            .status(TransactionStatus.COMPLETED)
            .description("Funds added from card")
            .completedAt(LocalDateTime.now())
            .build();

        transaction = transactionRepository.save(transaction);

        notificationService.createNotification(user, "Funds Added",
            "Successfully added " + request.getAmount() + " to your wallet",
            NotificationType.TRANSACTION, transaction.getTransactionId());

        return mapToResponse(transaction);
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(Long userId, WalletOperationRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("User not found"));

        if (user.getWalletBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("Insufficient balance");
        }

        if (user.getTransactionPin() != null && request.getTransactionPin() != null) {
            if (!passwordEncoder.matches(request.getTransactionPin(), user.getTransactionPin())) {
                throw new BadRequestException("Invalid transaction PIN");
            }
        }

        user.setWalletBalance(user.getWalletBalance().subtract(request.getAmount()));
        userRepository.save(user);

        Transaction transaction = Transaction.builder()
            .transactionId(generateTransactionId())
            .sender(user)
            .amount(request.getAmount())
            .type(TransactionType.WITHDRAWAL)
            .status(TransactionStatus.COMPLETED)
            .description("Withdrawal to bank account")
            .completedAt(LocalDateTime.now())
            .build();

        transaction = transactionRepository.save(transaction);

        notificationService.createNotification(user, "Withdrawal Successful",
            "Successfully withdrew " + request.getAmount() + " from your wallet",
            NotificationType.TRANSACTION, transaction.getTransactionId());

        return mapToResponse(transaction);
    }

    @Override
    public Page<TransactionResponse> getTransactions(Long userId, TransactionFilterRequest filter) {
        User user = userRepository.getReferenceById(userId);
        PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
        return transactionRepository.findByUserWithFilters(
            user, filter.getType(), filter.getStatus(),
            filter.getStartDate(), filter.getEndDate(),
            filter.getMinAmount(), filter.getMaxAmount(),
            pageRequest
        ).map(this::mapToResponse);
    }

    @Override
    public List<TransactionResponse> searchTransactions(Long userId, String query) {
        User user = userRepository.getReferenceById(userId);
        return transactionRepository.searchTransactions(user, query)
            .stream().map(this::mapToResponse).toList();
    }

    @Override
    public TransactionResponse getTransaction(Long userId, String transactionId) {
        Transaction t = transactionRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new BadRequestException("Transaction not found"));
        User user = userRepository.getReferenceById(userId);
        if ((t.getSender() == null || !t.getSender().getId().equals(userId)) &&
            (t.getReceiver() == null || !t.getReceiver().getId().equals(userId))) {
            throw new BadRequestException("Access denied");
        }
        return mapToResponse(t);
    }

    @Override
    public void exportToCsv(Long userId, HttpServletResponse response) throws Exception {
        User user = userRepository.getReferenceById(userId);
        List<Transaction> transactions = transactionRepository
            .findBySenderOrReceiverOrderByCreatedAtDesc(user, user);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=transactions.csv");

        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(response.getOutputStream()))) {
            writer.writeNext(new String[]{"Transaction ID", "Type", "Amount", "Status",
                "Sender", "Receiver", "Date", "Note"});
            for (Transaction t : transactions) {
                writer.writeNext(new String[]{
                    t.getTransactionId(),
                    t.getType().name(),
                    t.getAmount().toPlainString(),
                    t.getStatus().name(),
                    t.getSender() != null ? t.getSender().getFullName() : "N/A",
                    t.getReceiver() != null ? t.getReceiver().getFullName() : "N/A",
                    t.getCreatedAt().toString(),
                    t.getNote() != null ? t.getNote() : ""
                });
            }
        }
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 16).toUpperCase().replace("-", "");
    }

    private TransactionResponse mapToResponse(Transaction t) {
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
