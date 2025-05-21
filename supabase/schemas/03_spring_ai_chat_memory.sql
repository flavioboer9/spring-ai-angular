-- Spring AI chat message storage table
create table if not exists public.spring_ai_chat_memories (
  conversation_id varchar(256) not null,
  type varchar(50) not null,
  position int not null,
  content text,
  metadata text,
  created_at timestamp with time zone default now(),
  primary key (conversation_id, type, position)
);

comment on table public.spring_ai_chat_memories is 'Stores chat message history for Spring AI conversations with memory persistence';
comment on column public.spring_ai_chat_memories.conversation_id is 'Unique identifier linking messages to a specific conversation';
comment on column public.spring_ai_chat_memories.type is 'Message type (USER or ASSISTANT)';
comment on column public.spring_ai_chat_memories.position is 'Sequential position of the message within the conversation';
comment on column public.spring_ai_chat_memories.content is 'Actual content of the chat message';
comment on column public.spring_ai_chat_memories.metadata is 'Additional metadata for the message stored as JSON string';
comment on column public.spring_ai_chat_memories.created_at is 'Timestamp when the message was created';
