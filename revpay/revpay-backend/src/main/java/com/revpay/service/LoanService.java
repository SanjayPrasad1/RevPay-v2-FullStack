package com.revpay.service;

import com.revpay.dto.request.LoanApplicationRequest;
import com.revpay.dto.response.LoanResponse;

import java.math.BigDecimal;
import java.util.List;

public interface LoanService {
    LoanResponse applyForLoan(Long userId, LoanApplicationRequest request);
    List<LoanResponse> getLoans(Long userId);
    LoanResponse getLoan(Long userId, Long loanId);
    LoanResponse makeRepayment(Long userId, Long loanId, BigDecimal amount);
}
