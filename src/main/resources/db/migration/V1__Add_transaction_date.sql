-- V1__Add_transaction_date.sql
ALTER TABLE transactions 
ADD COLUMN transaction_date TIMESTAMP;