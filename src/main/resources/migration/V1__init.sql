CREATE TABLE contracts (
  pid BIGSERIAL PRIMARY KEY,
  id VARCHAR(255) NOT NULL,
  recipientEthAddress
  created_at TIMESTAMP
);

CREATE TABLE transfers (
  pid BIGSERIAL PRIMARY KEY,
  id VARCHAR(255) NOT NULL,
  created_at TIMESTAMP,
  contract_pid BIGINT NOT NULL,
  status VARCHAR(255),
  ark_transaction_id VARCHAR(255),
  ark_amount DECIMAL(8,5),
  ark_to_ark_rate DECIMAL(8,5),
  ark_flat_fee DECIMAL(8,5),
  ark_percent_fee DECIMAL(8,5),
  ark_total_fee DECIMAL(8,5),
  eth_transaction_id VARCHAR(255),
  eth_send_amount DECIMAL(8,5)
);

ALTER TABLE transfers ADD CONSTRAINT FOREIGN KEY (contract_pid) REFERENCES contracts (pid);
