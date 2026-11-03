package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.PetDTO;
import br.com.fiap.clyvovet.entity.Pet;
import br.com.fiap.clyvovet.entity.Tutor;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.repository.PetRepository;
import br.com.fiap.clyvovet.repository.TutorRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PetService {

    private final PetRepository repository;
    private final TutorRepository tutorRepository;

    public PetService(PetRepository repository, TutorRepository tutorRepository) {
        this.repository = repository;
        this.tutorRepository = tutorRepository;
    }

    @Cacheable("pets")
    public Page<PetDTO> listar(String nome, String especie, Long tutorId, Pageable pageable) {
        if (tutorId != null) return repository.findByTutorId(tutorId, pageable).map(this::toDTO);
        if (especie != null && !especie.isBlank())
            return repository.findByEspecieIgnoreCase(especie, pageable).map(this::toDTO);
        if (nome != null && !nome.isBlank())
            return repository.findByNomeContainingIgnoreCase(nome, pageable).map(this::toDTO);
        return repository.findAll(pageable).map(this::toDTO);
    }

    @Cacheable(value = "pets", key = "#id")
    public PetDTO buscarPorId(Long id) {
        return toDTO(findOrFail(id));
    }

    @CacheEvict(value = "pets", allEntries = true)
    public PetDTO criar(PetDTO dto) {
        Tutor tutor = tutorRepository.findById(dto.getTutorId())
                .orElseThrow(() -> new ResourceNotFoundException("Tutor não encontrado com id: " + dto.getTutorId()));
        Pet pet = toEntity(dto);
        pet.setTutor(tutor);
        return toDTO(repository.save(pet));
    }

    @CacheEvict(value = "pets", allEntries = true)
    public PetDTO atualizar(Long id, PetDTO dto) {
        Pet pet = findOrFail(id);
        Tutor tutor = tutorRepository.findById(dto.getTutorId())
                .orElseThrow(() -> new ResourceNotFoundException("Tutor não encontrado com id: " + dto.getTutorId()));
        pet.setNome(dto.getNome());
        pet.setEspecie(dto.getEspecie());
        pet.setRaca(dto.getRaca());
        pet.setDataNascimento(dto.getDataNascimento());
        pet.setPeso(dto.getPeso());
        pet.setTutor(tutor);
        return toDTO(repository.save(pet));
    }

    @CacheEvict(value = "pets", allEntries = true)
    public void deletar(Long id) {
        findOrFail(id);
        repository.deleteById(id);
    }

    private Pet findOrFail(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + id));
    }

    private PetDTO toDTO(Pet p) {
        return PetDTO.builder()
                .id(p.getId()).nome(p.getNome()).especie(p.getEspecie())
                .raca(p.getRaca()).dataNascimento(p.getDataNascimento()).peso(p.getPeso())
                .tutorId(p.getTutor().getId()).tutorNome(p.getTutor().getNome())
                .build();
    }

    private Pet toEntity(PetDTO dto) {
        return Pet.builder()
                .nome(dto.getNome()).especie(dto.getEspecie())
                .raca(dto.getRaca()).dataNascimento(dto.getDataNascimento()).peso(dto.getPeso())
                .build();
    }
}
