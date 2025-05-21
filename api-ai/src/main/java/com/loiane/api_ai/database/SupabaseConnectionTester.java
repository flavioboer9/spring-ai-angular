package com.loiane.api_ai.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Classe simples para testar a conexão com o Supabase
 * Independente do Spring Boot, pode ser executada diretamente
 */
public class SupabaseConnectionTester {

    // Configurações de conexão do Supabase (conforme definido no application.properties)
    private static final String URL = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:5432/postgres";
    private static final String USER = "postgres.plpnjwofkcohvnntdfmo";
    private static final String PASSWORD = "Fhbsupabase1@";

    public static void main(String[] args) {
        System.out.println("Testando conexão com Supabase...");
        
        try {
            // Carrega o driver JDBC do PostgreSQL
            Class.forName("org.postgresql.Driver");
            
            // Tenta estabelecer conexão
            System.out.println("Conectando a " + URL);
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            
            if (conn != null) {
                System.out.println("Conexão estabelecida com sucesso!");
                
                // Obtém metadados da conexão
                System.out.println("URL de conexão: " + conn.getMetaData().getURL());
                System.out.println("Versão do banco: " + conn.getMetaData().getDatabaseProductVersion());
                
                // Executa uma consulta de teste
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT current_database()");
                
                if (rs.next()) {
                    System.out.println("Banco de dados atual: " + rs.getString(1));
                }
                
                // Verifica se a extensão pgvector está habilitada
                rs = stmt.executeQuery("SELECT EXISTS (SELECT 1 FROM pg_extension WHERE extname = 'vector')");
                if (rs.next()) {
                    boolean pgvectorEnabled = rs.getBoolean(1);
                    System.out.println("Extensão pgvector habilitada: " + (pgvectorEnabled ? "SIM" : "NAO"));
                }
                
                // Fecha a conexão
                conn.close();
                System.out.println("Conexão fechada");
            }
        } catch (Exception e) {
            System.out.println("ERRO ao conectar ao Supabase: " + e.getMessage());
            e.printStackTrace();
        }
    }
}