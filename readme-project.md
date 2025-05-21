# Spring AI Angular Project

## Visão Geral do Projeto api-ai

Este projeto é uma API Spring Boot que implementa integrações com modelos de IA usando o framework Spring AI. O projeto demonstra como integrar Large Language Models (LLMs) em aplicações Java, com funcionalidades como chat simples, chat com memória persistente e potencialmente RAG (Retrieval Augmented Generation).

## Estrutura e Tecnologias

### Tecnologias Principais
- **Spring Boot**: versão 3.4.5
- **Java**: versão 24
- **Spring AI**: versão 1.0.0-RC1 (Release Candidate)
- **PostgreSQL**: para armazenamento de dados
- **PGVector**: extensão PostgreSQL para armazenamento eficiente de vetores/embeddings

### Dependências Importantes
- Spring Boot Web
- Spring AI OpenAI Starter
- Spring AI Chat Memory Repository JDBC
- Spring AI PDF Document Reader
- Spring AI Vector Store
- Docker Compose

## Funcionalidades Implementadas

### 1. Chat Simples
- **Endpoint**: `/api/chat`
- Comunicação direta com modelos de IA (OpenAI)
- Não mantém histórico entre solicitações
- Implementação minimalista de interação com LLM

### 2. Chat com Memória
- **Endpoint**: `/api/chat-memory`
- Armazena histórico de conversas em banco de dados PostgreSQL
- Permite conversas persistentes com ID único
- Mantém até 10 mensagens no contexto da conversa
- Gera automaticamente descrições para as conversas

### 3. Outras Funcionalidades
- **booksprompt**: Trabalha com prompts específicos sobre livros
- **rag**: Implementação de Retrieval Augmented Generation (RAG) - uma técnica para buscar informações relevantes de uma base de conhecimento antes de gerar respostas

## Arquitetura
- Segue padrão MVC (Model-View-Controller) do Spring
- Uso de records Java para DTOs (ChatRequest e ChatResponse)
- Serviços separados para diferentes tipos de interação com IA
- Persistência de dados com JdbcChatMemoryRepository

## Integração
Este projeto é parte de uma solução maior que inclui um frontend em Angular, formando uma aplicação completa de demonstração de funcionalidades de IA.

## Como Executar
O projeto utiliza Docker Compose para facilitar a configuração do ambiente de desenvolvimento, incluindo a base de dados PostgreSQL necessária para o funcionamento da memória de chat e do armazenamento de vetores.

## Configuração
Para configurar este projeto, é necessário:
1. Configurar credenciais OpenAI no arquivo de propriedades
2. Executar o Docker Compose para iniciar o banco de dados
3. Iniciar a aplicação Spring Boot

---

Criado em: 21 de Maio de 2025