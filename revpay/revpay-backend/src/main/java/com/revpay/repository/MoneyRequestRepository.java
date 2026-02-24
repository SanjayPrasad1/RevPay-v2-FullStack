package com.revpay.repository;

import com.revpay.entity.MoneyRequest;
import com.revpay.entity.User;
import com.revpay.enums.MoneyRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MoneyRequestRepository extends JpaRepository<MoneyRequest, Long> {
    List<MoneyRequest> findByRecipientAndStatusOrderByCreatedAtDesc(User recipient, MoneyRequestStatus status);
    List<MoneyRequest> findByRequesterOrderByCreatedAtDesc(User requester);
    List<MoneyRequest> findByRecipientOrderByCreatedAtDesc(User recipient);
    Optional<MoneyRequest> findByIdAndRequester(Long id, User requester);
    Optional<MoneyRequest> findByIdAndRecipient(Long id, User recipient);
}
