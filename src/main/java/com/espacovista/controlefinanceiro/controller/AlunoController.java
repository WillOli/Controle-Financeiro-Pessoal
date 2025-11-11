package com.espacovista.controlefinanceiro.controller;

import com.espacovista.controlefinanceiro.dto.AlunoCreateDTO;
import com.espacovista.controlefinanceiro.dto.AlunoDTO;
import com.espacovista.controlefinanceiro.dto.AlunoProfileDTO;
import com.espacovista.controlefinanceiro.dto.PromocaoAlunoDTO;
import com.espacovista.controlefinanceiro.dto.AlunoUpdateDTO;
import com.espacovista.controlefinanceiro.entity.enums.StatusAluno;
import com.espacovista.controlefinanceiro.service.AlunoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @PostMapping
    public ResponseEntity<AlunoDTO> create(@Valid @RequestBody AlunoCreateDTO dto) {
        return ResponseEntity.ok(alunoService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlunoDTO> update(@PathVariable Long id, @Valid @RequestBody AlunoUpdateDTO dto) {
        return ResponseEntity.ok(alunoService.update(id, dto));
    }

    @GetMapping
    @PreAuthorize("hasRole('INSTRUTOR') or hasRole('ADMIN')")
    public ResponseEntity<Page<AlunoDTO>> search(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) StatusAluno status,
            @RequestParam(required = false, name = "graduacaoId") Long graduacaoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(alunoService.search(nome, status, graduacaoId, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoProfileDTO> profile(@PathVariable Long id) {
        return ResponseEntity.ok(alunoService.getProfile(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AlunoDTO> changeStatus(@PathVariable Long id, @RequestParam StatusAluno status) {
        return ResponseEntity.ok(alunoService.changeStatus(id, status));
    }

    @PostMapping("/{id}/promover")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> promover(@PathVariable Long id, @Valid @RequestBody PromocaoAlunoDTO dto) {
        alunoService.promoverAluno(id, dto);
        return ResponseEntity.ok().build();
    }
}