package com.galeriaseleta.config;

import com.galeriaseleta.model.Categoria;
import com.galeriaseleta.model.OpcaoFrete;
import com.galeriaseleta.model.Usuario;
import com.galeriaseleta.repository.CategoriaRepository;
import com.galeriaseleta.repository.OpcaoFreteRepository;
import com.galeriaseleta.repository.UsuarioRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AdminInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final OpcaoFreteRepository opcaoFreteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbc;

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

    @Override
    public void run(ApplicationArguments args) {
        migrarColunas();
        garantirAdmin();
        seedCategorias();
        seedFretes();
    }

    private void migrarColunas() {
        // Migrações seguras para bancos de dados existentes
        tryAlter("ALTER TABLE usuarios     ADD COLUMN papel      TEXT NOT NULL DEFAULT 'cliente'");
        tryAlter("ALTER TABLE usuarios     ADD COLUMN cpf        TEXT");
        tryAlter("ALTER TABLE carrinho     ADD COLUMN quantidade INTEGER NOT NULL DEFAULT 1");
        tryAlter("ALTER TABLE itens_pedido ADD COLUMN quantidade INTEGER NOT NULL DEFAULT 1");
    }

    private void tryAlter(String sql) {
        try { jdbc.execute(sql); } catch (Exception ignored) {}
    }

    private void garantirAdmin() {
        String adminEmail = "admin@galeriaseleta.com";
        if (usuarioRepository.findByEmail(adminEmail).isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail(adminEmail);
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setTelefone("(00) 00000-0000");
            admin.setPapel("admin");
            usuarioRepository.save(admin);
            System.out.println(">>> Admin criado: " + adminEmail + " / admin123");
        } else {
            usuarioRepository.findByEmail(adminEmail).ifPresent(admin -> {
                if (!"admin".equals(admin.getPapel())) {
                    admin.setPapel("admin");
                    usuarioRepository.save(admin);
                }
            });
        }
    }

    private void seedCategorias() {
        if (categoriaRepository.count() == 0) {
            String[][] cats = {
                {"Camisetas", "camisetas"},
                {"Calças",    "calcas"},
                {"Blusas",    "blusas"},
                {"Acessórios","acessorios"}
            };
            for (String[] c : cats) {
                Categoria cat = new Categoria();
                cat.setNome(c[0]);
                cat.setNomeUrl(c[1]);
                cat.setAtivo(true);
                categoriaRepository.save(cat);
            }
            System.out.println(">>> Categorias criadas: Camisetas, Calças, Blusas, Acessórios");
        }
    }

    private void seedFretes() {
        if (opcaoFreteRepository.count() == 0) {
            OpcaoFrete padrao = new OpcaoFrete();
            padrao.setNome("Padrão");
            padrao.setPrazoMinimo(5);
            padrao.setPrazoMaximo(7);
            padrao.setPreco(new BigDecimal("29.90"));
            opcaoFreteRepository.save(padrao);

            OpcaoFrete expresso = new OpcaoFrete();
            expresso.setNome("Expresso");
            expresso.setPrazoMinimo(1);
            expresso.setPrazoMaximo(3);
            expresso.setPreco(new BigDecimal("54.90"));
            opcaoFreteRepository.save(expresso);

            System.out.println(">>> Fretes criados (Padrão R$29,90 e Expresso R$54,90)");
        }
    }
}
