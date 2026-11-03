package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.entity.Agendamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    Page<Agendamento> findByStatus(String status, Pageable pageable);
    Page<Agendamento> findByTipo(String tipo, Pageable pageable);
    Page<Agendamento> findByPetId(Long petId, Pageable pageable);
    Page<Agendamento> findByVeterinarioId(Long veterinarioId, Pageable pageable);

    List<Agendamento> findAllByOrderByDataAgendamentoDesc();
    List<Agendamento> findByVeterinarioIdOrderByDataAgendamentoAsc(Long veterinarioId);
    List<Agendamento> findByPetIdOrderByDataAgendamentoDesc(Long petId);
    List<Agendamento> findByPetTutorIdOrderByDataAgendamentoDesc(Long tutorId);
    List<Agendamento> findByStatusIgnoreCaseOrderByDataAgendamentoAsc(String status);

    @Query("SELECT COUNT(a) > 0 FROM Agendamento a WHERE a.veterinario.id = :vetId AND UPPER(a.status) <> 'CANCELADO' AND a.dataAgendamento BETWEEN :inicio AND :fim")
    boolean existsConflitoVeterinario(@Param("vetId") Long vetId, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COUNT(a) > 0 FROM Agendamento a WHERE a.pet.id = :petId AND UPPER(a.status) <> 'CANCELADO' AND a.dataAgendamento BETWEEN :inicio AND :fim")
    boolean existsConflitoPet(@Param("petId") Long petId, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
