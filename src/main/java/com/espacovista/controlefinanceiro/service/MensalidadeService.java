package com.espacovista.controlefinanceiro.service;

import com.espacovista.controlefinanceiro.dto.MensalidadeStatusDTO;
import com.espacovista.controlefinanceiro.dto.RegistroPagamentoDTO;
import com.espacovista.controlefinanceiro.entity.Mensalidade;
import com.espacovista.controlefinanceiro.entity.Aluno;
import com.espacovista.controlefinanceiro.entity.enums.StatusPagamento;
import com.espacovista.controlefinanceiro.repository.MensalidadeRepository;
import com.espacovista.controlefinanceiro.repository.AlunoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MensalidadeService {

    private final MensalidadeRepository mensalidadeRepository;
    private final AlunoRepository alunoRepository;

    public MensalidadeService(MensalidadeRepository mensalidadeRepository, AlunoRepository alunoRepository) {
        this.mensalidadeRepository = mensalidadeRepository;
        this.alunoRepository = alunoRepository;
    }

    @Transactional
    public MensalidadeStatusDTO registrarPagamento(RegistroPagamentoDTO dto) {
        Mensalidade m = mensalidadeRepository
                .findByAlunoIdAndAnoAndMes(dto.alunoId, dto.ano, dto.mes)
                .orElseGet(Mensalidade::new);
        if (m.getId() == null) {
            Aluno aluno = alunoRepository.findById(dto.alunoId)
                    .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
            m.setAluno(aluno);
        }
        m.setAno(dto.ano);
        m.setMes(dto.mes);
        m.setStatusPagamento(StatusPagamento.PAGO);
        m.setDataPagamento(dto.dataPagamento != null ? dto.dataPagamento : LocalDate.now());
        m.setValorPago(dto.valorPago);
        m = mensalidadeRepository.save(m);
        return toStatusDTO(m, dto.alunoId);
    }

    @Transactional(readOnly = true)
    public List<MensalidadeStatusDTO> buscarPendentes(Integer ano, Integer mes) {
        return mensalidadeRepository.findByAnoMesAndStatus(ano, mes, StatusPagamento.PENDENTE)
                .stream().map(m -> toStatusDTO(m, m.getAluno() != null ? m.getAluno().getId() : null))
                .collect(Collectors.toList());
    }

    @Transactional
    public MensalidadeStatusDTO alterarStatus(Long id, StatusPagamento novoStatus) {
        Mensalidade m = mensalidadeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mensalidade não encontrada"));
        m.setStatusPagamento(novoStatus);
        if (novoStatus == StatusPagamento.PENDENTE) {
            m.setDataPagamento(null);
            m.setValorPago(null);
        }
        mensalidadeRepository.save(m);
        return toStatusDTO(m, m.getAluno() != null ? m.getAluno().getId() : null);
    }

    private MensalidadeStatusDTO toStatusDTO(Mensalidade m, Long alunoId) {
        MensalidadeStatusDTO dto = new MensalidadeStatusDTO();
        dto.id = m.getId();
        dto.alunoId = alunoId;
        dto.ano = m.getAno();
        dto.mes = m.getMes();
        dto.statusPagamento = m.getStatusPagamento().name();
        return dto;
    }
}