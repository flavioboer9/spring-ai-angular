-- Chat memory table for storing chat session IDs and metadata
create table if not exists public.chat_memories (
  id varchar(256) primary key,
  user_id varchar(256) not null,
  description varchar(256),
  created_at timestamp with time zone default now()
);

comment on table public.chat_memories is 'Stores chat session identifiers and basic metadata used by the Spring AI application for conversation tracking';
comment on column public.chat_memories.id is 'Unique identifier for the chat conversation';
comment on column public.chat_memories.user_id is 'User identifier associated with this conversation';
comment on column public.chat_memories.description is 'Short description of the conversation topic or purpose';
comment on column public.chat_memories.created_at is 'Timestamp when this conversation was created';
