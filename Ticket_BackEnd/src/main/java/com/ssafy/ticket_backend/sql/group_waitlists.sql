CREATE TABLE IF NOT EXISTS public.group_waitlists
(
    groupwaitlist_id bigint NOT NULL DEFAULT nextval('waitlists_waitlist_id_seq'::regclass),
    ticket_ids bigint[],
    user_id bigint NOT NULL,
    game_id bigint NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    waitlist_status character varying(50) COLLATE pg_catalog."default",
    transaction_id bigint,
    number_of_people integer DEFAULT 1,
    CONSTRAINT group_waitlists_pkey PRIMARY KEY (groupwaitlist_id),
    CONSTRAINT unique_group_waitlist_ticket_user UNIQUE (ticket_ids, user_id),
    CONSTRAINT fk_group_waitlist_game FOREIGN KEY (game_id)
        REFERENCES public.games (game_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT fk_group_waitlist_user FOREIGN KEY (user_id)
        REFERENCES public.users (user_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
)