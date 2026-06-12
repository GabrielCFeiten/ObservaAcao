package com.obs.observaAcao.repositories;

import com.obs.observaAcao.enums.TipoUsuarioEnum;
import com.obs.observaAcao.models.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Integer> {
    UserDetails findByLogin(String login);
    boolean existsByLogin(String login);
    boolean existsByCpf(String cpf);
    List<UsuarioModel> findByTipo(TipoUsuarioEnum tipo);
}
