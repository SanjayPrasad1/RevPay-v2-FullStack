package com.revpay.service;

import com.revpay.dto.request.MoneyRequestDTO;
import com.revpay.dto.response.MoneyRequestResponse;

import java.util.List;

public interface MoneyRequestService {
    MoneyRequestResponse createRequest(Long requesterId, MoneyRequestDTO dto);
    List<MoneyRequestResponse> getIncomingRequests(Long userId);
    List<MoneyRequestResponse> getOutgoingRequests(Long userId);
    MoneyRequestResponse acceptRequest(Long userId, Long requestId, String pin);
    MoneyRequestResponse declineRequest(Long userId, Long requestId);
    MoneyRequestResponse cancelRequest(Long userId, Long requestId);
}
