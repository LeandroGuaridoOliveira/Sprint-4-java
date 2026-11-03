package br.com.fiap.clyvovet.controller.web;

import br.com.fiap.clyvovet.entity.Agendamento;
import br.com.fiap.clyvovet.entity.Pet;
import br.com.fiap.clyvovet.entity.Usuario;
import br.com.fiap.clyvovet.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardWebController {

    private final ContextoAutenticacaoWeb contextoAutenticacao;
    private final PetRepository petRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final EventoSaudeRepository eventoSaudeRepository;

    public DashboardWebController(ContextoAutenticacaoWeb contextoAutenticacao,
                                  PetRepository petRepository,
                                  AgendamentoRepository agendamentoRepository,
                                  EventoSaudeRepository eventoSaudeRepository) {
        this.contextoAutenticacao = contextoAutenticacao;
        this.petRepository = petRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.eventoSaudeRepository = eventoSaudeRepository;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Usuario usuario = contextoAutenticacao.obterUsuario(authentication);
        boolean isVet = contextoAutenticacao.possuiAcessoClinico(authentication);

        model.addAttribute("usuario", usuario);
        model.addAttribute("isVet", isVet);
        model.addAttribute("totalPets", petRepository.count());
        model.addAttribute("totalAgendamentos", agendamentoRepository.count());
        model.addAttribute("totalConsultasRealizadas", eventoSaudeRepository.count());

        if (isVet) {
            List<Agendamento> agendamentosRecentes = agendamentoRepository.findAllByOrderByDataAgendamentoDesc();
            model.addAttribute("agendamentos", agendamentosRecentes.stream().limit(6).toList());
            model.addAttribute("atendimentosPendentes", agendamentosRecentes.stream()
                    .filter(a -> !"CONCLUIDO".equalsIgnoreCase(a.getStatus()) && !"CANCELADO".equalsIgnoreCase(a.getStatus()))
                    .limit(5)
                    .toList());
        } else {
            Long tutorId = contextoAutenticacao.obterTutorIdOuPadrao(usuario);
            List<Pet> meusPets = petRepository.findByTutorId(tutorId);
            List<Agendamento> meusAgendamentos = agendamentoRepository.findByPetTutorIdOrderByDataAgendamentoDesc(tutorId);

            model.addAttribute("meusPets", meusPets);
            model.addAttribute("agendamentos", meusAgendamentos.stream().limit(5).toList());
        }

        return "dashboard";
    }
}
