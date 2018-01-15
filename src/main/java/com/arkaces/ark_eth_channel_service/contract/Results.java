package com.arkaces.ark_eth_channel_service.contract;

import com.arkaces.ark_eth_channel_service.transfer.Transfer;
import lombok.Data;

import java.util.List;

@Data
public class Results {

    private String recipientEthAddress;
    private String depositArkAddress;
    private List<Transfer> transfers;
}