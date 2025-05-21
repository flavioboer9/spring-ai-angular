-- Vector store table for RAG functionality
create table if not exists public.vector_stores (
  id uuid default uuid_generate_v4() primary key,
  content text not null,
  metadata jsonb,
  embedding vector(1536) not null,
  created_at timestamp with time zone default now()
);

-- Create HNSW index for efficient vector similarity search
create index if not exists vector_stores_embedding_idx 
on public.vector_stores 
using hnsw (embedding vector_cosine_ops);

comment on table public.vector_stores is 'Stores vector embeddings for documents used in Retrieval Augmented Generation (RAG)';
comment on column public.vector_stores.id is 'Unique identifier for the vector entry';
comment on column public.vector_stores.content is 'Text content associated with this embedding';
comment on column public.vector_stores.metadata is 'JSON metadata providing additional context for the embedding';
comment on column public.vector_stores.embedding is 'Vector representation of the content (1536 dimensions) for semantic search';
comment on column public.vector_stores.created_at is 'Timestamp when this vector entry was created';
