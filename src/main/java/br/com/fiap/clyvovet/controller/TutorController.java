package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.TutorDTO;
import br.com.fiap.clyvovet.service.TutorService;
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
@RequestMapping("/api/tutores")
@Tag(name = "Tutores", description = "Gerenciamento de tutores (donos dos pets)")
public class TutorController {

    private final TutorService service;

    public TutorController(TutorService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar tutores", description = "Busca por nome com paginação e ordenação")
    public ResponseEntity<Page<TutorDTO>> listar(
            @RequestParam(required = false) String nome,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(nome, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tutor por ID")
    public ResponseEntity<TutorDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo tutor")
    public ResponseEntity<TutorDTO> criar(@Valid @RequestBody TutorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar tutor")
    public ResponseEntity<TutorDTO> atualizar(@PathVariable Long id, @Valid @RequestBody TutorDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover tutor")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
