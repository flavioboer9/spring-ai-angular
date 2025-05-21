package com.loiane.api_ai.rag.mongo;

import com.loiane.api_ai.chat.ChatRequest;
import org.springframework.ai.document.Document;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RagReadVectorDbController {

    // Usando System.out ao invés de logger para evitar problemas de compilação
    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public RagReadVectorDbController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
        System.out.println("RagReadVectorDbController inicializado com vectorStore: " + 
                vectorStore.getClass().getSimpleName());
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchVectorStore(@RequestBody ChatRequest request) {
        try {
            if (request.message() == null || request.message().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Query não pode ser vazia");
                return ResponseEntity.badRequest().body(error);
            }
            
            System.out.println("Realizando busca por similaridade para: '" + request.message() + "'");
            
            // Retrieve documents similar to a query
            List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                    .query(request.message())
                    .topK(5)  // Aumentado para 5 resultados
                    .build()
            );
            
            System.out.println("Encontrados " + results.size() + " documentos similares");
            
            if (results == null || results.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Nenhum documento encontrado para esta consulta");
                return ResponseEntity.ok(response);
            }
            
            StringBuilder combinedText = new StringBuilder();
            for (Document doc : results) {
                String text = doc.getText();
                Map<String, Object> metadata = doc.getMetadata();
                String source = metadata != null && metadata.containsKey("source") ? 
                        "Fonte: " + metadata.get("source") : "Fonte desconhecida";
                combinedText.append(text).append("\n").append(source).append("\n----------\n");
            }
                    
            Map<String, Object> response = new HashMap<>();
            response.put("results", combinedText);
            response.put("count", results.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Erro ao buscar documentos no vector store: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao processar a consulta: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @GetMapping(value = "/query")
    public ResponseEntity<?> queryByParam(@RequestParam(required = false) String query) {
        if (query == null || query.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Parâmetro 'query' é obrigatório");
            return ResponseEntity.badRequest().body(error);
        }
        
        ChatRequest request = new ChatRequest(query);
        return searchVectorStore(request);
    }
}
