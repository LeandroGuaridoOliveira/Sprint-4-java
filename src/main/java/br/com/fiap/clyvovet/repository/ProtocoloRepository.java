package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.entity.Protocolo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProtocoloRepository extends JpaRepository<Protocolo, Long> {
    Page<Protocolo> findByEspecieIgnoreCase(String especie, Pageable pageable);
    Page<Protocolo> findByTipoProtocoloIgnoreCase(String tipoProtocolo, Pageable pageable);
}
