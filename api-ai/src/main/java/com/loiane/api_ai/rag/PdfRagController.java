package com.loiane.api_ai.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.MediaType;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Controlador de teste básico para verificar o mapeamento REST
 */
@RestController
public class PdfRagController {
    
    private static final Logger log = LoggerFactory.getLogger(PdfRagController.class);

    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;
    
    public PdfRagController(VectorStore vectorStore, EmbeddingModel embeddingModel) {
        this.vectorStore = vectorStore;
        this.embeddingModel = embeddingModel;
        log.info("PdfRagController inicializado com caminhos: /rag/test, /rag/status, /rag/pdfVector");
    }

    @GetMapping("/rag/test")
    public Map<String, String> test() {
        log.info("Endpoint de teste acessado");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Teste bem-sucedido!");
        return response;
    }

    @GetMapping("/rag/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        log.info("Endpoint de status acessado");
        Map<String, Object> status = new HashMap<>();
        status.put("status", "online");
        status.put("vectorStore", vectorStore.getClass().getSimpleName());
        status.put("embeddingModel", embeddingModel.getClass().getSimpleName());
        
        return ResponseEntity.ok(status);
    }

       /**
     * Endpoint para upload de arquivos PDF
     * @param file O arquivo PDF a ser processado
     * @param chunkSize Tamanho do chunk para divisão do documento (opcional, padrão 1000)
     * @param overlap Sobreposição entre chunks (opcional, padrão 200)
     * @return Resposta com detalhes sobre o processamento
     */
    @PostMapping(value = "/rag/pdfVector", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> uploadPdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "chunkSize", defaultValue = "1000") int chunkSize,
            @RequestParam(value = "overlap", defaultValue = "200") int overlap) {
        
        log.info("Método uploadPdf chamado! Arquivo: {}, tamanho: {} bytes, tipo: {}", 
                file.getOriginalFilename(), file.getSize(), file.getContentType());
        log.info("Parâmetros: chunkSize={}, overlap={}", chunkSize, overlap);
        
        Map<String, Object> response = new HashMap<>();
        Path tempFile = null;
        
        try {
            if (file.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Arquivo vazio. Por favor, envie um arquivo PDF válido.");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (!file.getContentType().equals("application/pdf")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Apenas arquivos PDF são suportados.");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Criar arquivo temporário
            String originalFilename = file.getOriginalFilename();
            String fileId = UUID.randomUUID().toString();
            tempFile = Files.createTempFile("upload-" + fileId + "-", ".pdf");
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
            log.info("Arquivo temporário criado: {}", tempFile);
            
            // Configurar leitor PDF
            PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(
                tempFile.toUri().toString(),
                PdfDocumentReaderConfig.builder()
                    .withPageTopMargin(0)
                    .withPagesPerDocument(1)
                    .build()
            );
            
            // Ler documento
            log.info("Processando o documento PDF...");
            List<Document> documents = pdfReader.read();
            
            // Criar nova lista com documentos adicionando metadados
            List<Document> enhancedDocuments = new ArrayList<>();
            for (Document doc : documents) {
                Map<String, Object> metadata = new HashMap<>();
                if (doc.getMetadata() != null) {
                    metadata.putAll(doc.getMetadata());
                }
                metadata.put("source", originalFilename);
                metadata.put("uploadDate", System.currentTimeMillis());
                metadata.put("fileId", fileId);
                
                // Criar um novo documento com os metadados atualizados
                Document enhancedDoc = new Document(
                    doc.getId(), 
                    doc.getText(), 
                    metadata
                );
                enhancedDocuments.add(enhancedDoc);
            }
            
            // Dividir em chunks menores para processamento eficiente
            log.info("Dividindo documento em chunks (tamanho padrão)");
            TextSplitter textSplitter = new TokenTextSplitter();
            List<Document> splitDocuments = textSplitter.apply(enhancedDocuments);
            
            // Adicionar ao armazenamento vetorial
            log.info("Adicionando {} chunks ao armazenamento vetorial", splitDocuments.size());
            vectorStore.add(splitDocuments);
            
            response.put("success", true);
            response.put("message", "Documento processado com sucesso!");
            response.put("filename", originalFilename);
            response.put("fileId", fileId);
            response.put("totalChunks", splitDocuments.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao processar o documento", e);
            response.put("success", false);
            response.put("message", "Erro ao processar o documento: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } finally {
            // Limpar arquivo temporário
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                    log.info("Arquivo temporário removido: {}", tempFile);
                } catch (IOException e) {
                    log.warn("Não foi possível remover o arquivo temporário: {}", tempFile, e);
                }
            }
        }
    }
}
