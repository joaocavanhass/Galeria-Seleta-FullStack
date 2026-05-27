// ============================================================
// ARQUIVO: ContatoService.java
// FUNÇÃO: Serviço com a lógica do formulário de contato e newsletter.
//
// LÓGICA DA NEWSLETTER:
// - Se o email já existe mas está inativo: reativa a inscrição
// - Se o email não existe: cria uma nova inscrição
// - Se o email já está ativo: não faz nada (idempotente)
// Isso usa o padrão ifPresentOrElse do Optional.
//
// CONEXÕES: chamado por ContatoController.
// ============================================================

package com.galeriaseleta.service;

import com.galeriaseleta.dto.request.ContatoRequest;
import com.galeriaseleta.dto.request.NewsletterRequest;
import com.galeriaseleta.model.Contato;
import com.galeriaseleta.model.Newsletter;
import com.galeriaseleta.repository.ContatoRepository;
import com.galeriaseleta.repository.NewsletterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContatoService {

    private final ContatoRepository contatoRepository;
    private final NewsletterRepository newsletterRepository;

    public ContatoService(ContatoRepository contatoRepository, NewsletterRepository newsletterRepository) {
        this.contatoRepository = contatoRepository;
        this.newsletterRepository = newsletterRepository;
    }

    // Salva uma mensagem de contato recebida pelo formulário do site
    public void enviarMensagem(ContatoRequest request) {
        Contato contato = new Contato();
        contato.setNome(request.getNome());
        contato.setSobrenome(request.getSobrenome());
        contato.setEmail(request.getEmail());
        contato.setTelefone(request.getTelefone());
        contato.setMensagem(request.getMensagem());
        contatoRepository.save(contato); // Salva no banco
    }

    // Retorna todas as mensagens de contato (para o admin visualizar)
    public List<Contato> listarMensagens() {
        return contatoRepository.findAll();
    }

    // Remove uma mensagem de contato pelo ID
    public void deletarMensagem(Long id) {
        contatoRepository.deleteById(id.intValue());
    }

    // Inscreve um email na newsletter.
    // ifPresentOrElse: executa o primeiro bloco se o email existir, o segundo se não existir.
    public void inscreverNewsletter(NewsletterRequest request) {
        newsletterRepository.findByEmail(request.getEmail()).ifPresentOrElse(
                inscricao -> {
                    // Email já existe: reativa a inscrição (caso tenha cancelado antes)
                    inscricao.setAtivo(true);
                    newsletterRepository.save(inscricao);
                },
                () -> {
                    // Email novo: cria uma nova inscrição
                    Newsletter inscricao = new Newsletter();
                    inscricao.setEmail(request.getEmail());
                    newsletterRepository.save(inscricao);
                }
        );
    }

    // Cancela a inscrição na newsletter desativando o registro (não deleta)
    // ifPresent: só executa se o email existir na base
    public void cancelarNewsletter(String email) {
        newsletterRepository.findByEmail(email).ifPresent(inscricao -> {
            inscricao.setAtivo(false); // Desativa em vez de deletar (mantém histórico)
            newsletterRepository.save(inscricao);
        });
    }
}
