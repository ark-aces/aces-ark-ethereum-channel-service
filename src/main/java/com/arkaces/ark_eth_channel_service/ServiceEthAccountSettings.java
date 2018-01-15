package com.arkaces.ark_eth_channel_service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "serviceArkAccount")
public class ServiceEthAccountSettings {

    private String address;
    private String passphrase;
}
