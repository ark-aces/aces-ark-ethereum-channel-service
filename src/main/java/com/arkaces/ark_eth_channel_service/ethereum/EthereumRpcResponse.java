package com.arkaces.ark_eth_channel_service.ethereum;

import lombok.Data;

@Data
public class EthereumRpcResponse<T> {

    private Integer id;
    private String jsonrpc;
    private T result;
}
