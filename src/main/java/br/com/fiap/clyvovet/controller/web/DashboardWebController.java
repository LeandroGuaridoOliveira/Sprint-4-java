package br.com.fiap.clyvovet.controller.web;

import br.com.fiap.clyvovet.entity.Agendamento;
import br.com.fiap.clyvovet.entity.Pet;
import br.com.fiap.clyvovet.entity.Usuario;
import br.com.fiap.clyvovet.repository.*;
import br.com.fiap.clyvovet.service.UsuarioDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardWebController {

    private final UsuarioDetailsService usuarioDetailsService;
    private final PetRepository petRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final EventoSaudeRepository eventoSaudeRepository;
    private final VeterinarioRepository veterinarioRepository;

    public DashboardWebController(UsuarioDetailsService usuarioDetailsService,
                                  PetRepository petRepository,
                                  AgendamentoRepository agendamentoRepository,
                                  EventoSaudeRepository eventoSaudeRepository,
                                  VeterinarioRepository veterinarioRepository) {
        this.usuarioDetailsService = usuarioDetailsService;
        this.petRepository = petRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.eventoSaudeRepository = eventoSaudeRepository;
        this.veterinarioRepository = veterinarioRepository;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioDetailsService.buscarPorEmail(authentication.getName());
        model.addAttribute("usuario", usuario);

        boolean isVet = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_VET") || a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isVet", isVet);

        // Métricas gerais
        long totalPets = petRepository.count();
        long totalAgendamentos = agendamentoRepository.count();
        long totalConsultasRealizadas = eventoSaudeRepository.count();

        model.addAttribute("totalPets", totalPets);
        model.addAttribute("totalAgendamentos", totalAgendamentos);
        model.addAttribute("totalConsultasRealizadas", totalConsultasRealizadas);

        if (isVet) {
            // Visão Veterinária: Próximos atendimentos e fila de consultas
            List<Agendamento> agendamentosRecentes = agendamentoRepository.findAllByOrderByDataAgendamentoDesc();
            model.addAttribute("agendamentos", agendamentosRecentes.stream().limit(6).toList());
            model.addAttribute("atendimentosPendentes", agendamentosRecentes.stream()
                    .filter(a -> !"CONCLUIDO".equalsIgnoreCase(a.getStatus()) && !"CANCELADO".equalsIgnoreCase(a.getStatus()))
                    .limit(5)
                    .toList());
        } else {
            // Visão Tutor: Seus pets e seus agendamentos
            Long tutorId = (usuario != null && usuario.getTutorId() != null) ? usuario.getTutorId() : 1L;
            List<Pet> meusPets = petRepository.findByTutorId(tutorId);
            List<Agendamento> meusAgendamentos = agendamentoRepository.findByPetTutorIdOrderByDataAgendamentoDesc(tutorId);

            model.addAttribute("meusPets", meusPets);
            model.addAttribute("agendamentos", meusAgendamentos.stream().limit(5).toList());
        }

        return "dashboard";
    }
}
