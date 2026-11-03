package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.AgendamentoDTO;
import br.com.fiap.clyvovet.entity.Agendamento;
import br.com.fiap.clyvovet.entity.Pet;
import br.com.fiap.clyvovet.entity.Veterinario;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.repository.AgendamentoRepository;
import br.com.fiap.clyvovet.repository.PetRepository;
import br.com.fiap.clyvovet.repository.VeterinarioRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AgendamentoService {

    private final AgendamentoRepository repository;
    private final PetRepository petRepository;
    private final VeterinarioRepository veterinarioRepository;

    public AgendamentoService(AgendamentoRepository repository, PetRepository petRepository,
                              VeterinarioRepository veterinarioRepository) {
        this.repository = repository;
        this.petRepository = petRepository;
        this.veterinarioRepository = veterinarioRepository;
    }

    @Cacheable("agendamentos")
    public Page<AgendamentoDTO> listar(String status, String tipo, Long petId, Long veterinarioId, Pageable pageable) {
        if (petId != null) return repository.findByPetId(petId, pageable).map(this::toDTO);
        if (veterinarioId != null) return repository.findByVeterinarioId(veterinarioId, pageable).map(this::toDTO);
        if (status != null && !status.isBlank()) return repository.findByStatus(status, pageable).map(this::toDTO);
        if (tipo != null && !tipo.isBlank()) return repository.findByTipo(tipo, pageable).map(this::toDTO);
        return repository.findAll(pageable).map(this::toDTO);
    }

    @Cacheable(value = "agendamentos", key = "#id")
    public AgendamentoDTO buscarPorId(Long id) {
        return toDTO(findOrFail(id));
    }

    @CacheEvict(value = "agendamentos", allEntries = true)
    public AgendamentoDTO criar(AgendamentoDTO dto) {
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + dto.getPetId()));
        Veterinario vet = veterinarioRepository.findById(dto.getVeterinarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinário não encontrado com id: " + dto.getVeterinarioId()));

        if (dto.getDataAgendamento().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data do agendamento não pode ser no passado");
        }

        Agendamento ag = toEntity(dto);
        ag.setPet(pet);
        ag.setVeterinario(vet);
        return toDTO(repository.save(ag));
    }

    @CacheEvict(value = "agendamentos", allEntries = true)
    public AgendamentoDTO atualizar(Long id, AgendamentoDTO dto) {
        Agendamento ag = findOrFail(id);
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + dto.getPetId()));
        Veterinario vet = veterinarioRepository.findById(dto.getVeterinarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinário não encontrado com id: " + dto.getVeterinarioId()));
        ag.setDataAgendamento(dto.getDataAgendamento());
        ag.setTipo(dto.getTipo());
        ag.setStatus(dto.getStatus());
        ag.setObservacao(dto.getObservacao());
        ag.setPet(pet);
        ag.setVeterinario(vet);
        return toDTO(repository.save(ag));
    }

    @CacheEvict(value = "agendamentos", allEntries = true)
    public void deletar(Long id) {
        findOrFail(id);
        repository.deleteById(id);
    }

    private Agendamento findOrFail(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado com id: " + id));
    }

    private AgendamentoDTO toDTO(Agendamento a) {
        return AgendamentoDTO.builder()
                .id(a.getId()).dataAgendamento(a.getDataAgendamento())
                .tipo(a.getTipo()).status(a.getStatus()).observacao(a.getObservacao())
                .petId(a.getPet().getId()).petNome(a.getPet().getNome())
                .veterinarioId(a.getVeterinario().getId()).veterinarioNome(a.getVeterinario().getNome())
                .build();
    }

    private Agendamento toEntity(AgendamentoDTO dto) {
        return Agendamento.builder()
                .dataAgendamento(dto.getDataAgendamento())
                .tipo(dto.getTipo()).status(dto.getStatus()).observacao(dto.getObservacao())
                .build();
    }
}
