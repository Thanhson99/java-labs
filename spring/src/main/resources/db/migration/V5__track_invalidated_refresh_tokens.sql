create table if not exists invalidated_refresh_tokens (
    token_hash varchar(128) primary key,
    username varchar(64) not null,
    session_id varchar(64) not null,
    session_label varchar(128) not null,
    invalidated_at timestamp not null,
    invalidation_reason varchar(32) not null
);

create index if not exists idx_invalidated_refresh_tokens_username on invalidated_refresh_tokens(username);
