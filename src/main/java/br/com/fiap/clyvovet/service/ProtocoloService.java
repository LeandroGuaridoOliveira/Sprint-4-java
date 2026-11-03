package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.ProtocoloDTO;
import br.com.fiap.clyvovet.entity.Protocolo;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.repository.ProtocoloRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProtocoloService {

    private final ProtocoloRepository repository;

    public ProtocoloService(ProtocoloRepository repository) {
        this.repository = repository;
    }

    @Cacheable("protocolos")
    public Page<ProtocoloDTO> listar(String especie, String tipoProtocolo, Pageable pageable) {
        if (especie != null && !especie.isBlank())
            return repository.findByEspecieIgnoreCase(especie, pageable).map(this::toDTO);
        if (tipoProtocolo != null && !tipoProtocolo.isBlank())
            return repository.findByTipoProtocoloIgnoreCase(tipoProtocolo, pageable).map(this::toDTO);
        return repository.findAll(pageable).map(this::toDTO);
    }

    @Cacheable(value = "protocolos", key = "#id")
    public ProtocoloDTO buscarPorId(Long id) {
        return toDTO(findOrFail(id));
    }

    public List<ProtocoloDTO> buscarProtocolosPorPet(String especie, String raca) {
        Page<Protocolo> byEspecie = repository.findByEspecieIgnoreCase(especie, Pageable.unpaged());
        return byEspecie.stream()
                .filter(p -> p.getRaca() == null || p.getRaca().equalsIgnoreCase(raca))
                .map(this::toDTO)
                .toList();
    }

    @CacheEvict(value = "protocolos", allEntries = true)
    public ProtocoloDTO criar(ProtocoloDTO dto) {
        return toDTO(repository.save(toEntity(dto)));
    }

    @CacheEvict(value = "protocolos", allEntries = true)
    public ProtocoloDTO atualizar(Long id, ProtocoloDTO dto) {
        Protocolo protocolo = findOrFail(id);
        protocolo.setEspecie(dto.getEspecie());
        protocolo.setRaca(dto.getRaca());
        protocolo.setTipoProtocolo(dto.getTipoProtocolo());
        protocolo.setDescricao(dto.getDescricao());
        protocolo.setIntervaloDias(dto.getIntervaloDias());
        protocolo.setFaixaEtaria(dto.getFaixaEtaria());
        return toDTO(repository.save(protocolo));
    }

    @CacheEvict(value = "protocolos", allEntries = true)
    public void deletar(Long id) {
        findOrFail(id);
        repository.deleteById(id);
    }

    private Protocolo findOrFail(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Protocolo não encontrado com id: " + id));
    }

    private ProtocoloDTO toDTO(Protocolo p) {
        return ProtocoloDTO.builder()
                .id(p.getId()).especie(p.getEspecie()).raca(p.getRaca())
                .tipoProtocolo(p.getTipoProtocolo()).descricao(p.getDescricao())
                .intervaloDias(p.getIntervaloDias()).faixaEtaria(p.getFaixaEtaria())
                .build();
    }

    private Protocolo toEntity(ProtocoloDTO dto) {
        return Protocolo.builder()
                .especie(dto.getEspecie()).raca(dto.getRaca())
                .tipoProtocolo(dto.getTipoProtocolo()).descricao(dto.getDescricao())
                .intervaloDias(dto.getIntervaloDias()).faixaEtaria(dto.getFaixaEtaria())
                .build();
    }
}
