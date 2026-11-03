package br.com.fiap.clyvovet.controller.web;

import br.com.fiap.clyvovet.dto.SolicitacaoAgendamentoDTO;
import br.com.fiap.clyvovet.entity.Agendamento;
import br.com.fiap.clyvovet.entity.Pet;
import br.com.fiap.clyvovet.entity.Usuario;
import br.com.fiap.clyvovet.entity.Veterinario;
import br.com.fiap.clyvovet.exception.RegraDeNegocioException;
import br.com.fiap.clyvovet.repository.AgendamentoRepository;
import br.com.fiap.clyvovet.repository.PetRepository;
import br.com.fiap.clyvovet.repository.VeterinarioRepository;
import br.com.fiap.clyvovet.service.TriagemAgendamentoService;
import br.com.fiap.clyvovet.service.UsuarioDetailsService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/agendamentos")
public class AgendamentoWebController {

    private final AgendamentoRepository agendamentoRepository;
    private final PetRepository petRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final TriagemAgendamentoService triagemAgendamentoService;
    private final UsuarioDetailsService usuarioDetailsService;

    public AgendamentoWebController(AgendamentoRepository agendamentoRepository,
                                    PetRepository petRepository,
                                    VeterinarioRepository veterinarioRepository,
                                    TriagemAgendamentoService triagemAgendamentoService,
                                    UsuarioDetailsService usuarioDetailsService) {
        this.agendamentoRepository = agendamentoRepository;
        this.petRepository = petRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.triagemAgendamentoService = triagemAgendamentoService;
        this.usuarioDetailsService = usuarioDetailsService;
    }

    /**
     * Lista de agendamentos com visualização adaptada ao perfil do usuário
     */
    @GetMapping
    public String listarAgendamentos(Authentication authentication, Model model) {
        Usuario usuario = usuarioDetailsService.buscarPorEmail(authentication.getName());
        boolean isVet = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_VET") || a.getAuthority().equals("ROLE_ADMIN"));

        List<Agendamento> agendamentos;
        if (isVet) {
            agendamentos = agendamentoRepository.findAllByOrderByDataAgendamentoDesc();
        } else {
            Long tutorId = (usuario != null && usuario.getTutorId() != null) ? usuario.getTutorId() : 1L;
            agendamentos = agendamentoRepository.findByPetTutorIdOrderByDataAgendamentoDesc(tutorId);
        }

        model.addAttribute("agendamentos", agendamentos);
        model.addAttribute("isVet", isVet);
        return "agenda/lista";
    }

    /**
     * Exibição do Formulário de Triagem e Agendamento Clínico
     */
    @GetMapping("/novo")
    public String formularioNovoAgendamento(Authentication authentication, Model model) {
        Usuario usuario = usuarioDetailsService.buscarPorEmail(authentication.getName());
        boolean isVet = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_VET") || a.getAuthority().equals("ROLE_ADMIN"));

        List<Pet> pets;
        if (isVet) {
            pets = petRepository.findAll();
        } else {
            Long tutorId = (usuario != null && usuario.getTutorId() != null) ? usuario.getTutorId() : 1L;
            pets = petRepository.findByTutorId(tutorId);
        }

        List<Veterinario> veterinarios = veterinarioRepository.findAll();

        if (!model.containsAttribute("dto")) {
            SolicitacaoAgendamentoDTO dto = SolicitacaoAgendamentoDTO.builder()
                    .dataHora(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0))
                    .tipo("Consulta")
                    .tipoTriagem("Rotina / Preventivo")
                    .build();
            model.addAttribute("dto", dto);
        }

        model.addAttribute("pets", pets);
        model.addAttribute("veterinarios", veterinarios);
        return "fluxos/novo-agendamento";
    }

    /**
     * Processamento das regras de negócio de agendamento, validação e verificação de conflitos de agenda
     */
    @PostMapping("/solicitar")
    public String solicitarAgendamento(
            Authentication authentication,
            @Valid @ModelAttribute("dto") SolicitacaoAgendamentoDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioDetailsService.buscarPorEmail(authentication.getName());
        boolean isVet = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_VET") || a.getAuthority().equals("ROLE_ADMIN"));

        if (bindingResult.hasErrors()) {
            carregarListasFormulario(usuario, isVet, model);
            return "fluxos/novo-agendamento";
        }

        try {
            triagemAgendamentoService.processarTriagemEAgendamento(dto);
            redirectAttributes.addFlashAttribute("sucesso", 
                    "Triagem concluída e agendamento confirmado com sucesso! Horário reservado no sistema clínico.");
            return "redirect:/agendamentos";
        } catch (RegraDeNegocioException ex) {
            model.addAttribute("erro", ex.getMessage());
            carregarListasFormulario(usuario, isVet, model);
            return "fluxos/novo-agendamento";
        } catch (Exception ex) {
            model.addAttribute("erro", "Erro ao processar solicitação: " + ex.getMessage());
            carregarListasFormulario(usuario, isVet, model);
            return "fluxos/novo-agendamento";
        }
    }

    @PostMapping("/{id}/cancelar")
    public String cancelarAgendamento(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        agendamentoRepository.findById(id).ifPresent(a -> {
            a.setStatus("CANCELADO");
            agendamentoRepository.save(a);
        });
        redirectAttributes.addFlashAttribute("sucesso", "Agendamento cancelado com sucesso.");
        return "redirect:/agendamentos";
    }

    private void carregarListasFormulario(Usuario usuario, boolean isVet, Model model) {
        List<Pet> pets;
        if (isVet) {
            pets = petRepository.findAll();
        } else {
            Long tutorId = (usuario != null && usuario.getTutorId() != null) ? usuario.getTutorId() : 1L;
            pets = petRepository.findByTutorId(tutorId);
        }
        model.addAttribute("pets", pets);
        model.addAttribute("veterinarios", veterinarioRepository.findAll());
    }
}
