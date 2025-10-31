package br.edu.ifpr.gep.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Configuração manual do Spring para apps JavaFX.
 * Inicia o contexto e permite injeção de beans.
 */
@SpringBootApplication(scanBasePackages = "br.edu.ifpr.gep")  // Escaneia pacotes do projeto
public class SpringConfig {
    private static ConfigurableApplicationContext context;

    public static void start() {
        context = SpringApplication.run(SpringConfig.class);
        System.out.println("Contexto Spring iniciado com MongoDB.");
    }

    public static void stop() {
        if (context != null) {
            context.close();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz) {
        return (T) context.getBean(clazz);
    }
}