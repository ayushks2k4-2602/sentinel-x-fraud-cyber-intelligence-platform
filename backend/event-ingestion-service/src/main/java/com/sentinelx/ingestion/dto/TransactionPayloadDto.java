package com.sentinelx.ingestion.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionPayloadDto {

    @NotBlank(message = "transactionId is mandatory")
    private String transactionId;

    @NotNull(message = "amount is mandatory")
    @DecimalMin(value = "0.01", message = "amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "currency is mandatory")
    private String currency;

    @NotBlank(message = "type is mandatory")
    private String type; // e.g. IMPS_TRANSFER, CARD_PAYMENT, UPI_PAYMENT

    private String beneficiaryName;
    private String beneficiaryAccount;
}
