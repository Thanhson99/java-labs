create table if not exists user_profiles (
    user_id varchar(64) primary key,
    email varchar(255) not null unique,
    region varchar(16) not null,
    constraint chk_user_profiles_region check (region in ('APAC', 'EU', 'US'))
);
