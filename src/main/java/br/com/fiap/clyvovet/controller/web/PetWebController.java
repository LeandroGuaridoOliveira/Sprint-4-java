package br.com.fiap.clyvovet.controller.web;

import br.com.fiap.clyvovet.entity.*;
import br.com.fiap.clyvovet.repository.*;
import br.com.fiap.clyvovet.service.UsuarioDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/pets")
public class PetWebController {

    private final PetRepository petRepository;
    private final EventoSaudeRepository eventoSaudeRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final ProtocoloRepository protocoloRepository;
    private final UsuarioDetailsService usuarioDetailsService;

    public PetWebController(PetRepository petRepository,
                             EventoSaudeRepository eventoSaudeRepository,
                             AgendamentoRepository agendamentoRepository,
                             ProtocoloRepository protocoloRepository,
                             UsuarioDetailsService usuarioDetailsService) {
        this.petRepository = petRepository;
        this.eventoSaudeRepository = eventoSaudeRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.protocoloRepository = protocoloRepository;
        this.usuarioDetailsService = usuarioDetailsService;
    }

    @GetMapping
    public String listarPets(Authentication authentication, Model model) {
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

        model.addAttribute("pets", pets);
        model.addAttribute("isVet", isVet);
        return "pets/lista";
    }

    @GetMapping("/{id}/prontuario")
    public String prontuarioPet(@PathVariable("id") Long id, Authentication authentication, Model model) {
        Pet pet = petRepository.findById(id).orElse(null);
        if (pet == null) {
            return "redirect:/pets";
        }

        boolean isVet = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_VET") || a.getAuthority().equals("ROLE_ADMIN"));

        List<EventoSaude> historico = eventoSaudeRepository.findByPetIdOrderByDataEventoDesc(pet.getId());
        List<Agendamento> agendamentos = agendamentoRepository.findByPetIdOrderByDataAgendamentoDesc(pet.getId());
        List<Protocolo> protocolos = protocoloRepository.findAll().stream()
                .filter(p -> p.getEspecie().equalsIgnoreCase(pet.getEspecie()))
                .toList();

        model.addAttribute("pet", pet);
        model.addAttribute("historico", historico);
        model.addAttribute("agendamentos", agendamentos);
        model.addAttribute("protocolos", protocolos);
        model.addAttribute("isVet", isVet);
        return "pets/prontuario";
    }
}
