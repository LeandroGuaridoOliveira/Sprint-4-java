package br.com.fiap.clyvovet.controller.web;

import br.com.fiap.clyvovet.entity.Usuario;
import br.com.fiap.clyvovet.service.UsuarioDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class ContextoAutenticacaoWeb {

    private final UsuarioDetailsService usuarioDetailsService;

    public ContextoAutenticacaoWeb(UsuarioDetailsService usuarioDetailsService) {
        this.usuarioDetailsService = usuarioDetailsService;
    }

    public Usuario obterUsuario(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        return usuarioDetailsService.buscarPorEmail(authentication.getName());
    }

    public boolean possuiAcessoClinico(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_VET".equals(a.getAuthority()) || "ROLE_ADMIN".equals(a.getAuthority()));
    }

    public Long obterTutorIdOuPadrao(Usuario usuario) {
        return (usuario != null && usuario.getTutorId() != null) ? usuario.getTutorId() : 1L;
    }
}
