// ============================================================
// ARQUIVO: AdminInitializer.java
// FUNÇÃO: Executado automaticamente toda vez que o servidor inicia.
// Garante que o banco de dados tenha os dados mínimos necessários:
// - Usuário administrador padrão
// - Categorias de produtos
// - Opções de frete
// Pense nele como um "setup inicial" que roda antes da aplicação
// estar disponível para os usuários.
// ============================================================

package com.galeriaseleta.config;

// Importações dos modelos (entidades que representam tabelas do banco)
import com.galeriaseleta.model.Categoria;
import com.galeriaseleta.model.OpcaoFrete;
import com.galeriaseleta.model.Usuario;

// Importações dos repositórios (interfaces para acessar o banco de dados)
import com.galeriaseleta.repository.CategoriaRepository;
import com.galeriaseleta.repository.OpcaoFreteRepository;
import com.galeriaseleta.repository.UsuarioRepository;

// ApplicationRunner: interface do Spring Boot que garante que o método run()
// seja chamado automaticamente logo após a aplicação iniciar
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

// JdbcTemplate: permite executar SQL puro diretamente no banco,
// sem precisar criar um model/repository para isso
import org.springframework.jdbc.core.JdbcTemplate;

// PasswordEncoder: serviço que criptografa senhas antes de salvar no banco.
// Nunca salvamos senha em texto puro — o BCrypt transforma "admin123"
// em uma string embaralhada como "$2a$10$..."
import org.springframework.security.crypto.password.PasswordEncoder;

// @Component informa ao Spring que esta classe deve ser gerenciada por ele.
// O Spring a cria automaticamente e a injeta onde for necessário.
import org.springframework.stereotype.Component;

import java.math.BigDecimal; // Usado para valores monetários com precisão decimal

// @Component: registra esta classe no Spring para que ele a gerencie e execute
@Component
public class AdminInitializer implements ApplicationRunner { // implements ApplicationRunner = roda ao iniciar

    // Atributos: dependências que esta classe precisa para funcionar.
    // São declaradas aqui e fornecidas pelo Spring (injeção de dependência).
    private final UsuarioRepository usuarioRepository;     // Acessa a tabela de usuários / O final indica que só pode receber valor uma única vez.
    private final CategoriaRepository categoriaRepository; // Acessa a tabela de categorias
    private final OpcaoFreteRepository opcaoFreteRepository; // Acessa a tabela de fretes
    private final PasswordEncoder passwordEncoder;         // Criptografa senhas
    private final JdbcTemplate jdbc;                       // Executa SQL puro

    // Construtor: o Spring chama este método e passa as dependências automaticamente.
    // Isso chama "injeção de dependência por construtor" — é a forma mais segura.
    public AdminInitializer(UsuarioRepository usuarioRepository,
                            CategoriaRepository categoriaRepository,
                            OpcaoFreteRepository opcaoFreteRepository,
                            PasswordEncoder passwordEncoder,
                            JdbcTemplate jdbc) {
        this.usuarioRepository    = usuarioRepository;
        this.categoriaRepository  = categoriaRepository;
        this.opcaoFreteRepository = opcaoFreteRepository;
        this.passwordEncoder      = passwordEncoder;
        this.jdbc                 = jdbc;
    }

    // run() é chamado automaticamente pelo Spring após a aplicação subir.
    // ApplicationArguments são argumentos de linha de comando — não usados aqui.
    @Override
    public void run(ApplicationArguments args) {
        migrarColunas(); // 1. Garante que colunas novas existam no banco (migrações)
        garantirAdmin(); // 2. Cria o usuário admin se não existir
        seedCategorias(); // 3. Popula as categorias iniciais se o banco estiver vazio
        seedFretes();     // 4. Popula as opções de frete se o banco estiver vazio
    }

