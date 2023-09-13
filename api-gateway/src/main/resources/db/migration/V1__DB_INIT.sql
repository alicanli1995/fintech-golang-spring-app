drop table if exists public.account cascade;
drop table if exists public.roles cascade;
drop table if exists public.user_roles cascade;
drop sequence if exists public.account_id_seq cascade;

-------------------------------------------------------

create sequence public.account_id_seq
    start with 1
    increment by 1
    no minvalue
    no maxvalue
    cache 1;

create table public.account
(
    id                 bigint       not null
        primary key,
    created_by         varchar(255),
    created_date       timestamp,
    created_ip         varchar(255),
    last_modified_by   varchar(255),
    last_modified_date timestamp,
    last_modified_ip   varchar(255),
    email              varchar(30)
        constraint uk_q0uja26qgu1atulenwup9rxyr
            unique,
    full_name          varchar(255),
    password           varchar(255) not null,
    phone              varchar(10),
    username           varchar(20)
        constraint uk_gex1lmaqpg0ir5g1f5eftyaa1
            unique
);

alter table public.account
    owner to postgres;

-------------------------------------------------------

create table public.roles
(
    id                 bigserial
        primary key,
    created_by         varchar(255),
    created_date       timestamp,
    created_ip         varchar(255),
    last_modified_by   varchar(255),
    last_modified_date timestamp,
    last_modified_ip   varchar(255),
    name               varchar(20)
);

alter table public.roles
    owner to postgres;

-------------------------------------------------------
create table public.user_roles
(
    user_id bigint not null
        constraint fkn6rghlrxo1uta1ffpj0puglmp
            references public.account,
    role_id bigint not null
        constraint fkh8ciramu9cc9q3qcqiv4ue8a6
            references public.roles,
    primary key (user_id, role_id)
);

alter table public.user_roles
    owner to postgres;
