package com.espacovista.controlefinanceiro.controller;

import com.espacovista.controlefinanceiro.entity.Graduacao;
import com.espacovista.controlefinanceiro.repository.GraduacaoRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/graduacoes")
public class GraduacaoController {

    private final GraduacaoRepository graduacaoRepository;

    public GraduacaoController(GraduacaoRepository graduacaoRepository) {
        this.graduacaoRepository = graduacaoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Graduacao>> list() {
        return ResponseEntity.ok(graduacaoRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Graduacao> create(@Valid @RequestBody Graduacao g) {
        return ResponseEntity.ok(graduacaoRepository.save(g));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Graduacao> update(@PathVariable Long id, @Valid @RequestBody Graduacao g) {
        g.setId(id);
        return ResponseEntity.ok(graduacaoRepository.save(g));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        graduacaoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}