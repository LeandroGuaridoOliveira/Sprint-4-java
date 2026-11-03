package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.VeterinarioDTO;
import br.com.fiap.clyvovet.service.VeterinarioService;
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
@RequestMapping("/api/veterinarios")
@Tag(name = "Veterinários", description = "Gerenciamento de veterinários")
public class VeterinarioController {

    private final VeterinarioService service;

    public VeterinarioController(VeterinarioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar veterinários", description = "Busca por nome, especialidade ou clínica")
    public ResponseEntity<Page<VeterinarioDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String especialidade,
            @RequestParam(required = false) Long clinicaId,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(nome, especialidade, clinicaId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar veterinário por ID")
    public ResponseEntity<VeterinarioDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo veterinário")
    public ResponseEntity<VeterinarioDTO> criar(@Valid @RequestBody VeterinarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar veterinário")
    public ResponseEntity<VeterinarioDTO> atualizar(@PathVariable Long id, @Valid @RequestBody VeterinarioDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover veterinário")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
