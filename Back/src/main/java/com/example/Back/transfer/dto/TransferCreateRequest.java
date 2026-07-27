package com.example.Back.transfer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransferCreateRequest {
    @NotBlank(message = "Gönderici hesap numarası zorunludur")
    private String fromAccountNumber;

    @NotBlank(message = "Alıcı hesap numarası zorunludur")
    private String toAccountNumber;

    @NotNull(message = "Tutar (amount) zorunludur")
    @DecimalMin(value = "0.01", message = "Tutar sıfırdan büyük olmalıdır")
    private BigDecimal amount;
    
    @NotBlank(message = "Idempotency key zorunludur")
    private String idempotencyKey; // Çift çekimleri önlemek için
}
