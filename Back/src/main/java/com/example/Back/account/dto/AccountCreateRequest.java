package com.example.Back.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AccountCreateRequest {
    @NotBlank(message = "Para birimi (currency) boş olamaz")
    private String currency;
}
