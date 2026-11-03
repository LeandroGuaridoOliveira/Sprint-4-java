package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.ProtocoloDTO;
import br.com.fiap.clyvovet.service.ProtocoloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/protocolos")
@Tag(name = "Protocolos", description = "Protocolos preventivos e terapêuticos por espécie/raça")
public class ProtocoloController {

    private final ProtocoloService service;

    public ProtocoloController(ProtocoloService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar protocolos", description = "Busca por espécie ou tipo de protocolo")
    public ResponseEntity<Page<ProtocoloDTO>> listar(
            @RequestParam(required = false) String especie,
            @RequestParam(required = false) String tipoProtocolo,
            @PageableDefault(size = 10, sort = "especie") Pageable pageable) {
        return ResponseEntity.ok(service.listar(especie, tipoProtocolo, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar protocolo por ID")
    public ResponseEntity<ProtocoloDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/pet")
    @Operation(summary = "Buscar protocolos aplicáveis a um pet", description = "Retorna protocolos compatíveis com a espécie e raça informadas")
    public ResponseEntity<List<ProtocoloDTO>> buscarPorPet(
            @RequestParam String especie,
            @RequestParam(required = false) String raca) {
        return ResponseEntity.ok(service.buscarProtocolosPorPet(especie, raca));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo protocolo")
    public ResponseEntity<ProtocoloDTO> criar(@Valid @RequestBody ProtocoloDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar protocolo")
    public ResponseEntity<ProtocoloDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ProtocoloDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover protocolo")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
