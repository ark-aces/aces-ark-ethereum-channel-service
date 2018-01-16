package com.arkaces.ark_eth_channel_service.transfer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
class ArkEventPayload {

    private String id;
    private String transactionId;

    @JsonProperty("data")
    private ArkTransaction transaction;

    private String subscriptionId;
    private String createdAt;
}
