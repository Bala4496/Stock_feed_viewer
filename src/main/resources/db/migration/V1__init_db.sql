create table companies
(
    id   serial primary key,
    code varchar(4)  not null unique,
    name varchar(64) not null unique
);

create table quotes
(
    id             serial primary key,
    company_code   varchar(4)              not null
        constraint quotes_companies_id_fk references companies (code) on delete cascade,
    price          numeric(10, 2)          not null,
    gap_percentage numeric(10, 2)          not null,
    created_at     timestamp default now() not null
);
