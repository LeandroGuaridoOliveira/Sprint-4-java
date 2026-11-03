package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.EventoSaudeDTO;
import br.com.fiap.clyvovet.entity.EventoSaude;
import br.com.fiap.clyvovet.entity.Pet;
import br.com.fiap.clyvovet.entity.Veterinario;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.repository.EventoSaudeRepository;
import br.com.fiap.clyvovet.repository.PetRepository;
import br.com.fiap.clyvovet.repository.VeterinarioRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EventoSaudeService {

    private final EventoSaudeRepository repository;
    private final PetRepository petRepository;
    private final VeterinarioRepository veterinarioRepository;

    public EventoSaudeService(EventoSaudeRepository repository, PetRepository petRepository,
                              VeterinarioRepository veterinarioRepository) {
        this.repository = repository;
        this.petRepository = petRepository;
        this.veterinarioRepository = veterinarioRepository;
    }

    @Cacheable("eventos")
    public Page<EventoSaudeDTO> listar(String tipoEvento, Long petId, Long veterinarioId, Pageable pageable) {
        if (petId != null) return repository.findByPetId(petId, pageable).map(this::toDTO);
        if (veterinarioId != null) return repository.findByVeterinarioId(veterinarioId, pageable).map(this::toDTO);
        if (tipoEvento != null && !tipoEvento.isBlank())
            return repository.findByTipoEvento(tipoEvento, pageable).map(this::toDTO);
        return repository.findAll(pageable).map(this::toDTO);
    }

    @Cacheable(value = "eventos", key = "#id")
    public EventoSaudeDTO buscarPorId(Long id) {
        return toDTO(findOrFail(id));
    }

    @CacheEvict(value = "eventos", allEntries = true)
    public EventoSaudeDTO criar(EventoSaudeDTO dto) {
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + dto.getPetId()));
        Veterinario vet = veterinarioRepository.findById(dto.getVeterinarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinário não encontrado com id: " + dto.getVeterinarioId()));
        EventoSaude evento = toEntity(dto);
        evento.setPet(pet);
        evento.setVeterinario(vet);
        return toDTO(repository.save(evento));
    }

    @CacheEvict(value = "eventos", allEntries = true)
    public EventoSaudeDTO atualizar(Long id, EventoSaudeDTO dto) {
        EventoSaude evento = findOrFail(id);
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + dto.getPetId()));
        Veterinario vet = veterinarioRepository.findById(dto.getVeterinarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinário não encontrado com id: " + dto.getVeterinarioId()));
        evento.setTipoEvento(dto.getTipoEvento());
        evento.setDataEvento(dto.getDataEvento());
        evento.setDescricao(dto.getDescricao());
        evento.setResultado(dto.getResultado());
        evento.setPet(pet);
        evento.setVeterinario(vet);
        return toDTO(repository.save(evento));
    }

    @CacheEvict(value = "eventos", allEntries = true)
    public void deletar(Long id) {
        findOrFail(id);
        repository.deleteById(id);
    }

    private EventoSaude findOrFail(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento de saúde não encontrado com id: " + id));
    }

    private EventoSaudeDTO toDTO(EventoSaude e) {
        return EventoSaudeDTO.builder()
                .id(e.getId()).tipoEvento(e.getTipoEvento()).dataEvento(e.getDataEvento())
                .descricao(e.getDescricao()).resultado(e.getResultado())
                .petId(e.getPet().getId()).petNome(e.getPet().getNome())
                .veterinarioId(e.getVeterinario().getId()).veterinarioNome(e.getVeterinario().getNome())
                .build();
    }

    private EventoSaude toEntity(EventoSaudeDTO dto) {
        return EventoSaude.builder()
                .tipoEvento(dto.getTipoEvento()).dataEvento(dto.getDataEvento())
                .descricao(dto.getDescricao()).resultado(dto.getResultado())
                .build();
    }
}
