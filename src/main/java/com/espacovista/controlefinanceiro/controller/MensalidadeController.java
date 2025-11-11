package com.espacovista.controlefinanceiro.controller;

import com.espacovista.controlefinanceiro.dto.MensalidadeStatusDTO;
import com.espacovista.controlefinanceiro.dto.RegistroPagamentoDTO;
import com.espacovista.controlefinanceiro.entity.enums.StatusPagamento;
import com.espacovista.controlefinanceiro.service.MensalidadeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mensalidades")
public class MensalidadeController {

    private final MensalidadeService mensalidadeService;

    public MensalidadeController(MensalidadeService mensalidadeService) {
        this.mensalidadeService = mensalidadeService;
    }

    @PostMapping("/pagar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MensalidadeStatusDTO> registrarPagamento(@Valid @RequestBody RegistroPagamentoDTO dto) {
        return ResponseEntity.ok(mensalidadeService.registrarPagamento(dto));
    }

    @GetMapping("/pendentes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MensalidadeStatusDTO>> pendentes(@RequestParam Integer ano, @RequestParam Integer mes) {
        return ResponseEntity.ok(mensalidadeService.buscarPendentes(ano, mes));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MensalidadeStatusDTO> alterarStatus(@PathVariable Long id, @RequestParam StatusPagamento status) {
        return ResponseEntity.ok(mensalidadeService.alterarStatus(id, status));
    }
}