    // Migrações seguras: tenta adicionar colunas novas ao banco.
    // Como o banco SQLite já pode existir de versões anteriores do projeto,
    // precisamos garantir que as colunas novas existam sem quebrar dados antigos.
    private void migrarColunas() {
        tryAlter("ALTER TABLE usuarios     ADD COLUMN papel      TEXT NOT NULL DEFAULT 'cliente'");
        tryAlter("ALTER TABLE usuarios     ADD COLUMN cpf        TEXT");
        tryAlter("ALTER TABLE carrinho     ADD COLUMN quantidade INTEGER NOT NULL DEFAULT 1");
        tryAlter("ALTER TABLE itens_pedido ADD COLUMN quantidade INTEGER NOT NULL DEFAULT 1");
    }

    // Executa um ALTER TABLE e ignora o erro se a coluna já existir.
    // SQLite lança exceção ao tentar adicionar uma coluna duplicada,
    // então usamos try/catch para ignorar esse caso sem travar a aplicação.
    private void tryAlter(String sql) {
        try { jdbc.execute(sql); } catch (Exception ignored) {}
    }

    // Verifica se o usuário admin existe no banco.
    // Se não existir, cria com email e senha padrão.
    // Se existir mas sem papel "admin", corrige o papel.
    private void garantirAdmin() {
        String adminEmail = "admin@galeriaseleta.com";

        // findByEmail retorna Optional<Usuario> — um container que pode ou não ter valor
        if (usuarioRepository.findByEmail(adminEmail).isEmpty()) {
            // Admin não existe: cria um novo
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail(adminEmail);
            // encode() criptografa a senha. Nunca salve senhas em texto puro!
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setTelefone("(00) 00000-0000");
            admin.setPapel("admin"); // Define o papel como administrador
            usuarioRepository.save(admin); // Salva no banco de dados
            System.out.println(">>> Admin criado: " + adminEmail + " / admin123");
        } else {
            // Admin existe: verifica se o papel está correto
            usuarioRepository.findByEmail(adminEmail).ifPresent(admin -> {
                if (!"admin".equals(admin.getPapel())) {
                    admin.setPapel("admin"); // Corrige o papel se necessário
                    usuarioRepository.save(admin);
                }
            });
        }
    }

    // Cria as categorias iniciais apenas se o banco estiver completamente vazio.
    // count() retorna o total de registros na tabela de categorias.
    private void seedCategorias() {
        if (categoriaRepository.count() == 0) {
            // Array bidimensional: cada linha tem [nome, nomeUrl]
            // nomeUrl é o slug usado na URL (sem acentos ou espaços)
            String[][] cats = {
                {"Camisetas", "camisetas"},
                {"Calças",    "calcas"},
                {"Blusas",    "blusas"},
                {"Acessórios","acessorios"}
            };
            // Percorre cada categoria e salva no banco
            for (String[] c : cats) {
                Categoria cat = new Categoria();
                cat.setNome(c[0]);    // Ex: "Camisetas"
                cat.setNomeUrl(c[1]); // Ex: "camisetas" (usado na URL)
                cat.setAtivo(true);   // Categoria ativa desde o início
                categoriaRepository.save(cat);
            }
            System.out.println(">>> Categorias criadas: Camisetas, Calças, Blusas, Acessórios");
        }
    }

    // Cria as opções de frete iniciais apenas se a tabela estiver vazia.
    // O projeto oferece dois tipos de frete: Padrão e Expresso.
    private void seedFretes() {
        if (opcaoFreteRepository.count() == 0) {
            // Frete Padrão: mais barato, prazo maior
            OpcaoFrete padrao = new OpcaoFrete();
            padrao.setNome("Padrão");
            padrao.setPrazoMinimo(5); // Mínimo 5 dias úteis
            padrao.setPrazoMaximo(7); // Máximo 7 dias úteis
            padrao.setPreco(new BigDecimal("29.90")); // BigDecimal para precisão monetária
            opcaoFreteRepository.save(padrao);

            // Frete Expresso: mais caro, prazo menor
            OpcaoFrete expresso = new OpcaoFrete();
            expresso.setNome("Expresso");
            expresso.setPrazoMinimo(1); // Mínimo 1 dia útil
            expresso.setPrazoMaximo(3); // Máximo 3 dias úteis
            expresso.setPreco(new BigDecimal("54.90"));
            opcaoFreteRepository.save(expresso);

            System.out.println(">>> Fretes criados (Padrão R$29,90 e Expresso R$54,90)");
        }
    }
}
