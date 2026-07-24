package com.example.Back.loan.controller;

import com.example.Back.loan.dto.LoanRequestDTO;
import com.example.Back.loan.dto.LoanResponseDTO;
import com.example.Back.loan.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/apply")
    public ResponseEntity<LoanResponseDTO> applyForLoan(@RequestBody LoanRequestDTO request) {
        return ResponseEntity.ok(loanService.applyForLoan(request));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<LoanResponseDTO>> getLoansByAccount(@PathVariable UUID accountId) {
        return ResponseEntity.ok(loanService.getLoansByAccount(accountId));
    }
}
