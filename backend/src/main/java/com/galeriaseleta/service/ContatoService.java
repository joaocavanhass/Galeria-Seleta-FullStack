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

    public void enviarMensagem(ContatoRequest request) {
        Contato contato = new Contato();
        contato.setNome(request.getNome());
        contato.setSobrenome(request.getSobrenome());
        contato.setEmail(request.getEmail());
        contato.setTelefone(request.getTelefone());
        contato.setMensagem(request.getMensagem());
        contatoRepository.save(contato);
    }

    public List<Contato> listarMensagens() {
        return contatoRepository.findAll();
    }

    public void deletarMensagem(Long id) {
        contatoRepository.deleteById(id.intValue());
    }

    public void inscreverNewsletter(NewsletterRequest request) {
        newsletterRepository.findByEmail(request.getEmail()).ifPresentOrElse(
                inscricao -> {
                    inscricao.setAtivo(true);
                    newsletterRepository.save(inscricao);
                },
                () -> {
                    Newsletter inscricao = new Newsletter();
                    inscricao.setEmail(request.getEmail());
                    newsletterRepository.save(inscricao);
                }
        );
    }

    public void cancelarNewsletter(String email) {
        newsletterRepository.findByEmail(email).ifPresent(inscricao -> {
            inscricao.setAtivo(false);
            newsletterRepository.save(inscricao);
        });
    }
}
