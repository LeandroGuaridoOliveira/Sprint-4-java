package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.ClinicaDTO;
import br.com.fiap.clyvovet.entity.Clinica;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.repository.ClinicaRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ClinicaService {

    private final ClinicaRepository repository;

    public ClinicaService(ClinicaRepository repository) {
        this.repository = repository;
    }

    @Cacheable("clinicas")
    public Page<ClinicaDTO> listar(String nome, Pageable pageable) {
        Page<Clinica> page = (nome != null && !nome.isBlank())
                ? repository.findByNomeContainingIgnoreCase(nome, pageable)
                : repository.findAll(pageable);
        return page.map(this::toDTO);
    }

    @Cacheable(value = "clinicas", key = "#id")
    public ClinicaDTO buscarPorId(Long id) {
        return toDTO(findOrFail(id));
    }

    @CacheEvict(value = "clinicas", allEntries = true)
    public ClinicaDTO criar(ClinicaDTO dto) {
        Clinica clinica = toEntity(dto);
        return toDTO(repository.save(clinica));
    }

    @CacheEvict(value = "clinicas", allEntries = true)
    public ClinicaDTO atualizar(Long id, ClinicaDTO dto) {
        Clinica clinica = findOrFail(id);
        clinica.setNome(dto.getNome());
        clinica.setCnpj(dto.getCnpj());
        clinica.setEndereco(dto.getEndereco());
        clinica.setTelefone(dto.getTelefone());
        clinica.setEmail(dto.getEmail());
        return toDTO(repository.save(clinica));
    }

    @CacheEvict(value = "clinicas", allEntries = true)
    public void deletar(Long id) {
        findOrFail(id);
        repository.deleteById(id);
    }

    private Clinica findOrFail(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clínica não encontrada com id: " + id));
    }

    private ClinicaDTO toDTO(Clinica c) {
        return ClinicaDTO.builder()
                .id(c.getId()).nome(c.getNome()).cnpj(c.getCnpj())
                .endereco(c.getEndereco()).telefone(c.getTelefone()).email(c.getEmail())
                .build();
    }

    private Clinica toEntity(ClinicaDTO dto) {
        return Clinica.builder()
                .nome(dto.getNome()).cnpj(dto.getCnpj())
                .endereco(dto.getEndereco()).telefone(dto.getTelefone()).email(dto.getEmail())
                .build();
    }
}
