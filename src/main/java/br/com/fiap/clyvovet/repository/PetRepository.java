package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {
    Page<Pet> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Pet> findByEspecieIgnoreCase(String especie, Pageable pageable);
    Page<Pet> findByTutorId(Long tutorId, Pageable pageable);
    List<Pet> findByTutorId(Long tutorId);
}
