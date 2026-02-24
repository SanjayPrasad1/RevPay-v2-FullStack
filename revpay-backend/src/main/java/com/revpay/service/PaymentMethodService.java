package com.revpay.service;

import com.revpay.dto.request.AddPaymentMethodRequest;
import com.revpay.dto.response.PaymentMethodResponse;

import java.util.List;

public interface PaymentMethodService {
    PaymentMethodResponse addPaymentMethod(Long userId, AddPaymentMethodRequest request);
    List<PaymentMethodResponse> getPaymentMethods(Long userId);
    void setDefault(Long userId, Long paymentMethodId);
    void deletePaymentMethod(Long userId, Long paymentMethodId);
}
