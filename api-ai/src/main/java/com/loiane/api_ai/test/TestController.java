package com.loiane.api_ai.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador simples para testes
 */
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    public TestController() {
        log.info("TestController inicializado");
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        log.info("Endpoint hello acessado");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello World!");
        return response;
    }
}
