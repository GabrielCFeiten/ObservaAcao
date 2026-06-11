package com.obs.observaAcao.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.obs.observaAcao.enums.TipoUsuarioEnum;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "tb_usuarios")
@Data
public class UsuarioModel implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    private String cpf;
    private String telefone;

    @Column(unique = true)
    private String login;

    // Nunca serializar o hash da senha no JSON de resposta.
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    private TipoUsuarioEnum tipo;

    // Campos internos do UserDetails que não devem aparecer no JSON.
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.tipo == TipoUsuarioEnum.GESTOR)
            return List.of(
                new SimpleGrantedAuthority("ROLE_GESTOR"),
                new SimpleGrantedAuthority("ROLE_CIDADAO")
            );
        else if (this.tipo == TipoUsuarioEnum.CIDADAO)
            return List.of(new SimpleGrantedAuthority("ROLE_CIDADAO"));
        else
            return List.of(new SimpleGrantedAuthority("ROLE_ANONIMO"));
    }

    @Override
    @JsonIgnore
    public String getPassword() { return password; }

    @Override
    @JsonIgnore
    public String getUsername() { return login; }
}
