CREATE TABLE transaction_state
(
    id        BIGSERIAL PRIMARY KEY,

    ticket_id BIGINT      NOT NULL,
    seller_id BIGINT      NOT NULL,
    buyer_id  BIGINT,

    status    VARCHAR(50) NOT NULL, -- 결제 전, 결제 완료, 관람완료, 결제 취소, 보류

    CONSTRAINT fk_transaction_ticket FOREIGN KEY (ticket_id)
        REFERENCES tickets (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_transaction_seller FOREIGN KEY (seller_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_transaction_buyer FOREIGN KEY (buyer_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);
