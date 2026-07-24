package com.example.Back.loan.service;

import com.example.Back.loan.dto.LoanRequestDTO;
import com.example.Back.loan.dto.LoanResponseDTO;
import com.example.Back.loan.entity.Loan;
import com.example.Back.loan.entity.LoanStatus;
import com.example.Back.loan.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Transactional
    public LoanResponseDTO applyForLoan(LoanRequestDTO request) {
        Loan loan = Loan.builder()
                .accountId(request.getAccountId())
                .amount(request.getAmount())
                .interestRate(request.getInterestRate())
                .status(LoanStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        loanRepository.save(loan);
        return LoanResponseDTO.from(loan);

        // TODO(Can): Convert request to Loan entity
        // TODO(Can): Set initial status to PENDING
        // TODO(Can): Call CreditScoreCalculator (you will write this) to determine if
        // approved or rejected
        // TODO(Can): Update status based on calculator result
        // TODO(Can): Save to database and return mapped LoanResponseDTO

    }

    public List<LoanResponseDTO> getLoansByAccount(UUID accountId) {
        List<Loan> loans = loanRepository.findByAccountId(accountId);
        return loans.stream()
                .map(LoanResponseDTO::from)
                .toList();

        // TODO(Can): Fetch loans by accountId from repository and map to DTOs

    }

    @Transactional
    public LoanResponseDTO payLoan(UUID loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        loan.setStatus(LoanStatus.ACTIVE);
        loan.setUpdatedAt(LocalDateTime.now());
        loanRepository.save(loan);
        return LoanResponseDTO.from(loan);
    }

    @Transactional
    public LoanResponseDTO rejectLoan(UUID loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
        loan.setStatus(LoanStatus.REJECTED);
        loan.setUpdatedAt(LocalDateTime.now());
        loanRepository.save(loan);
        return LoanResponseDTO.from(loan);
    }

    // TODO(Can): Add methods for state transitions (e.g. payLoan, rejectLoan
    // manually etc. if needed)
}
