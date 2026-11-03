package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.VeterinarioDTO;
import br.com.fiap.clyvovet.entity.Clinica;
import br.com.fiap.clyvovet.entity.Veterinario;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.repository.ClinicaRepository;
import br.com.fiap.clyvovet.repository.VeterinarioRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class VeterinarioService {

    private final VeterinarioRepository repository;
    private final ClinicaRepository clinicaRepository;

    public VeterinarioService(VeterinarioRepository repository, ClinicaRepository clinicaRepository) {
        this.repository = repository;
        this.clinicaRepository = clinicaRepository;
    }

    @Cacheable("veterinarios")
    public Page<VeterinarioDTO> listar(String nome, String especialidade, Long clinicaId, Pageable pageable) {
        if (clinicaId != null) return repository.findByClinicaId(clinicaId, pageable).map(this::toDTO);
        if (especialidade != null && !especialidade.isBlank())
            return repository.findByEspecialidadeContainingIgnoreCase(especialidade, pageable).map(this::toDTO);
        if (nome != null && !nome.isBlank())
            return repository.findByNomeContainingIgnoreCase(nome, pageable).map(this::toDTO);
        return repository.findAll(pageable).map(this::toDTO);
    }

    @Cacheable(value = "veterinarios", key = "#id")
    public VeterinarioDTO buscarPorId(Long id) {
        return toDTO(findOrFail(id));
    }

    @CacheEvict(value = "veterinarios", allEntries = true)
    public VeterinarioDTO criar(VeterinarioDTO dto) {
        Clinica clinica = clinicaRepository.findById(dto.getClinicaId())
                .orElseThrow(() -> new ResourceNotFoundException("Clínica não encontrada com id: " + dto.getClinicaId()));
        Veterinario vet = toEntity(dto);
        vet.setClinica(clinica);
        return toDTO(repository.save(vet));
    }

    @CacheEvict(value = "veterinarios", allEntries = true)
    public VeterinarioDTO atualizar(Long id, VeterinarioDTO dto) {
        Veterinario vet = findOrFail(id);
        Clinica clinica = clinicaRepository.findById(dto.getClinicaId())
                .orElseThrow(() -> new ResourceNotFoundException("Clínica não encontrada com id: " + dto.getClinicaId()));
        vet.setNome(dto.getNome());
        vet.setCrmv(dto.getCrmv());
        vet.setEspecialidade(dto.getEspecialidade());
        vet.setTelefone(dto.getTelefone());
        vet.setClinica(clinica);
        return toDTO(repository.save(vet));
    }

    @CacheEvict(value = "veterinarios", allEntries = true)
    public void deletar(Long id) {
        findOrFail(id);
        repository.deleteById(id);
    }

    private Veterinario findOrFail(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veterinário não encontrado com id: " + id));
    }

    private VeterinarioDTO toDTO(Veterinario v) {
        return VeterinarioDTO.builder()
                .id(v.getId()).nome(v.getNome()).crmv(v.getCrmv())
                .especialidade(v.getEspecialidade()).telefone(v.getTelefone())
                .clinicaId(v.getClinica().getId()).clinicaNome(v.getClinica().getNome())
                .build();
    }

    private Veterinario toEntity(VeterinarioDTO dto) {
        return Veterinario.builder()
                .nome(dto.getNome()).crmv(dto.getCrmv())
                .especialidade(dto.getEspecialidade()).telefone(dto.getTelefone())
                .build();
    }
}
