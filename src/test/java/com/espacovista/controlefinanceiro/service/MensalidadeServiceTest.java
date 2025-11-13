package com.espacovista.controlefinanceiro.service;

import com.espacovista.controlefinanceiro.dto.MensalidadeStatusDTO;
import com.espacovista.controlefinanceiro.dto.RegistroPagamentoDTO;
import com.espacovista.controlefinanceiro.entity.Aluno;
import com.espacovista.controlefinanceiro.entity.Mensalidade;
import com.espacovista.controlefinanceiro.entity.enums.StatusPagamento;
import com.espacovista.controlefinanceiro.repository.AlunoRepository;
import com.espacovista.controlefinanceiro.repository.MensalidadeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MensalidadeServiceTest {

    @Mock
    MensalidadeRepository mensalidadeRepository;
    @Mock
    AlunoRepository alunoRepository;

    @InjectMocks
    MensalidadeService mensalidadeService;

    @Test
    void registrarPagamento_criaQuandoNaoExiste_eMarcaPago() {
        when(mensalidadeRepository.findByAlunoIdAndAnoAndMes(1L, 2024, 11))
                .thenReturn(Optional.empty());

        Aluno aluno = new Aluno();
        aluno.setId(1L);
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));

        when(mensalidadeRepository.save(any())).thenAnswer(invocation -> {
            Mensalidade m = invocation.getArgument(0);
            m.setId(99L);
            return m;
        });

        RegistroPagamentoDTO dto = new RegistroPagamentoDTO();
        dto.alunoId = 1L;
        dto.ano = 2024;
        dto.mes = 11;
        dto.valorPago = new BigDecimal("150.00");
        dto.dataPagamento = LocalDate.of(2024, 11, 5);

        MensalidadeStatusDTO status = mensalidadeService.registrarPagamento(dto);
        Assertions.assertEquals("PAGO", status.statusPagamento);
        Assertions.assertEquals(99L, status.id);
    }

    @Test
    void registrarPagamento_atualizaQuandoExiste_eMarcaPago() {
        Mensalidade existente = new Mensalidade();
        existente.setId(50L);
        when(mensalidadeRepository.findByAlunoIdAndAnoAndMes(1L, 2024, 10))
                .thenReturn(Optional.of(existente));
        when(mensalidadeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RegistroPagamentoDTO dto = new RegistroPagamentoDTO();
        dto.alunoId = 1L;
        dto.ano = 2024;
        dto.mes = 10;
        dto.valorPago = new BigDecimal("99.90");

        MensalidadeStatusDTO status = mensalidadeService.registrarPagamento(dto);
        Assertions.assertEquals("PAGO", status.statusPagamento);
        Assertions.assertEquals(50L, status.id);
    }

    @Test
    void alterarStatus_pendente_zeraDataPagamentoEValorPago() {
        Mensalidade m = new Mensalidade();
        m.setId(10L);
        m.setDataPagamento(LocalDate.now());
        m.setValorPago(new BigDecimal("120.00"));
        when(mensalidadeRepository.findById(10L)).thenReturn(Optional.of(m));

        ArgumentCaptor<Mensalidade> captor = ArgumentCaptor.forClass(Mensalidade.class);

        MensalidadeStatusDTO status = mensalidadeService.alterarStatus(10L, StatusPagamento.PENDENTE);
        Assertions.assertEquals("PENDENTE", status.statusPagamento);

        verify(mensalidadeRepository).save(captor.capture());
        Mensalidade salvo = captor.getValue();
        Assertions.assertNull(salvo.getDataPagamento());
        Assertions.assertNull(salvo.getValorPago());
    }
}