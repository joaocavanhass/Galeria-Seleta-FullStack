// ============================================================
// ARQUIVO: GaleriaSelataApplication.java
// FUNÇÃO: Ponto de entrada da aplicação. É aqui que tudo começa.
// Quando você roda o comando "mvnw.cmd spring-boot:run", a JVM
// (máquina virtual Java) procura este arquivo e executa o método main().
// ============================================================

package com.galeriaseleta; // Define o "endereço" deste arquivo dentro do projeto

// Importações: trazem funcionalidades externas para dentro deste arquivo
import org.springframework.boot.SpringApplication;           // Responsável por iniciar o servidor
import org.springframework.boot.autoconfigure.SpringBootApplication; // Anotação principal do Spring Boot

// @SpringBootApplication é uma anotação "tudo em um" que faz três coisas ao mesmo tempo:
// 1. @SpringBootConfiguration  → marca esta classe como configuração principal
// 2. @EnableAutoConfiguration  → ativa a configuração automática do Spring Boot
//    (detecta as bibliotecas no projeto e configura tudo automaticamente)
// 3. @ComponentScan            → o Spring percorre o pacote "com.galeriaseleta" inteiro
//    procurando classes marcadas com @Controller, @Service, @Repository, etc.
//    e registra todas automaticamente — você não precisa declarar cada uma.
@SpringBootApplication
public class GaleriaSelataApplication {

    // main() é o método que a JVM executa ao iniciar o programa.
    // É o equivalente ao "play" de uma aplicação Java.
    public static void main(String[] args) {

        // SpringApplication.run() inicializa tudo:
        // - Conecta ao banco de dados SQLite
        // - Registra todos os controllers, services e repositories
        // - Executa o schema.sql para criar as tabelas
        // - Executa o AdminInitializer (cria admin, categorias e fretes)
        // - Sobe o servidor na porta 8080
        // A partir daqui a API está disponível em http://localhost:8080
        SpringApplication.run(GaleriaSelataApplication.class, args);
    }
}
