package com.arkaces.ark_eth_channel_service.ethereum;

import com.arkaces.aces_server.common.json.NiceObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EthereumService {

    private final EthereumWeiService ethereumWeiService;
    private final NiceObjectMapper objectMapper = new NiceObjectMapper(new ObjectMapper());
    private final EthereumRpcRequestFactory ethereumRpcRequestFactory = new EthereumRpcRequestFactory();
    private final RestTemplate ethereumRpcRestTemplate;

    public BigDecimal getBalance(String address) {
        HttpEntity<String> requestEntity = getRequestEntity("eth_getBalance", Arrays.asList(address, "latest"));
        String balanceHex = ethereumRpcRestTemplate.exchange(
                "/",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<EthereumRpcResponse<String>>() {}
        ).getBody().getResult();

        BigInteger wei = new BigInteger(balanceHex.replaceFirst("0x", ""), 16);

        return ethereumWeiService.toEther(wei);
    }

    public String sendTransaction(String from, String to, BigDecimal etherValue) {
        BigInteger wei = ethereumWeiService.toWei(etherValue);
        String value = getHexStringFromWei(wei);
        SendTransaction sendTransaction = SendTransaction.builder()
                .from(from)
                .to(to)
                .value(value)
                .build();
        HttpEntity<String> requestEntity = getRequestEntity("eth_sendTransaction", Collections.singletonList(sendTransaction));
        return ethereumRpcRestTemplate.exchange(
                "/",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<EthereumRpcResponse<String>>() {}
        ).getBody().getResult();
    }

    private String getHexStringFromWei(BigInteger wei) {
        return "0x" + removeLeadingZeros(wei.toString(16));
    }

    private String removeLeadingZeros(String s) {
        int index = findFirstNonZeroIndex(s);
        if (index == -1) {
            return "0";
        }
        return s.substring(index);
    }

    private int findFirstNonZeroIndex(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != '0') {
                return i;
            }
        }
        return -1;
    }

    private HttpEntity<String> getRequestEntity(String method, List<Object> params) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");

        EthereumRpcRequest ethereumRpcRequest = ethereumRpcRequestFactory.create(method, params);
        String body = objectMapper.writeValueAsString(ethereumRpcRequest);

        return new HttpEntity<>(body, headers);
    }
}
