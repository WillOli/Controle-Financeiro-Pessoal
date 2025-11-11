package com.espacovista.controlefinanceiro.service;

import com.espacovista.controlefinanceiro.dto.AlunoCreateDTO;
import com.espacovista.controlefinanceiro.dto.AlunoDTO;
import com.espacovista.controlefinanceiro.dto.AlunoProfileDTO;
import com.espacovista.controlefinanceiro.dto.AlunoUpdateDTO;
import com.espacovista.controlefinanceiro.entity.Aluno;
import com.espacovista.controlefinanceiro.entity.Graduacao;
import com.espacovista.controlefinanceiro.entity.HistoricoGraduacao;
import com.espacovista.controlefinanceiro.entity.Mensalidade;
import com.espacovista.controlefinanceiro.entity.enums.StatusAluno;
import com.espacovista.controlefinanceiro.repository.AlunoRepository;
import com.espacovista.controlefinanceiro.repository.GraduacaoRepository;
import com.espacovista.controlefinanceiro.repository.HistoricoGraduacaoRepository;
import com.espacovista.controlefinanceiro.repository.MensalidadeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final GraduacaoRepository graduacaoRepository;
    private final HistoricoGraduacaoRepository historicoRepository;
    private final MensalidadeRepository mensalidadeRepository;

    public AlunoService(AlunoRepository alunoRepository,
                        GraduacaoRepository graduacaoRepository,
                        HistoricoGraduacaoRepository historicoRepository,
                        MensalidadeRepository mensalidadeRepository) {
        this.alunoRepository = alunoRepository;
        this.graduacaoRepository = graduacaoRepository;
        this.historicoRepository = historicoRepository;
        this.mensalidadeRepository = mensalidadeRepository;
    }

    @Transactional
    public AlunoDTO create(AlunoCreateDTO dto) {
        Aluno a = new Aluno();
        a.setNomeCompleto(dto.nomeCompleto);
        a.setFotoUrl(dto.fotoUrl);
        a.setDataNascimento(dto.dataNascimento);
        a.setCpfRg(dto.cpfRg);
        a.setTelefone(dto.telefone);
        a.setEmail(dto.email);
        a.setEndereco(dto.endereco);
        a.setContatoEmergenciaNome(dto.contatoEmergenciaNome);
        a.setContatoEmergenciaTelefone(dto.contatoEmergenciaTelefone);
        a.setRestricoesMedicas(dto.restricoesMedicas);
        a.setDataInicio(dto.dataInicio);
        a.setStatusAluno(dto.statusAluno != null ? dto.statusAluno : StatusAluno.ATIVO);
        if (dto.graduacaoAtualId != null) {
            Graduacao g = graduacaoRepository.findById(dto.graduacaoAtualId)
                    .orElseThrow(() -> new IllegalArgumentException("Graduação não encontrada"));
            a.setGraduacaoAtual(g);
        }
        a = alunoRepository.save(a);
        return toDTO(a);
    }

    @Transactional
    public AlunoDTO update(Long id, AlunoUpdateDTO dto) {
        Aluno a = alunoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
        if (dto.nomeCompleto != null) a.setNomeCompleto(dto.nomeCompleto);
        if (dto.fotoUrl != null) a.setFotoUrl(dto.fotoUrl);
        if (dto.dataNascimento != null) a.setDataNascimento(dto.dataNascimento);
        if (dto.cpfRg != null) a.setCpfRg(dto.cpfRg);
        if (dto.telefone != null) a.setTelefone(dto.telefone);
        if (dto.email != null) a.setEmail(dto.email);
        if (dto.endereco != null) a.setEndereco(dto.endereco);
        if (dto.contatoEmergenciaNome != null) a.setContatoEmergenciaNome(dto.contatoEmergenciaNome);
        if (dto.contatoEmergenciaTelefone != null) a.setContatoEmergenciaTelefone(dto.contatoEmergenciaTelefone);
        if (dto.restricoesMedicas != null) a.setRestricoesMedicas(dto.restricoesMedicas);
        if (dto.dataInicio != null) a.setDataInicio(dto.dataInicio);
        if (dto.statusAluno != null) a.setStatusAluno(dto.statusAluno);
        if (dto.graduacaoAtualId != null) {
            Graduacao g = graduacaoRepository.findById(dto.graduacaoAtualId)
                    .orElseThrow(() -> new IllegalArgumentException("Graduação não encontrada"));
            a.setGraduacaoAtual(g);
        }
        a = alunoRepository.save(a);
        return toDTO(a);
    }

    @Transactional(readOnly = true)
    public Page<AlunoDTO> search(String nome, StatusAluno status, Long graduacaoId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return alunoRepository.search(nullIfBlank(nome), status, graduacaoId, pageable)
                .map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public AlunoProfileDTO getProfile(Long id) {
        Aluno a = alunoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
        AlunoProfileDTO p = new AlunoProfileDTO();
        p.id = a.getId();
        p.nomeCompleto = a.getNomeCompleto();
        p.fotoUrl = a.getFotoUrl();
        p.dataNascimento = a.getDataNascimento();
        p.cpfRg = a.getCpfRg();
        p.telefone = a.getTelefone();
        p.email = a.getEmail();
        p.endereco = a.getEndereco();
        p.contatoEmergenciaNome = a.getContatoEmergenciaNome();
        p.contatoEmergenciaTelefone = a.getContatoEmergenciaTelefone();
        p.restricoesMedicas = a.getRestricoesMedicas();
        p.dataInicio = a.getDataInicio();
        p.statusAluno = a.getStatusAluno();
        p.graduacaoAtualId = a.getGraduacaoAtual() != null ? a.getGraduacaoAtual().getId() : null;
        p.graduacaoAtualNome = a.getGraduacaoAtual() != null ? a.getGraduacaoAtual().getNome() : null;

        List<HistoricoGraduacao> historico = historicoRepository.findByAlunoIdOrderByDataGraduacaoDesc(id);
        p.historicoGraduacoes = historico.stream().map(h -> {
            AlunoProfileDTO.HistoricoItem hi = new AlunoProfileDTO.HistoricoItem();
            hi.graduacaoId = h.getGraduacao().getId();
            hi.graduacaoNome = h.getGraduacao().getNome();
            hi.dataGraduacao = h.getDataGraduacao();
            return hi;
        }).collect(Collectors.toList());

        List<Mensalidade> mensalidades = mensalidadeRepository.findByAlunoIdOrderByAnoDescMesDesc(id);
        p.mensalidades = mensalidades.stream().map(m -> {
            AlunoProfileDTO.MensalidadeItem mi = new AlunoProfileDTO.MensalidadeItem();
            mi.ano = m.getAno();
            mi.mes = m.getMes();
            mi.statusPagamento = m.getStatusPagamento().name();
            mi.dataPagamento = m.getDataPagamento();
            mi.valorPago = m.getValorPago();
            return mi;
        }).collect(Collectors.toList());

        return p;
    }

    @Transactional
    public AlunoDTO changeStatus(Long id, StatusAluno status) {
        Aluno a = alunoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
        a.setStatusAluno(status);
        a = alunoRepository.save(a);
        return toDTO(a);
    }

    private AlunoDTO toDTO(Aluno a) {
        AlunoDTO dto = new AlunoDTO();
        dto.id = a.getId();
        dto.nomeCompleto = a.getNomeCompleto();
        dto.fotoUrl = a.getFotoUrl();
        dto.dataNascimento = a.getDataNascimento();
        dto.cpfRg = a.getCpfRg();
        dto.telefone = a.getTelefone();
        dto.email = a.getEmail();
        dto.endereco = a.getEndereco();
        dto.contatoEmergenciaNome = a.getContatoEmergenciaNome();
        dto.contatoEmergenciaTelefone = a.getContatoEmergenciaTelefone();
        dto.restricoesMedicas = a.getRestricoesMedicas();
        dto.dataInicio = a.getDataInicio();
        dto.statusAluno = a.getStatusAluno();
        dto.graduacaoAtualId = a.getGraduacaoAtual() != null ? a.getGraduacaoAtual().getId() : null;
        dto.graduacaoAtualNome = a.getGraduacaoAtual() != null ? a.getGraduacaoAtual().getNome() : null;
        return dto;
    }

    private String nullIfBlank(String v) {
        return v == null || v.trim().isEmpty() ? null : v;
    }
}