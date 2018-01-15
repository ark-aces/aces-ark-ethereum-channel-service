# aces-service-ark-eth-channel

ACES ARK to ETH transfer channel service

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
  "recipientEthAddress": "ARNJJruY6RcuYCXcwWsu4bx9kyZtntqeAx"
}' 
```

```json
{
  "id": "abe05cd7-40c2-4fb0-a4a7-8d2f76e74978",
  "createdAt": "2017-07-04T21:59:38.129Z",
  "correlationId": "4aafe9-4a40-a7fb-6e788d2497f7",
  "status": "executed",
  "results": {
    "recipientEthAddress": "ARNJJruY6RcuYCXcwWsu4bx9kyZtntqeAx",
    "depositArkAddress": "5b83337a5af30bba26a55830a7d0ccf69114137ff699a3d718699ba1f498d77b",
    "transfers": []
  }
}
```

Get Contract information after sending ARK funds to `depositArkAddress`:

```bash
curl -X GET http://localhost:9190/contracts/{id}
```

```json
{
  "id": "abe05cd7-40c2-4fb0-a4a7-8d2f76e74978",
  "createdAt": "2017-07-04T21:59:38.129Z",
  "correlationId": "4aafe9-4a40-a7fb-6e788d2497f7",
  "status": "executed",
  "results": {
    "recipientEthAddress": "ARNJJruY6RcuYCXcwWsu4bx9kyZtntqeAx",
    "depositArkAddress": "5b83337a5af30bba26a55830a7d0ccf69114137ff699a3d718699ba1f498d77b",
    "transfers": [
      {
          "id": "fa046b0e-7b05-4a2d-a4c9-168951df3b90",
          "createdAt": "2017-07-05T21:00:38.457Z",
          "arkTransactionId": "49f55381c5c3c70f96e848df53ab7f9ae9881dbb8eb43e8f91f642018bf1258f",
          "arkAmount": "1.00000",
          "arkToEthRate": "0.00622",
          "arkFlatFee": "0.00000",
          "arkPercentFee": "1.00000",
          "arkTotalFee": "0.01000",
          "ethTransactionId": "49f55381c5c3c70f96e848df53ab7f9ae9881dbb8eb43e8f91f642018bf1258f",
          "ethSendAmount": "0.0062822"
      }
    ]
  }
}
```
