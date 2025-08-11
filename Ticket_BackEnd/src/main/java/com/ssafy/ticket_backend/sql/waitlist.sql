CREATE TABLE IF NOT EXISTS public.waitlists
(
    waitlist_id bigint NOT NULL DEFAULT nextval('waitlists_waitlist_id_seq'::regclass),
    ticket_id bigint,
    user_id bigint NOT NULL,
    game_id bigint NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    waitlist_status character varying(50) COLLATE pg_catalog."default",
    transaction_id bigint,
    CONSTRAINT waitlists_pkey PRIMARY KEY (waitlist_id),
    CONSTRAINT unique_waitlist_ticket_user UNIQUE (ticket_id, user_id),
    CONSTRAINT fk_waitlist_game FOREIGN KEY (game_id)
    REFERENCES public.games (game_id) MATCH SIMPLE
                         ON UPDATE NO ACTION
                         ON DELETE CASCADE,
    CONSTRAINT fk_waitlist_ticket FOREIGN KEY (ticket_id)
    REFERENCES public.tickets (ticket_id) MATCH SIMPLE
                         ON UPDATE NO ACTION
                         ON DELETE CASCADE,
    CONSTRAINT fk_waitlist_user FOREIGN KEY (user_id)
    REFERENCES public.users (user_id) MATCH SIMPLE
                         ON UPDATE NO ACTION
                         ON DELETE CASCADE
    )