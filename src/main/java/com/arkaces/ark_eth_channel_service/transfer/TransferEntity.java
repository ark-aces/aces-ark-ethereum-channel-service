package com.arkaces.ark_eth_channel_service.transfer;

import com.arkaces.ark_eth_channel_service.contract.ContractEntity;
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
    private String status;
    private String arkTransactionId;

    @Column(precision = 20, scale = 8)
    private BigDecimal arkAmount;

    @Column(precision = 20, scale = 8)
    private BigDecimal arkToEthRate;

    @Column(precision = 20, scale = 8)
    private BigDecimal arkFlatFee;

    @Column(precision = 20, scale = 8)
    private BigDecimal arkPercentFee;

    @Column(precision = 20, scale = 8)
    private BigDecimal arkTotalFee;

    private String ethTransactionId;

    @Column(precision = 20, scale = 8)
    private BigDecimal ethSendAmount;

    @ManyToOne(cascade = CascadeType.ALL)
    private ContractEntity contractEntity;
}
