create table admins
(
    id                  bigint auto_increment
        primary key,
    credentials_expired bit          not null,
    enabled             bit          not null,
    expired             bit          not null,
    locked              bit          not null,
    password            varchar(255) not null,
    username            varchar(255) not null,
    constraint UK5gr02hdrjhbm2sh88og0t7ic6
        unique (username)
);

create table admin_roles
(
    admin_id bigint not null,
    role     varchar(255) null,
    constraint FKos10nu865i674o95ba9m5v1bg
        foreign key (admin_id) references admins (id)
            on delete cascade
);

create table password_reset_tokens
(
    id            bigint auto_increment
        primary key,
    expires_at    datetime(6)  not null,
    used          bit          not null,
    used_at       datetime(6)  null,
    user_email    varchar(255) not null,
    user_fullname varchar(255) not null,
    token_hash    varchar(255) not null,
    constraint UKeouy94p9xchcrat7pyvrsnfdq
        unique (token_hash)
);

create table users
(
    id                  int auto_increment
        primary key,
    pesel               varchar(255) null,
    date_of_birth       date null,
    lastname            varchar(255) null,
    firstname           varchar(255) null,
    email               varchar(255) null,
    password            varchar(255) null,
    expired             tinyint(1) default 0 not null,
    locked              tinyint(1) default 0 not null,
    enabled             tinyint(1) default 1 not null,
    credentials_expired bit          not null,
    username            varchar(255) not null,
    account_counter     int null,
    phone_number        varchar(255) not null,
    constraint UKitp610ijy6ku9afo2it8wajii
        unique (pesel)
);

create table accounts
(
    id                  int auto_increment
        primary key,
    balance             decimal(38, 2) null,
    owner_id            int null,
    creation_date       datetime(6)    null,
    iban                varchar(255) not null,
    user_account_number int          not null,
    available_balance   decimal(38, 2) null,
    constraint FKjln86358moqf5k5pw89oiq8ur
        foreign key (owner_id) references users (id)
            on delete cascade
);

create table transactions
(
    id             int auto_increment
        primary key,
    amount         decimal(38, 2) null,
    date           datetime(6)                                                                                     null,
    status         varchar(20) null,
    title          varchar(100) null,
    type           enum ('DEPOSIT', 'FEE', 'TRANSFER_EXTERNAL', 'TRANSFER_INTERNAL', 'TRANSFER_OWN', 'WITHDRAWAL') null,
    destination_id int null,
    source_id      int null,
    constraint FK4030424h0rpxm0q0tnl74pbkh
        foreign key (destination_id) references accounts (id),
    constraint FKnkrduafehebfdd3udxvp2c13r
        foreign key (source_id) references accounts (id)
);

create table user_roles
(
    user_id int not null,
    role    varchar(255) null,
    constraint FK7ov27fyo7ebsvada1ej7qkphl
        foreign key (user_id) references users (id)
            on delete cascade
);

