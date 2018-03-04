package com.arkaces.ark_eth_channel_service.contract;

import ark_java_client.ArkClient;
import com.arkaces.ApiException;
import com.arkaces.aces_listener_api.AcesListenerApi;
import com.arkaces.aces_server.aces_service.contract.Contract;
import com.arkaces.aces_server.aces_service.contract.ContractStatus;
import com.arkaces.aces_server.aces_service.contract.CreateContractRequest;
import com.arkaces.aces_server.aces_service.error.ServiceErrorCodes;
import com.arkaces.aces_server.common.api_key_generation.ApiKeyGenerator;
import com.arkaces.aces_server.common.error.NotFoundException;
import com.arkaces.aces_server.common.identifer.IdentifierGenerator;
import io.swagger.client.model.Subscription;
import io.swagger.client.model.SubscriptionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@Transactional
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ContractController {
    
    private final IdentifierGenerator identifierGenerator;
    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;
    private final AcesListenerApi arkListener;
    private final String arkEventCallbackUrl;
    private final Integer arkMinConfirmations;
    private final ApiKeyGenerator apiKeyGenerator;
    private final ArkClient arkClient;
    
    @PostMapping("/contracts")
    public Contract<Results> postContract(@RequestBody CreateContractRequest<Arguments> createContractRequest) {
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setId(identifierGenerator.generate());
        contractEntity.setCorrelationId(createContractRequest.getCorrelationId());
        contractEntity.setStatus(ContractStatus.EXECUTED);
        contractEntity.setCreatedAt(LocalDateTime.now());
        contractEntity.setRecipientEthAddress(createContractRequest.getArguments().getRecipientEthAddress());

        // Generate ark wallet for deposits
        String depositArkPassphrase = apiKeyGenerator.generate();
        contractEntity.setDepositArkPassphrase(depositArkPassphrase);
        String depositArkAddress = arkClient.getAddress(depositArkPassphrase);
        contractEntity.setDepositArkAddress(depositArkAddress);
        log.info("Deposit Ark Address: {} --- Deposit Ark Passphrase: {}", depositArkAddress, depositArkPassphrase);

        // Subscribe to ark listener on deposit ark address
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setCallbackUrl(arkEventCallbackUrl);
        subscriptionRequest.setMinConfirmations(arkMinConfirmations);
        subscriptionRequest.setRecipientAddress(depositArkAddress);

        Subscription subscription;
        try {
            subscription = arkListener.subscriptionsPost(subscriptionRequest);
        } catch (ApiException e) {
            throw new RuntimeException("Ark Listener subscription failed to POST", e);
        }
        contractEntity.setSubscriptionId(subscription.getId());

        contractRepository.save(contractEntity);

        log.info("Contract Entity: {}", contractEntity);

        return contractMapper.map(contractEntity);
    }
    
    @GetMapping("/contracts/{contractId}")
    public Contract<Results> getContract(@PathVariable String contractId) {
        ContractEntity contractEntity = contractRepository.findOneById(contractId);
        if (contractEntity == null) {
            throw new NotFoundException(ServiceErrorCodes.CONTRACT_NOT_FOUND, "Contract not found with id = " + contractId);
        }
        return contractMapper.map(contractEntity);
    }
}
