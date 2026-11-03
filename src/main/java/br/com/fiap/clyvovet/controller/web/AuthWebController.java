package br.com.fiap.clyvovet.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthWebController {

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("erro", "E-mail ou senha inválidos. Por favor, verifique suas credenciais.");
        }
        if (logout != null) {
            model.addAttribute("sucesso", "Sessão encerrada com sucesso. Até logo!");
        }

        return "auth/login";
    }

    @GetMapping("/403")
    public String accessDeniedPage(Model model) {
        return "auth/acesso-negado";
    }
}
