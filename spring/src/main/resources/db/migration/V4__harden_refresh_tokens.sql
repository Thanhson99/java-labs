alter table refresh_tokens rename column token to token_hash;
alter table refresh_tokens add column session_id varchar(64);
alter table refresh_tokens add column session_label varchar(128);

update refresh_tokens
set session_id = 'legacy-session',
    session_label = 'legacy-session'
where session_id is null;

alter table refresh_tokens alter column session_id set not null;
alter table refresh_tokens alter column session_label set not null;

alter table refresh_tokens add constraint uk_refresh_tokens_session_id unique (session_id);
create index if not exists idx_refresh_tokens_session_label on refresh_tokens(session_label);
