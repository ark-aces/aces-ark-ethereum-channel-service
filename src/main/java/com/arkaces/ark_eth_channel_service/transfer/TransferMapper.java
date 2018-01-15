package com.arkaces.ark_eth_channel_service.transfer;

import org.springframework.stereotype.Service;

import java.time.ZoneOffset;

@Service
public class TransferMapper {
    
    public Transfer map(TransferEntity transferEntity) {
        Transfer transfer = new Transfer();
        transfer.setId(transferEntity.getId());
        transfer.setStatus(transferEntity.getStatus());
        transfer.setCreatedAt(transferEntity.getCreatedAt().atOffset(ZoneOffset.UTC).toString());
        transfer.setArkTransactionId(transferEntity.getArkTransactionId());
        transfer.setArkAmount(transferEntity.getArkAmount().toPlainString());
        transfer.setArkToEthRate(transferEntity.getArkToEthRate().toPlainString());
        transfer.setArkFlatFee(transferEntity.getArkFlatFee().toPlainString());
        transfer.setArkPercentFee(transferEntity.getArkPercentFee().toPlainString());
        transfer.setArkTotalFee(transferEntity.getArkTotalFee().toPlainString());
        transfer.setEthTransactionId(transferEntity.getEthTransactionId());
        transfer.setEthSendAmount(transferEntity.getEthSendAmount().toPlainString());
        return transfer;
    }
}
