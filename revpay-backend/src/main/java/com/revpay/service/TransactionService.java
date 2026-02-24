package com.revpay.service;

import com.revpay.dto.request.SendMoneyRequest;
import com.revpay.dto.request.TransactionFilterRequest;
import com.revpay.dto.request.WalletOperationRequest;
import com.revpay.dto.response.TransactionResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TransactionService {
    TransactionResponse sendMoney(Long senderId, SendMoneyRequest request);
    TransactionResponse addFunds(Long userId, WalletOperationRequest request);
    TransactionResponse withdraw(Long userId, WalletOperationRequest request);
    Page<TransactionResponse> getTransactions(Long userId, TransactionFilterRequest filter);
    List<TransactionResponse> searchTransactions(Long userId, String query);
    TransactionResponse getTransaction(Long userId, String transactionId);
    void exportToCsv(Long userId, HttpServletResponse response) throws Exception;
}
