-- 시퀀스 생성 (1부터 시작)
CREATE SEQUENCE IF NOT EXISTS group_transaction_states_transaction_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- 테이블 생성
CREATE TABLE IF NOT EXISTS public.group_transaction_states
(
    transaction_id bigint NOT NULL DEFAULT nextval('grouptransactionlist_transaction_id_seq'::regclass),
    ticket_id bigint[] NOT NULL,
    seller_id bigint NOT NULL,
    buyer_id bigint,
    transaction_status character varying(50) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT grouptransactionlist_pkey PRIMARY KEY (transaction_id),
    CONSTRAINT fk_grouptransactionlist_buyer FOREIGN KEY (buyer_id)
    REFERENCES public.users (user_id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE CASCADE,
    CONSTRAINT fk_grouptransactionlist_seller FOREIGN KEY (seller_id)
    REFERENCES public.users (user_id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE CASCADE
    );
