package br.com.fiap.clyvovet.controller.web;

import br.com.fiap.clyvovet.dto.SolicitacaoAgendamentoDTO;
import br.com.fiap.clyvovet.entity.Agendamento;
import br.com.fiap.clyvovet.entity.Pet;
import br.com.fiap.clyvovet.entity.Usuario;
import br.com.fiap.clyvovet.exception.RegraDeNegocioException;
import br.com.fiap.clyvovet.repository.AgendamentoRepository;
import br.com.fiap.clyvovet.repository.PetRepository;
import br.com.fiap.clyvovet.repository.VeterinarioRepository;
import br.com.fiap.clyvovet.service.TriagemAgendamentoService;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
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
    private final ContextoAutenticacaoWeb contextoAutenticacao;

    public AgendamentoWebController(AgendamentoRepository agendamentoRepository,
                                    PetRepository petRepository,
                                    VeterinarioRepository veterinarioRepository,
                                    TriagemAgendamentoService triagemAgendamentoService,
                                    ContextoAutenticacaoWeb contextoAutenticacao) {
        this.agendamentoRepository = agendamentoRepository;
        this.petRepository = petRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.triagemAgendamentoService = triagemAgendamentoService;
        this.contextoAutenticacao = contextoAutenticacao;
    }

    @GetMapping
    public String listarAgendamentos(Authentication authentication, Model model) {
        Usuario usuario = contextoAutenticacao.obterUsuario(authentication);
        boolean isVet = contextoAutenticacao.possuiAcessoClinico(authentication);

        List<Agendamento> agendamentos = isVet
                ? agendamentoRepository.findAllByOrderByDataAgendamentoDesc()
                : agendamentoRepository.findByPetTutorIdOrderByDataAgendamentoDesc(
                        contextoAutenticacao.obterTutorIdOuPadrao(usuario));

        model.addAttribute("agendamentos", agendamentos);
        model.addAttribute("isVet", isVet);
        return "agenda/lista";
    }

    @GetMapping("/novo")
    public String formularioNovoAgendamento(Authentication authentication, Model model) {
        Usuario usuario = contextoAutenticacao.obterUsuario(authentication);
        boolean isVet = contextoAutenticacao.possuiAcessoClinico(authentication);

        if (!model.containsAttribute("dto")) {
            SolicitacaoAgendamentoDTO dto = SolicitacaoAgendamentoDTO.builder()
                    .dataHora(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0))
                    .tipo("Consulta")
                    .tipoTriagem("Rotina / Preventivo")
                    .build();
            model.addAttribute("dto", dto);
        }

        carregarListasFormulario(usuario, isVet, model);
        return "fluxos/novo-agendamento";
    }

    @PostMapping("/solicitar")
    public String solicitarAgendamento(
            Authentication authentication,
            @Valid @ModelAttribute("dto") SolicitacaoAgendamentoDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = contextoAutenticacao.obterUsuario(authentication);
        boolean isVet = contextoAutenticacao.possuiAcessoClinico(authentication);

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
    @CacheEvict(value = "agendamentos", allEntries = true)
    public String cancelarAgendamento(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        agendamentoRepository.findById(id).ifPresent(a -> {
            a.setStatus("CANCELADO");
            agendamentoRepository.save(a);
        });
        redirectAttributes.addFlashAttribute("sucesso", "Agendamento cancelado com sucesso.");
        return "redirect:/agendamentos";
    }

    private void carregarListasFormulario(Usuario usuario, boolean isVet, Model model) {
        List<Pet> pets = isVet
                ? petRepository.findAll()
                : petRepository.findByTutorId(contextoAutenticacao.obterTutorIdOuPadrao(usuario));
        model.addAttribute("pets", pets);
        model.addAttribute("veterinarios", veterinarioRepository.findAll());
    }
}
