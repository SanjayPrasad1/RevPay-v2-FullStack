package com.revpay.service.impl;

import com.revpay.dto.request.MoneyRequestDTO;
import com.revpay.dto.response.MoneyRequestResponse;
import com.revpay.entity.MoneyRequest;
import com.revpay.entity.Transaction;
import com.revpay.entity.User;
import com.revpay.enums.MoneyRequestStatus;
import com.revpay.enums.NotificationType;
import com.revpay.enums.TransactionStatus;
import com.revpay.enums.TransactionType;
import com.revpay.exception.BadRequestException;
import com.revpay.repository.MoneyRequestRepository;
import com.revpay.repository.TransactionRepository;
import com.revpay.repository.UserRepository;
import com.revpay.service.MoneyRequestService;
import com.revpay.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MoneyRequestServiceImpl implements MoneyRequestService {

    private final MoneyRequestRepository moneyRequestRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public MoneyRequestResponse createRequest(Long requesterId, MoneyRequestDTO dto) {
        User requester = userRepository.findById(requesterId)
            .orElseThrow(() -> new BadRequestException("User not found"));
        User recipient = userRepository.findByEmail(dto.getRecipientIdentifier())
            .or(() -> userRepository.findByUsername(dto.getRecipientIdentifier()))
            .or(() -> userRepository.findByPhoneNumber(dto.getRecipientIdentifier()))
            .orElseThrow(() -> new BadRequestException("Recipient not found"));

        if (requester.getId().equals(recipient.getId())) {
            throw new BadRequestException("Cannot request money from yourself");
        }

        MoneyRequest request = MoneyRequest.builder()
            .requester(requester)
            .recipient(recipient)
            .amount(dto.getAmount())
            .purpose(dto.getPurpose())
            .build();

        request = moneyRequestRepository.save(request);

        notificationService.createNotification(recipient, "Money Request",
            requester.getFullName() + " requested " + dto.getAmount() + " from you.",
            NotificationType.MONEY_REQUEST, String.valueOf(request.getId()));

        return mapToResponse(request);
    }

    @Override
    public List<MoneyRequestResponse> getIncomingRequests(Long userId) {
        User user = userRepository.getReferenceById(userId);
        return moneyRequestRepository.findByRecipientOrderByCreatedAtDesc(user)
            .stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<MoneyRequestResponse> getOutgoingRequests(Long userId) {
        User user = userRepository.getReferenceById(userId);
        return moneyRequestRepository.findByRequesterOrderByCreatedAtDesc(user)
            .stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional
    public MoneyRequestResponse acceptRequest(Long userId, Long requestId, String pin) {
        User recipient = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("User not found"));
        MoneyRequest request = moneyRequestRepository.findByIdAndRecipient(requestId, recipient)
            .orElseThrow(() -> new BadRequestException("Money request not found"));

        if (request.getStatus() != MoneyRequestStatus.PENDING) {
            throw new BadRequestException("Request is no longer pending");
        }

        if (recipient.getWalletBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("Insufficient balance to accept request");
        }

        if (recipient.getTransactionPin() != null && pin != null) {
            if (!passwordEncoder.matches(pin, recipient.getTransactionPin())) {
                throw new BadRequestException("Invalid transaction PIN");
            }
        }

        recipient.setWalletBalance(recipient.getWalletBalance().subtract(request.getAmount()));
        request.getRequester().setWalletBalance(
            request.getRequester().getWalletBalance().add(request.getAmount()));

        userRepository.save(recipient);
        userRepository.save(request.getRequester());

        request.setStatus(MoneyRequestStatus.ACCEPTED);
        moneyRequestRepository.save(request);

        String txId = "TXN-" + UUID.randomUUID().toString().substring(0, 16).toUpperCase().replace("-", "");
        Transaction transaction = Transaction.builder()
            .transactionId(txId)
            .sender(recipient)
            .receiver(request.getRequester())
            .amount(request.getAmount())
            .type(TransactionType.SEND)
            .status(TransactionStatus.COMPLETED)
            .note("Payment for: " + request.getPurpose())
            .completedAt(LocalDateTime.now())
            .build();
        transactionRepository.save(transaction);

        notificationService.createNotification(request.getRequester(), "Money Request Accepted",
            recipient.getFullName() + " accepted your request of " + request.getAmount(),
            NotificationType.MONEY_REQUEST, String.valueOf(request.getId()));

        return mapToResponse(request);
    }

    @Override
    @Transactional
    public MoneyRequestResponse declineRequest(Long userId, Long requestId) {
        User recipient = userRepository.getReferenceById(userId);
        MoneyRequest request = moneyRequestRepository.findByIdAndRecipient(requestId, recipient)
            .orElseThrow(() -> new BadRequestException("Money request not found"));

        if (request.getStatus() != MoneyRequestStatus.PENDING) {
            throw new BadRequestException("Request is no longer pending");
        }

        request.setStatus(MoneyRequestStatus.DECLINED);
        moneyRequestRepository.save(request);

        notificationService.createNotification(request.getRequester(), "Money Request Declined",
            recipient.getFullName() + " declined your request of " + request.getAmount(),
            NotificationType.MONEY_REQUEST, String.valueOf(request.getId()));

        return mapToResponse(request);
    }

    @Override
    @Transactional
    public MoneyRequestResponse cancelRequest(Long userId, Long requestId) {
        User requester = userRepository.getReferenceById(userId);
        MoneyRequest request = moneyRequestRepository.findByIdAndRequester(requestId, requester)
            .orElseThrow(() -> new BadRequestException("Money request not found"));

        if (request.getStatus() != MoneyRequestStatus.PENDING) {
            throw new BadRequestException("Only pending requests can be cancelled");
        }

        request.setStatus(MoneyRequestStatus.CANCELLED);
        return mapToResponse(moneyRequestRepository.save(request));
    }

    private MoneyRequestResponse mapToResponse(MoneyRequest r) {
        return MoneyRequestResponse.builder()
            .id(r.getId())
            .requesterName(r.getRequester().getFullName())
            .requesterUsername(r.getRequester().getUsername())
            .recipientName(r.getRecipient().getFullName())
            .recipientUsername(r.getRecipient().getUsername())
            .amount(r.getAmount())
            .purpose(r.getPurpose())
            .status(r.getStatus())
            .createdAt(r.getCreatedAt())
            .updatedAt(r.getUpdatedAt())
            .build();
    }
}
