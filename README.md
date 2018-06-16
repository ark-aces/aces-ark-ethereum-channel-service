# aces-service-ark-eth-channel

ACES ARK to ETH transfer channel service

## Run Application

1. Setup configuration in `application.yml`
2. Start service: `mvn spring-boot:run`

### Want to run via docker?

1. Set correct environment variables in `docker-compose.yml`
2. Start service: `docker-compose up`

## Using Service

Get service info:

```bash
curl http://localhost:9190/
```

```json
{
  "name" : "Aces ARK-ETH Channel Service",
  "description" : "ACES ARK to ETH Channel service for transferring ARK to ETH",
  "version" : "1.0.0",
  "websiteUrl" : "https://ethaces.com",
  "instructions" : "After this contract is executed, any ARK sent to depositArkAddress will be exchanged for ETH and sent directly to the given recipientEthAddress less service fees.",
  "flatFee" : "0",
  "percentFee" : "1.00%",
  "inputSchema" : {
    "type" : "object",
    "properties" : {
      "recipientEthAddress" : {
        "type" : "string"
      }
    },
    "required" : [ "recipientEthAddress" ]
  },
  "outputSchema" : {
    "type" : "object",
    "properties" : {
      "depositArkAddress" : {
        "type" : "string"
      },
      "recipientEthAddress" : {
        "type" : "string"
      },
      "transfers" : {
        "type" : "array",
        "properties" : {
          "id" : {
            "type" : "string"
          },
          "createdAt" : {
            "type" : "string"
          },
          "arkTransactionId" : {
            "type" : "string"
          },
          "arkAmount" : {
            "type" : "string"
          },
          "arkToEthRate" : {
            "type" : "string"
          },
          "arkFlatFee" : {
            "type" : "string"
          },
          "arkPercentFee" : {
            "type" : "string"
          },
          "arkTotalFee" : {
            "type" : "string"
          },
          "ethTransactionId" : {
            "type" : "string"
          },
          "ethSendAmount" : {
            "type" : "string"
          }
        }
      }
    }
  }
}
```

Create a new Service Contract:

```bash
curl -X POST localhost:9190/contracts \
-H 'Content-type: application/json' \
-d '{
  "arguments": {
    "recipientEthAddress": "0xcfd866733c2192311add9836f0e0cf50daba16a7"
  }
}'
```

```json
{
  "id": "JEZfPklZ4H0ygmoqZFUd",
  "createdAt": "2018-01-28T19:17:24.725Z",
  "status": "executed",
  "results": {
    "recipientEthAddress": "0xcfd866733c2192311add9836f0e0cf50daba16a7",
    "depositArkAddress": "Aa1aGP6aF8z1LqYiNB8P86ozzfZ3TRFjBV",
    "transfers": []
  }
}
```

Get Contract information after sending ARK funds to `depositArkAddress`:

```bash
curl -X GET http://localhost:9190/contracts/JEZfPklZ4H0ygmoqZFUd
```

```json
{
    "id": "JEZfPklZ4H0ygmoqZFUd",
    "createdAt": "2018-01-28T19:17:24.725Z",
    "status": "executed",
    "results": {
        "recipientEthAddress": "0xcfd866733c2192311add9836f0e0cf50daba16a7",
        "depositArkAddress": "Aa1aGP6aF8z1LqYiNB8P86ozzfZ3TRFjBV",
        "transfers": [
            {
                "id": "JkvWCbxitSM6RC2FsxnJ",
                "status": "complete",
                "createdAt": "2018-01-28T20:03:54.753Z",
                "arkTransactionId": "262facac6267e84951a6c92416bd11dc31bda787271e77ab3ec3aef48aadc33e",
                "arkAmount": "0.10000000",
                "arkToEthRate": "0.00535500",
                "arkFlatFee": "0.00000000",
                "arkPercentFee": "1.00000000",
                "arkTotalFee": "0.00100000",
                "ethTransactionId": "0x009f710213ca3088d8e18cd7184f5b90c007bd1980895324bb633452bf94428f",
                "ethSendAmount": "0.00053550"
            }
        ]
    }
}
```
