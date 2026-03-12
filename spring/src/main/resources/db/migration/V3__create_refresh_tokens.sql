create table if not exists refresh_tokens (
    token varchar(128) primary key,
    username varchar(64) not null,
    expires_at timestamp not null,
    created_at timestamp not null
);

create index if not exists idx_refresh_tokens_username on refresh_tokens(username);
