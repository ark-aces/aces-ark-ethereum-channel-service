package com.arkaces.ark_eth_channel_service.transfer;

import com.arkaces.aces_server.common.identifer.IdentifierGenerator;
import com.arkaces.ark_eth_channel_service.FeeSettings;
import com.arkaces.ark_eth_channel_service.ServiceEthAccountSettings;
import com.arkaces.ark_eth_channel_service.ark.ArkSatoshiService;
import com.arkaces.ark_eth_channel_service.contract.ContractEntity;
import com.arkaces.ark_eth_channel_service.contract.ContractRepository;
import com.arkaces.ark_eth_channel_service.ethereum.EthereumService;
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
import java.util.Collections;

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
    private final EthereumService ethereumService;

    @PostMapping("/arkEvents")
    public ResponseEntity<Void> handleArkEvent(@RequestBody ArkEventPayload eventPayload) {
        String arkTransactionId = eventPayload.getTransactionId();
        ArkTransaction transaction = eventPayload.getTransaction();

        log.info("Received ark event: {} -> {}", arkTransactionId, transaction.toString());

        String subscriptionId = eventPayload.getSubscriptionId();
        ContractEntity contractEntity = contractRepository.findOneBySubscriptionId(subscriptionId);
        if (contractEntity != null) {
            log.info("Matched event for contract id {}, ark transaction id {}", contractEntity.getId(), arkTransactionId);

            TransferEntity transferEntity = new TransferEntity();
            String transferId = identifierGenerator.generate();
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

            transferEntity.setStatus(TransferStatus.NEW.getStatus());

            transferRepository.save(transferEntity);

            // Check that service has enough eth to send
            BigDecimal serviceAvailableEth = ethereumService.getBalance(serviceEthAccountSettings.getAddress());
            if (ethSendAmount.compareTo(serviceAvailableEth) < 0) {
                // Send eth transaction
                String ethTransactionId = ethereumService.sendTransaction(
                        serviceEthAccountSettings.getAddress(),
                        contractEntity.getRecipientEthAddress(),
                        ethSendAmount
                );

                // Check if eth transaction was successful
                if (ethTransactionId != null) {
                    transferEntity.setEthTransactionId(ethTransactionId);

                    log.info("Sent {} ETH to {}, eth transaction id {}, ark transaction id {}",
                            ethSendAmount.toPlainString(),
                            contractEntity.getRecipientEthAddress(),
                            ethTransactionId,
                            arkTransactionId
                    );

                    transferEntity.setStatus(TransferStatus.COMPLETE.getStatus());
                } else {
                    log.error("Failed to send {} ETH to {}, ark transaction id {}",
                            ethSendAmount.toPlainString(),
                            contractEntity.getRecipientEthAddress(),
                            arkTransactionId
                    );

                    transferEntity.setStatus(TransferStatus.FAILED.getStatus());
                }
            } else {
                log.warn("Service account has insufficient eth to send transfer " + transferId
                        + ": available = " + serviceAvailableEth + ", ethSendAmount = " + ethSendAmount);
                transferEntity.setStatus(TransferStatus.FAILED.getStatus());
            }


            transferRepository.save(transferEntity);

            log.info("Saved transfer id {} to contract {}", transferEntity.getId(), contractEntity.getId());
        }

        return ResponseEntity.ok().build();
    }
}
