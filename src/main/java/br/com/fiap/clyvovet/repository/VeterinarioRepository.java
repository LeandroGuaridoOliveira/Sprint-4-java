package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.entity.Veterinario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {
    Page<Veterinario> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Veterinario> findByEspecialidadeContainingIgnoreCase(String especialidade, Pageable pageable);
    Page<Veterinario> findByClinicaId(Long clinicaId, Pageable pageable);
}
