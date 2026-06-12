package com.obs.observaAcao.repositories;

import com.obs.observaAcao.models.ContatoModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContatoRepository extends JpaRepository<ContatoModel, Integer> {
}
