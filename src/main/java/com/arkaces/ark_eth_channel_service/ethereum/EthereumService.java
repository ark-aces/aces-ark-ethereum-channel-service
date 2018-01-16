package com.arkaces.ark_eth_channel_service.ethereum;

import com.arkaces.aces_server.common.json.NiceObjectMapper;
import com.arkaces.ark_eth_channel_service.utils.ByteUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EthereumService {

    private final RestTemplate ethereumRpcRestTemplate;
    private final EthereumWeiService ethereumWeiService;
    private final EthereumRpcRequestFactory ethereumRpcRequestFactory = new EthereumRpcRequestFactory();
    private final NiceObjectMapper objectMapper = new NiceObjectMapper(new ObjectMapper());

    public String sendTransaction(String senderAddress, String recipientAddress, BigDecimal etherValue) {
        SendTransaction sendTransaction = SendTransaction.builder()
                .from(Hex.encodeHexString(senderAddress.getBytes(StandardCharsets.UTF_8)))
                .to(Hex.encodeHexString(recipientAddress.getBytes(StandardCharsets.UTF_8)))
                .value(Hex.encodeHexString(ByteUtils.longToBytesNoLeadingZeros(ethereumWeiService.toWei(etherValue))))
                .build();
        HttpEntity<String> requestEntity = getRequestEntity("eth_sendTransaction", Collections.singletonList(sendTransaction));
        return hexToAscii(
                ethereumRpcRestTemplate.exchange(
                        "/",
                        HttpMethod.POST,
                        requestEntity,
                        new ParameterizedTypeReference<EthereumRpcResponse<String>>() {}
                )
                .getBody()
                .getResult()
        );
    }

    private HttpEntity<String> getRequestEntity(String method, List<Object> params) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");

        EthereumRpcRequest ethereumRpcRequest = ethereumRpcRequestFactory.create(method, params);
        String body = objectMapper.writeValueAsString(ethereumRpcRequest);

        return new HttpEntity<>(body, headers);
    }

    private String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }
}
