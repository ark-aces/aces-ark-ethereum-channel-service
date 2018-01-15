package com.arkaces.ark_eth_channel_service.transfer;

import ark_java_client.ArkClient;
import com.arkaces.aces_server.common.identifer.IdentifierGenerator;
import com.arkaces.ark_eth_channel_service.FeeSettings;
import com.arkaces.ark_eth_channel_service.ServiceEthAccountSettings;
import com.arkaces.ark_eth_channel_service.ark.ArkSatoshiService;
import com.arkaces.ark_eth_channel_service.contract.ContractEntity;
import com.arkaces.ark_eth_channel_service.contract.ContractRepository;
import com.arkaces.ark_eth_channel_service.exchange_rate.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ArkEventHandler {

    private final ContractRepository contractRepository;
    private final TransferRepository transferRepository;
    private final IdentifierGenerator identifierGenerator;
    private final ExchangeRateService exchangeRateService;
    private final ArkSatoshiService arkSatoshiService;
    private final ServiceEthAccountSettings serviceEthAccountSettings;
    private final FeeSettings feeSettings;

    @PostMapping("/arkEvents")
    public ResponseEntity<Void> handleArkEvent(@RequestBody ArkEvent event) {
        // todo: verify event post is signed by listener
        String arkTransactionId = event.getTransactionId();
        ArkTransaction transaction = event.getTransaction();
        
        log.info("Received Ark event: " + arkTransactionId + " -> " + transaction.toString());
        
        String subscriptionId = event.getSubscriptionId();
        ContractEntity contractEntity = contractRepository.findOneBySubscriptionId(subscriptionId);
        if (contractEntity != null) {
            // todo: lock contract for update to prevent concurrent processing of a listener transaction.
            // Listeners send events serially, so that shouldn't be an issue, but we might want to lock
            // to be safe.

            log.info("Matched event for contract id " + contractEntity.getId() + " ark transaction id " + arkTransactionId);

            String transferId = identifierGenerator.generate();

            TransferEntity transferEntity = new TransferEntity();
            transferEntity.setId(transferId);
            transferEntity.setCreatedAt(LocalDateTime.now());
            transferEntity.setArkTransactionId(arkTransactionId);
            transferEntity.setContractEntity(contractEntity);

            // Get ARK amount from transaction
            Long arkSatoshis = transaction.getAmount();
            BigDecimal arkAmount = arkSatoshiService.toArk(arkSatoshis);
            transferEntity.setArkAmount(arkAmount);

            BigDecimal arkToEthRate = exchangeRateService.getRate("ARK", "ETH");
            transferEntity.setArkToEthRate(arkToEthRate);

            // Set fees
            transferEntity.setArkFlatFee(feeSettings.getArkFlatFee());
            transferEntity.setArkPercentFee(feeSettings.getArkPercentFee());

            BigDecimal percentFee = feeSettings.getArkPercentFee().divide(new BigDecimal("100.00"), 8, BigDecimal.ROUND_HALF_UP);
            BigDecimal arkTotalFeeAmount = arkAmount.multiply(percentFee).add(feeSettings.getArkFlatFee());
            transferEntity.setArkTotalFee(arkTotalFeeAmount);

            // Calculate send eth amount
            BigDecimal ethSendAmount = BigDecimal.ZERO;
            if (arkAmount.compareTo(arkTotalFeeAmount) > 0) {
                ethSendAmount = arkAmount.multiply(arkToEthRate).setScale(8, RoundingMode.HALF_DOWN);
            }
            transferEntity.setEthSendAmount(ethSendAmount);

            transferEntity.setStatus(TransferStatus.NEW);
            transferRepository.save(transferEntity);

            // Send eth transaction
            String ethTransactionId = ""; // TODO
//            String ethTransactionId = arkClient.broadcastTransaction(
//                    contractEntity.getRecipientEthAddress(),
//                    ethSendSatoshis,
//                    null,
//                    serviceEthAccountSettings.getPassphrase()
//            );
            transferEntity.setEthTransactionId(ethTransactionId);

            log.info("Sent " + ethSendAmount + " ETH to " + contractEntity.getRecipientEthAddress()
                + ", ETH transaction id " + ethTransactionId + ", ARK transaction " + arkTransactionId);

            transferEntity.setStatus(TransferStatus.COMPLETE);
            transferRepository.save(transferEntity);
            
            log.info("Saved transfer id " + transferEntity.getId() + " to contract " + contractEntity.getId());
        }
        
        return ResponseEntity.ok().build();
    }
}
