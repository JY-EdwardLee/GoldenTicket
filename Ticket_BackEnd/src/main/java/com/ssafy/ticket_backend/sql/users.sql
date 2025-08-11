CREATE TABLE IF NOT EXISTS public.users
(
    user_id
    bigint
    NOT
    NULL
    DEFAULT
    nextval
(
    'users_user_id_seq'
    :
    :
    regclass
),
    email character varying
(
    255
) COLLATE pg_catalog."default" NOT NULL,
    gender character varying
(
    10
) COLLATE pg_catalog."default",
    user_name character varying
(
    100
) COLLATE pg_catalog."default",
    user_role character varying
(
    50
) COLLATE pg_catalog."default",
    nick_name character varying
(
    100
) COLLATE pg_catalog."default",
    birth_date date,
    profile_photo_url text COLLATE pg_catalog."default",
    phone_number character varying
(
    20
) COLLATE pg_catalog."default",
    my_team character varying
(
    50
) COLLATE pg_catalog."default",
    social_provider character varying
(
    50
) COLLATE pg_catalog."default",
    weight numeric
(
    5,
    2
) DEFAULT 0.0,
    transfer_number integer DEFAULT 0,
    receive_number integer DEFAULT 0,
    panelty_point integer DEFAULT 0,
    is_block boolean DEFAULT false,
    is_delete boolean DEFAULT false,
    CONSTRAINT users_pkey PRIMARY KEY
(
    user_id
),
    CONSTRAINT users_email_key UNIQUE
(
    email
)
    )