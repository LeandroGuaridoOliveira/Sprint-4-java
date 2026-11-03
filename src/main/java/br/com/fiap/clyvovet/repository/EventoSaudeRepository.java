package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.entity.EventoSaude;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoSaudeRepository extends JpaRepository<EventoSaude, Long> {
    Page<EventoSaude> findByTipoEvento(String tipoEvento, Pageable pageable);
    Page<EventoSaude> findByPetId(Long petId, Pageable pageable);
    Page<EventoSaude> findByVeterinarioId(Long veterinarioId, Pageable pageable);
    List<EventoSaude> findByPetIdOrderByDataEventoDesc(Long petId);
}
