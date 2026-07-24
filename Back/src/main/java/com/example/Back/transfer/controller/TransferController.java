package com.example.Back.transfer.controller;

import com.example.Back.transfer.dto.TransferCreateRequest;
import com.example.Back.transfer.dto.TransferResponse;
import com.example.Back.transfer.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<TransferResponse> createTransfer(@Valid @RequestBody TransferCreateRequest request) {
        // İdempotency anahtarı genellikle Client (Frontend) tarafından üretilip header'dan veya body'den gelir.
        // Biz burada body'den alıyoruz.
        return ResponseEntity.ok(transferService.executeTransfer(request));
    }
}
