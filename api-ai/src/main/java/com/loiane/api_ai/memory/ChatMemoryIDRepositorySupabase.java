package com.loiane.api_ai.memory;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repositório adaptado para trabalhar com a versão Supabase das tabelas de memória de chat
 */
@Repository
public class ChatMemoryIDRepositorySupabase {

    private final JdbcTemplate jdbcTemplate;

    public ChatMemoryIDRepositorySupabase(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String generateChatId(String userId) {
        String sql = "INSERT INTO chat_memories (user_id, id) VALUES (?, ?)";
        String chatId = generateUniqueChatId();
        jdbcTemplate.update(sql, userId, chatId);
        return chatId;
    }

    private String generateUniqueChatId() {
        return java.util.UUID.randomUUID().toString();
    }

    public boolean chatIdExists(String chatId) {
        String sql = "SELECT COUNT(*) FROM spring_ai_chat_memories WHERE conversation_id = ? AND type = 'USER'";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{chatId}, Integer.class);
        return count != null && count == 1;
    }

    public void updateDescription(String chatId, String description) {
        String sql = "UPDATE chat_memories SET description = ? WHERE id = ?";
        jdbcTemplate.update(sql, description, chatId);
    }
}
