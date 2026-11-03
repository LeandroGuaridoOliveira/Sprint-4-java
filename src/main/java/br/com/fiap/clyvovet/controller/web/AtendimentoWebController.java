package br.com.fiap.clyvovet.controller.web;

import br.com.fiap.clyvovet.dto.AtendimentoConsultaDTO;
import br.com.fiap.clyvovet.entity.Agendamento;
import br.com.fiap.clyvovet.entity.EventoSaude;
import br.com.fiap.clyvovet.entity.Pet;
import br.com.fiap.clyvovet.exception.RegraDeNegocioException;
import br.com.fiap.clyvovet.repository.AgendamentoRepository;
import br.com.fiap.clyvovet.repository.EventoSaudeRepository;
import br.com.fiap.clyvovet.repository.PetRepository;
import br.com.fiap.clyvovet.service.ExecucaoConsultaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/atendimentos")
public class AtendimentoWebController {

    private final AgendamentoRepository agendamentoRepository;
    private final PetRepository petRepository;
    private final EventoSaudeRepository eventoSaudeRepository;
    private final ExecucaoConsultaService execucaoConsultaService;

    public AtendimentoWebController(AgendamentoRepository agendamentoRepository,
                                    PetRepository petRepository,
                                    EventoSaudeRepository eventoSaudeRepository,
                                    ExecucaoConsultaService execucaoConsultaService) {
        this.agendamentoRepository = agendamentoRepository;
        this.petRepository = petRepository;
        this.eventoSaudeRepository = eventoSaudeRepository;
        this.execucaoConsultaService = execucaoConsultaService;
    }

    /**
     * Fila de atendimentos clínicos pendentes para o médico veterinário
     */
    @GetMapping
    public String filaAtendimentos(Model model) {
        List<Agendamento> agendamentos = agendamentoRepository.findAllByOrderByDataAgendamentoDesc().stream()
                .filter(a -> !"CONCLUIDO".equalsIgnoreCase(a.getStatus()) && !"CANCELADO".equalsIgnoreCase(a.getStatus()))
                .toList();

        model.addAttribute("agendamentos", agendamentos);
        return "atendimento/fila";
    }

    /**
     * Inicia o atendimento clínico de um agendamento específico
     */
    @GetMapping("/{id}/iniciar")
    public String iniciarAtendimento(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Agendamento agendamento = agendamentoRepository.findById(id).orElse(null);
        if (agendamento == null) {
            redirectAttributes.addFlashAttribute("erro", "Agendamento não encontrado.");
            return "redirect:/agendamentos";
        }

        if ("CONCLUIDO".equalsIgnoreCase(agendamento.getStatus())) {
            redirectAttributes.addFlashAttribute("erro", "Este agendamento já foi finalizado.");
            return "redirect:/agendamentos";
        }

        Pet pet = agendamento.getPet();
        List<EventoSaude> historico = eventoSaudeRepository.findByPetIdOrderByDataEventoDesc(pet.getId());

        if (!model.containsAttribute("dto")) {
            AtendimentoConsultaDTO dto = AtendimentoConsultaDTO.builder()
                    .agendamentoId(agendamento.getId())
                    .petId(pet.getId())
                    .veterinarioId(agendamento.getVeterinario().getId())
                    .pesoAtual(pet.getPeso())
                    .tipoProcedimento(agendamento.getTipo())
                    .aplicarProtocoloPreventivo(false)
                    .build();
            model.addAttribute("dto", dto);
        }

        model.addAttribute("agendamento", agendamento);
        model.addAttribute("pet", pet);
        model.addAttribute("historico", historico);
        return "fluxos/realizar-consulta";
    }

    /**
     * Conclusão atômica da consulta, registro de histórico e atualização de prontuário
     */
    @PostMapping("/finalizar")
    public String finalizarAtendimento(
            @Valid @ModelAttribute("dto") AtendimentoConsultaDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            carregarContextoAtendimento(dto.getAgendamentoId(), model);
            return "fluxos/realizar-consulta";
        }

        try {
            execucaoConsultaService.executarAtendimentoClinico(dto);
            redirectAttributes.addFlashAttribute("sucesso", 
                    "Consulta clínica concluída com sucesso! Prontuário médico e peso do animal foram atualizados.");
            return "redirect:/pets/" + dto.getPetId() + "/prontuario";
        } catch (RegraDeNegocioException ex) {
            model.addAttribute("erro", ex.getMessage());
            carregarContextoAtendimento(dto.getAgendamentoId(), model);
            return "fluxos/realizar-consulta";
        } catch (Exception ex) {
            model.addAttribute("erro", "Erro ao concluir atendimento: " + ex.getMessage());
            carregarContextoAtendimento(dto.getAgendamentoId(), model);
            return "fluxos/realizar-consulta";
        }
    }

    private void carregarContextoAtendimento(Long agendamentoId, Model model) {
        agendamentoRepository.findById(agendamentoId).ifPresent(agendamento -> {
            model.addAttribute("agendamento", agendamento);
            model.addAttribute("pet", agendamento.getPet());
            model.addAttribute("historico", eventoSaudeRepository.findByPetIdOrderByDataEventoDesc(agendamento.getPet().getId()));
        });
    }
}
