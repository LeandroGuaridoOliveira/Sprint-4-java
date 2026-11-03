package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.entity.Usuario;
import br.com.fiap.clyvovet.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com e-mail: " + email));

        if (Boolean.FALSE.equals(usuario.getAtivo())) {
            throw new UsernameNotFoundException("Usuário desativado no sistema.");
        }

        String role = usuario.getRole().startsWith("ROLE_") ? usuario.getRole() : "ROLE_" + usuario.getRole();

        return new User(
                usuario.getEmail(),
                usuario.getSenha(),
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email).orElse(null);
    }
}
