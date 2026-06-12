package com.obs.observaAcao.services;

import com.obs.observaAcao.dtos.AlterarTipoDTO;
import com.obs.observaAcao.dtos.AtualizarPerfilDTO;
import com.obs.observaAcao.dtos.CadastroUsuarioDTO;
import com.obs.observaAcao.enums.TipoUsuarioEnum;
import com.obs.observaAcao.models.UsuarioModel;
import com.obs.observaAcao.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = usuarioRepository.findByLogin(username);
        if (user == null) throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        return user;
    }

    public UsuarioModel cadastrar(CadastroUsuarioDTO dto) {
        if (usuarioRepository.existsByLogin(dto.login())) {
            throw new RuntimeException("Login já está em uso.");
        }
        if (usuarioRepository.existsByCpf(dto.cpf())) {
            throw new RuntimeException("CPF já cadastrado.");
        }
        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome(dto.nome());
        usuario.setCpf(dto.cpf());
        usuario.setTelefone(dto.telefone());
        usuario.setLogin(dto.login());
        usuario.setPassword(passwordEncoder.encode(dto.password()));
        usuario.setTipo(TipoUsuarioEnum.CIDADAO);
        return usuarioRepository.save(usuario);
    }

    public UsuarioModel findById(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + id));
    }

    
    public UsuarioModel alterarTipo(Integer usuarioId, AlterarTipoDTO dto) {
        if (dto.novoTipo() == TipoUsuarioEnum.ANONIMO) {
            throw new RuntimeException("Não é possível definir manualmente o tipo ANONIMO.");
        }
        UsuarioModel usuario = findById(usuarioId);
        usuario.setTipo(dto.novoTipo());
        return usuarioRepository.save(usuario);
    }

    
    public UsuarioModel atualizarPerfil(UsuarioModel logado, AtualizarPerfilDTO dto) {
        if (dto.nome() != null && !dto.nome().isBlank()) {
            logado.setNome(dto.nome().trim());
        }
        if (dto.telefone() != null && !dto.telefone().isBlank()) {
            logado.setTelefone(dto.telefone().trim());
        }
        if (dto.login() != null && !dto.login().isBlank()
                && !dto.login().equalsIgnoreCase(logado.getLogin())) {
            if (usuarioRepository.existsByLogin(dto.login())) {
                throw new IllegalArgumentException("Este e-mail já está em uso.");
            }
            logado.setLogin(dto.login().trim());
        }
        if (dto.password() != null && !dto.password().isBlank()) {
            if (dto.password().length() < 6) {
                throw new IllegalArgumentException("A senha deve ter no mínimo 6 caracteres.");
            }
            logado.setPassword(passwordEncoder.encode(dto.password()));
        }
        return usuarioRepository.save(logado);
    }

    
    public List<UsuarioModel> listarGestores() {
        return usuarioRepository.findByTipo(TipoUsuarioEnum.GESTOR);
    }
}
