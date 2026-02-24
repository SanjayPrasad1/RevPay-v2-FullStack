package com.revpay.service.impl;

import com.revpay.dto.request.AddPaymentMethodRequest;
import com.revpay.dto.response.PaymentMethodResponse;
import com.revpay.entity.PaymentMethod;
import com.revpay.entity.User;
import com.revpay.enums.NotificationType;
import com.revpay.exception.BadRequestException;
import com.revpay.repository.PaymentMethodRepository;
import com.revpay.repository.UserRepository;
import com.revpay.service.NotificationService;
import com.revpay.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public PaymentMethodResponse addPaymentMethod(Long userId, AddPaymentMethodRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("User not found"));

        String lastFour = request.getCardNumber().substring(request.getCardNumber().length() - 4);
        String masked = "**** **** **** " + lastFour;

        if (request.isDefault()) {
            // Remove existing default
            paymentMethodRepository.findByUserAndIsDefaultTrue(user)
                .ifPresent(pm -> { pm.setDefault(false); paymentMethodRepository.save(pm); });
        }

        PaymentMethod pm = PaymentMethod.builder()
            .user(user)
            .cardHolderName(request.getCardHolderName())
            .lastFourDigits(lastFour)
            .maskedCardNumber(masked)
            .expiryMonth(request.getExpiryMonth())
            .expiryYear(request.getExpiryYear())
            .cardType(request.getCardType())
            .billingAddress(request.getBillingAddress())
            .billingCity(request.getBillingCity())
            .billingState(request.getBillingState())
            .billingZip(request.getBillingZip())
            .billingCountry(request.getBillingCountry())
            .isDefault(request.isDefault())
            .build();

        pm = paymentMethodRepository.save(pm);

        notificationService.createNotification(user, "Card Added",
            "New " + request.getCardType() + " card ending in " + lastFour + " has been added.",
            NotificationType.CARD_CHANGE, null);

        return mapToResponse(pm);
    }

    @Override
    public List<PaymentMethodResponse> getPaymentMethods(Long userId) {
        User user = userRepository.getReferenceById(userId);
        return paymentMethodRepository.findByUserAndActiveTrue(user)
            .stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional
    public void setDefault(Long userId, Long paymentMethodId) {
        User user = userRepository.getReferenceById(userId);
        paymentMethodRepository.findByUserAndIsDefaultTrue(user)
            .ifPresent(pm -> { pm.setDefault(false); paymentMethodRepository.save(pm); });

        PaymentMethod pm = paymentMethodRepository.findByIdAndUser(paymentMethodId, user)
            .orElseThrow(() -> new BadRequestException("Payment method not found"));
        pm.setDefault(true);
        paymentMethodRepository.save(pm);
    }

    @Override
    @Transactional
    public void deletePaymentMethod(Long userId, Long paymentMethodId) {
        User user = userRepository.getReferenceById(userId);
        PaymentMethod pm = paymentMethodRepository.findByIdAndUser(paymentMethodId, user)
            .orElseThrow(() -> new BadRequestException("Payment method not found"));
        pm.setActive(false);
        paymentMethodRepository.save(pm);

        notificationService.createNotification(user, "Card Removed",
            "Card ending in " + pm.getLastFourDigits() + " has been removed.",
            NotificationType.CARD_CHANGE, null);
    }

    private PaymentMethodResponse mapToResponse(PaymentMethod pm) {
        return PaymentMethodResponse.builder()
            .id(pm.getId())
            .cardHolderName(pm.getCardHolderName())
            .maskedCardNumber(pm.getMaskedCardNumber())
            .lastFourDigits(pm.getLastFourDigits())
            .expiryMonth(pm.getExpiryMonth())
            .expiryYear(pm.getExpiryYear())
            .cardType(pm.getCardType())
            .billingAddress(pm.getBillingAddress())
            .isDefault(pm.isDefault())
            .createdAt(pm.getCreatedAt())
            .build();
    }
}
