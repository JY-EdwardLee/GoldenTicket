CREATE TABLE tickets (
    id BIGSERIAL PRIMARY KEY,                       

    status VARCHAR(50) NOT NULL,                      
    price INTEGER NOT NULL,                           
    game_id BIGINT NOT NULL,                          -- FK: game 테이블 참조
    seat VARCHAR(100) NOT NULL,                       

    seller_id BIGINT NOT NULL,                        -- FK: users (양도자)
    buyer_id BIGINT,                                  -- FK: users (양수자) 

    transaction_date TIMESTAMP,                       
    matched_date TIMESTAMP,                      
    --link TEXT,                                        -- 티켓 확인 링크
    image TEXT,                                       -- 이미지 URL

    CONSTRAINT fk_ticket_game FOREIGN KEY (game_id)
        REFERENCES games(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_ticket_seller FOREIGN KEY (seller_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_ticket_buyer FOREIGN KEY (buyer_id)
        REFERENCES users(id)
        ON DELETE SET NULL							  -- 구매자는 기본값 null 사용
);
