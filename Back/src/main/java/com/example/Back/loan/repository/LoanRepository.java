package com.example.Back.loan.repository;

import com.example.Back.loan.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import com.example.Back.loan.entity.LoanStatus;

@Repository
public interface LoanRepository extends JpaRepository<Loan, UUID> {
    List<Loan> findByAccountId(UUID accountId);
    long countByStatus(LoanStatus status);
}
