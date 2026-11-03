package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.ClinicaDTO;
import br.com.fiap.clyvovet.service.ClinicaService;
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
@RequestMapping("/api/clinicas")
@Tag(name = "Clínicas", description = "Gerenciamento de clínicas veterinárias")
public class ClinicaController {

    private final ClinicaService service;

    public ClinicaController(ClinicaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar clínicas", description = "Lista clínicas com paginação, ordenação e busca por nome")
    public ResponseEntity<Page<ClinicaDTO>> listar(
            @RequestParam(required = false) String nome,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(nome, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar clínica por ID")
    public ResponseEntity<ClinicaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar nova clínica")
    public ResponseEntity<ClinicaDTO> criar(@Valid @RequestBody ClinicaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar clínica")
    public ResponseEntity<ClinicaDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ClinicaDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover clínica")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
