package com.obs.observaAcao.controllers;

import com.obs.observaAcao.dtos.ContatoDTO;
import com.obs.observaAcao.models.ContatoModel;
import com.obs.observaAcao.services.ContatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contatos")
public class ContatoController {

    @Autowired
    private ContatoService contatoService;

    @GetMapping
    public ResponseEntity<ContatoModel> obter() {
        return ResponseEntity.ok(contatoService.obter());
    }

    @PutMapping
    public ResponseEntity<ContatoModel> atualizar(@RequestBody ContatoDTO dto) {
        return ResponseEntity.ok(contatoService.atualizar(dto));
    }
}
