package com.arkaces.btc_ark_channel_service.contract;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transfers")
public class TransferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;

    private String id;
    private LocalDateTime createdAt;
    private BigDecimal btcAmount;
    private BigDecimal btcToArkRate;
    private BigDecimal btcFlatFee;
    private BigDecimal btcPercentFee;
    private BigDecimal btcTotalFee;
    private BigDecimal arkSendAmount;
    private String arkTransactionId;
}
