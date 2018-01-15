package com.arkaces.ark_eth_channel_service.transfer;

import lombok.Data;

@Data
public class Transfer {

    private String id;
    private String status;
    private String createdAt;
    private String arkTransactionId;
    private String arkAmount;
    private String arkToEthRate;
    private String arkFlatFee;
    private String arkPercentFee;
    private String arkTotalFee;
    private String ethTransactionId;
    private String ethSendAmount;
}
