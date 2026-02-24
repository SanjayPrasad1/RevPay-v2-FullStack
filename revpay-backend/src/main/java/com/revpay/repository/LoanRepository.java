package com.revpay.repository;

import com.revpay.entity.Loan;
import com.revpay.entity.User;
import com.revpay.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByBusinessUserOrderByCreatedAtDesc(User user);
    List<Loan> findByBusinessUserAndStatus(User user, LoanStatus status);
    Optional<Loan> findByLoanNumber(String loanNumber);
    Optional<Loan> findByIdAndBusinessUser(Long id, User user);
}
