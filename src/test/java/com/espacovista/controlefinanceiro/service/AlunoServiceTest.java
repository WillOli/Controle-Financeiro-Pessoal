package com.espacovista.controlefinanceiro.service;

import com.espacovista.controlefinanceiro.dto.AlunoProfileDTO;
import com.espacovista.controlefinanceiro.dto.AlunoUpdateDTO;
import com.espacovista.controlefinanceiro.dto.PromocaoAlunoDTO;
import com.espacovista.controlefinanceiro.entity.Aluno;
import com.espacovista.controlefinanceiro.entity.Graduacao;
import com.espacovista.controlefinanceiro.entity.HistoricoGraduacao;
import com.espacovista.controlefinanceiro.entity.Mensalidade;
import com.espacovista.controlefinanceiro.entity.enums.StatusAluno;
import com.espacovista.controlefinanceiro.repository.AlunoRepository;
import com.espacovista.controlefinanceiro.repository.GraduacaoRepository;
import com.espacovista.controlefinanceiro.repository.HistoricoGraduacaoRepository;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock
    AlunoRepository alunoRepository;
    @Mock
    GraduacaoRepository graduacaoRepository;
    @Mock
    HistoricoGraduacaoRepository historicoRepository;
    @Mock
    MensalidadeRepository mensalidadeRepository;

    @InjectMocks
    AlunoService alunoService;

    @Test
    void promoverAluno_deveAtualizarGraduacaoESalvarHistorico() {
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        Graduacao atual = new Graduacao();
        atual.setId(10L);
        atual.setNome("Graduacao Atual");
        aluno.setGraduacaoAtual(atual);

        Graduacao nova = new Graduacao();
        nova.setId(20L);
        nova.setNome("Nova Graduacao");

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(graduacaoRepository.findById(20L)).thenReturn(Optional.of(nova));

        PromocaoAlunoDTO dto = new PromocaoAlunoDTO();
        dto.novaGraduacaoId = 20L;
        dto.dataGraduacao = LocalDate.now();

        alunoService.promoverAluno(1L, dto);

        ArgumentCaptor<Aluno> alunoCaptor = ArgumentCaptor.forClass(Aluno.class);
        verify(alunoRepository).save(alunoCaptor.capture());
        Aluno salvo = alunoCaptor.getValue();
        Assertions.assertEquals(20L, salvo.getGraduacaoAtual().getId());

        verify(historicoRepository, times(1)).save(any(HistoricoGraduacao.class));
    }

    @Test
    void promoverAluno_deveLancarQuandoAlunoNaoEncontrado() {
        when(alunoRepository.findById(99L)).thenReturn(Optional.empty());
        PromocaoAlunoDTO dto = new PromocaoAlunoDTO();
        dto.novaGraduacaoId = 1L;
        dto.dataGraduacao = LocalDate.now();

        Assertions.assertThrows(IllegalArgumentException.class, () -> alunoService.promoverAluno(99L, dto));
        verify(historicoRepository, never()).save(any());
        verify(alunoRepository, never()).save(any());
    }

    @Test
    void update_deveLancarQuandoDTOContemGraduacaoAtualId() {
        Aluno a = new Aluno();
        a.setId(5L);
        when(alunoRepository.findById(5L)).thenReturn(Optional.of(a));

        AlunoUpdateDTO dto = new AlunoUpdateDTO();
        dto.graduacaoAtualId = 10L;

        Assertions.assertThrows(IllegalArgumentException.class, () -> alunoService.update(5L, dto));
        verify(alunoRepository, never()).save(any());
    }

    @Test
    void update_deveSalvarDadosAtualizadosDoDTO() {
        Aluno a = new Aluno();
        a.setId(5L);
        a.setTelefone("1111");
        a.setEmail("old@mail.com");
        when(alunoRepository.findById(5L)).thenReturn(Optional.of(a));

        AlunoUpdateDTO dto = new AlunoUpdateDTO();
        dto.telefone = "9999";
        dto.email = "new@mail.com";

        when(alunoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = alunoService.update(5L, dto);
        Assertions.assertEquals("9999", result.telefone);
        Assertions.assertEquals("new@mail.com", result.email);
        verify(alunoRepository, times(1)).save(any(Aluno.class));
    }

    @Test
    void getProfile_deveConsultarRepositoriosERetornarDadosCorretos() {
        Aluno a = new Aluno();
        a.setId(1L);
        a.setNomeCompleto("Fulano");
        a.setTelefone("123");
        Graduacao g = new Graduacao();
        g.setId(2L);
        g.setNome("Azul");
        a.setGraduacaoAtual(g);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(a));

        HistoricoGraduacao h = new HistoricoGraduacao();
        h.setAluno(a);
        h.setGraduacao(g);
        h.setDataGraduacao(LocalDate.of(2024, 1, 1));
        when(historicoRepository.findByAlunoIdOrderByDataGraduacaoDesc(1L))
                .thenReturn(List.of(h));

        Mensalidade m = new Mensalidade();
        m.setAno(2024);
        m.setMes(10);
        m.setDataPagamento(LocalDate.of(2024, 10, 10));
        m.setValorPago(new BigDecimal("100.00"));
        when(mensalidadeRepository.findByAlunoIdOrderByAnoDescMesDesc(1L))
                .thenReturn(List.of(m));

        AlunoProfileDTO profile = alunoService.getProfile(1L);

        verify(alunoRepository, times(1)).findById(1L);
        verify(historicoRepository, times(1)).findByAlunoIdOrderByDataGraduacaoDesc(1L);
        verify(mensalidadeRepository, times(1)).findByAlunoIdOrderByAnoDescMesDesc(1L);

        Assertions.assertEquals("Fulano", profile.nomeCompleto);
        Assertions.assertEquals(2L, profile.graduacaoAtualId);
        Assertions.assertEquals("Azul", profile.graduacaoAtualNome);
        Assertions.assertEquals(1, profile.historicoGraduacoes.size());
        Assertions.assertEquals(1, profile.mensalidades.size());
        Assertions.assertEquals("100.00", profile.mensalidades.get(0).valorPago.toPlainString());
    }
}