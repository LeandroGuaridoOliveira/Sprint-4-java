package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.EventoSaudeDTO;
import br.com.fiap.clyvovet.service.EventoSaudeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eventos-saude")
@Tag(name = "Eventos de Saúde", description = "Registro de consultas, vacinas, cirurgias, exames e check-ups")
public class EventoSaudeController {

    private final EventoSaudeService service;

    public EventoSaudeController(EventoSaudeService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar eventos de saúde", description = "Busca por tipo, pet ou veterinário")
    public ResponseEntity<Page<EventoSaudeDTO>> listar(
            @RequestParam(required = false) String tipoEvento,
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) Long veterinarioId,
            @PageableDefault(size = 10, sort = "dataEvento") Pageable pageable) {
        return ResponseEntity.ok(service.listar(tipoEvento, petId, veterinarioId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar evento de saúde por ID")
    public ResponseEntity<EventoSaudeDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar novo evento de saúde")
    public ResponseEntity<EventoSaudeDTO> criar(@Valid @RequestBody EventoSaudeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar evento de saúde")
    public ResponseEntity<EventoSaudeDTO> atualizar(@PathVariable Long id, @Valid @RequestBody EventoSaudeDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover evento de saúde")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
