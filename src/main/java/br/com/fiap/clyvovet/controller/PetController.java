package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.PetDTO;
import br.com.fiap.clyvovet.service.PetService;
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
@RequestMapping("/api/pets")
@Tag(name = "Pets", description = "Gerenciamento de pets")
public class PetController {

    private final PetService service;

    public PetController(PetService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar pets", description = "Busca por nome, espécie ou tutor")
    public ResponseEntity<Page<PetDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String especie,
            @RequestParam(required = false) Long tutorId,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(nome, especie, tutorId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pet por ID")
    public ResponseEntity<PetDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo pet")
    public ResponseEntity<PetDTO> criar(@Valid @RequestBody PetDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pet")
    public ResponseEntity<PetDTO> atualizar(@PathVariable Long id, @Valid @RequestBody PetDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover pet")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
