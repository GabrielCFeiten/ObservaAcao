package com.obs.observaAcao.services;

import com.obs.observaAcao.dtos.ContatoDTO;
import com.obs.observaAcao.models.ContatoModel;
import com.obs.observaAcao.repositories.ContatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContatoService {

    @Autowired
    private ContatoRepository contatoRepository;

    public ContatoModel obter() {
        return contatoRepository.findById(1).orElseGet(() -> {
            ContatoModel c = new ContatoModel();
            c.setId(1);
            c.setEmail("vigilancia@sarandi.pr.gov.br");
            c.setTelefone("(44) 3264-0000");
            c.setEndereco("Rua José Emiliano de Gusmão, 565 — Centro, Sarandi/PR");
            return contatoRepository.save(c);
        });
    }

    public ContatoModel atualizar(ContatoDTO dto) {
        ContatoModel c = obter();
        if (dto.email() != null && !dto.email().isBlank()) c.setEmail(dto.email().trim());
        if (dto.telefone() != null && !dto.telefone().isBlank()) c.setTelefone(dto.telefone().trim());
        if (dto.endereco() != null && !dto.endereco().isBlank()) c.setEndereco(dto.endereco().trim());
        return contatoRepository.save(c);
    }
}
