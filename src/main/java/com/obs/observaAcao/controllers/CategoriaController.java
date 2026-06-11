package com.obs.observaAcao.controllers;

import com.obs.observaAcao.dtos.CategoriaDTO;
import com.obs.observaAcao.enums.CategoriaEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @GetMapping
    public List<CategoriaDTO> listarCategorias() {

        return Arrays.stream(CategoriaEnum.values())
                .map(c -> new CategoriaDTO(
                        c.name(),
                        c.isPermiteAnonimo()
                ))
                .toList();
    }
}