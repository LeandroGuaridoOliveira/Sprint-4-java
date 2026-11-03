package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.TutorDTO;
import br.com.fiap.clyvovet.entity.Tutor;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.repository.TutorRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TutorService {

    private final TutorRepository repository;

    public TutorService(TutorRepository repository) {
        this.repository = repository;
    }

    @Cacheable("tutores")
    public Page<TutorDTO> listar(String nome, Pageable pageable) {
        Page<Tutor> page = (nome != null && !nome.isBlank())
                ? repository.findByNomeContainingIgnoreCase(nome, pageable)
                : repository.findAll(pageable);
        return page.map(this::toDTO);
    }

    @Cacheable(value = "tutores", key = "#id")
    public TutorDTO buscarPorId(Long id) {
        return toDTO(findOrFail(id));
    }

    @CacheEvict(value = "tutores", allEntries = true)
    public TutorDTO criar(TutorDTO dto) {
        return toDTO(repository.save(toEntity(dto)));
    }

    @CacheEvict(value = "tutores", allEntries = true)
    public TutorDTO atualizar(Long id, TutorDTO dto) {
        Tutor tutor = findOrFail(id);
        tutor.setNome(dto.getNome());
        tutor.setCpf(dto.getCpf());
        tutor.setEmail(dto.getEmail());
        tutor.setTelefone(dto.getTelefone());
        tutor.setEndereco(dto.getEndereco());
        return toDTO(repository.save(tutor));
    }

    @CacheEvict(value = "tutores", allEntries = true)
    public void deletar(Long id) {
        findOrFail(id);
        repository.deleteById(id);
    }

    private Tutor findOrFail(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor não encontrado com id: " + id));
    }

    private TutorDTO toDTO(Tutor t) {
        return TutorDTO.builder()
                .id(t.getId()).nome(t.getNome()).cpf(t.getCpf())
                .email(t.getEmail()).telefone(t.getTelefone()).endereco(t.getEndereco())
                .build();
    }

    private Tutor toEntity(TutorDTO dto) {
        return Tutor.builder()
                .nome(dto.getNome()).cpf(dto.getCpf())
                .email(dto.getEmail()).telefone(dto.getTelefone()).endereco(dto.getEndereco())
                .build();
    }
}
