package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.AgendamentoDTO;
import br.com.fiap.clyvovet.service.AgendamentoService;
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
@RequestMapping("/api/agendamentos")
@Tag(name = "Agendamentos", description = "Gerenciamento de agendamentos de consultas e procedimentos")
public class AgendamentoController {

    private final AgendamentoService service;

    public AgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar agendamentos", description = "Busca por status, tipo, pet ou veterinário")
    public ResponseEntity<Page<AgendamentoDTO>> listar(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) Long veterinarioId,
            @PageableDefault(size = 10, sort = "dataAgendamento") Pageable pageable) {
        return ResponseEntity.ok(service.listar(status, tipo, petId, veterinarioId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar agendamento por ID")
    public ResponseEntity<AgendamentoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo agendamento")
    public ResponseEntity<AgendamentoDTO> criar(@Valid @RequestBody AgendamentoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar agendamento")
    public ResponseEntity<AgendamentoDTO> atualizar(@PathVariable Long id, @Valid @RequestBody AgendamentoDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover agendamento")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
