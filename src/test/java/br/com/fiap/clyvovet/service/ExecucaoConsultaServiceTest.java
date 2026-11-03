package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.AtendimentoConsultaDTO;
import br.com.fiap.clyvovet.entity.Agendamento;
import br.com.fiap.clyvovet.entity.EventoSaude;
import br.com.fiap.clyvovet.entity.Pet;
import br.com.fiap.clyvovet.entity.Veterinario;
import br.com.fiap.clyvovet.exception.RegraDeNegocioException;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.repository.AgendamentoRepository;
import br.com.fiap.clyvovet.repository.EventoSaudeRepository;
import br.com.fiap.clyvovet.repository.PetRepository;
import br.com.fiap.clyvovet.repository.VeterinarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Execução clínica do atendimento e atualização de prontuário")
class ExecucaoConsultaServiceTest {

    private static final Long AGENDAMENTO_ID = 10L;
    private static final Long PET_ID = 1L;

    /** Quarta-feira, 10:00. Referência fixa para o carimbo de encerramento do atendimento. */
    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 6, 10, 10, 0);

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private VeterinarioRepository veterinarioRepository;

    @Mock
    private EventoSaudeRepository eventoSaudeRepository;

    private ExecucaoConsultaService service;

    private Pet pet;
    private Veterinario veterinario;
    private Agendamento agendamento;

    @BeforeEach
    void setUp() {
        Clock relogioFixo = Clock.fixed(AGORA.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        service = new ExecucaoConsultaService(agendamentoRepository, petRepository,
                veterinarioRepository, eventoSaudeRepository, relogioFixo);

        pet = Pet.builder().id(PET_ID).nome("Thor").especie("Canino").peso(12.0).build();
        veterinario = Veterinario.builder().id(2L).nome("Dra. Helena").crmv("SP-12345").build();
        agendamento = Agendamento.builder()
                .id(AGENDAMENTO_ID)
                .pet(pet)
                .veterinario(veterinario)
                .status("CONFIRMADO")
                .tipo("CONSULTA")
                .dataAgendamento(AGORA.plusHours(2))
                .observacao("[Triagem: ROTINA] Queixa: Apatia")
                .build();
    }

    @Test
    @DisplayName("Atualiza peso, cria evento de saúde e conclui o agendamento em uma única operação")
    void deveConsolidarAtendimentoCompleto() {
        mockarAtendimentoDisponivel();
        when(eventoSaudeRepository.save(any(EventoSaude.class))).thenAnswer(inv -> inv.getArgument(0));

        EventoSaude evento = service.executarAtendimentoClinico(dtoValido().build());

        assertThat(pet.getPeso()).isEqualTo(13.4);
        verify(petRepository).save(pet);

        assertThat(evento.getPet()).isEqualTo(pet);
        assertThat(evento.getVeterinario()).isEqualTo(veterinario);
        assertThat(evento.getDataEvento()).isEqualTo(LocalDate.of(2026, 6, 10));
        assertThat(evento.getTipoEvento()).isEqualTo("CONSULTA_CLINICA");

        ArgumentCaptor<Agendamento> capturado = ArgumentCaptor.forClass(Agendamento.class);
        verify(agendamentoRepository).save(capturado.capture());
        assertThat(capturado.getValue().getStatus()).isEqualTo("CONCLUIDO");
        assertThat(capturado.getValue().getObservacao())
                .contains("[Triagem: ROTINA]")
                .contains("Atendimento finalizado em 10/06/2026 10:00")
                .contains("Dra. Helena");
    }

    @Test
    @DisplayName("Anexa as orientações ao tutor na descrição do prontuário")
    void deveAnexarOrientacoesAoTutor() {
        mockarAtendimentoDisponivel();
        when(eventoSaudeRepository.save(any(EventoSaude.class))).thenAnswer(inv -> inv.getArgument(0));

        EventoSaude evento = service.executarAtendimentoClinico(
                dtoValido().observacoesTutor("Retornar em 15 dias").build());

        assertThat(evento.getDescricao()).contains("[Orientações ao Tutor: Retornar em 15 dias]");
    }

    @Test
    @DisplayName("Sinaliza no desfecho quando o protocolo preventivo é aplicado")
    void deveRegistrarProtocoloPreventivo() {
        mockarAtendimentoDisponivel();
        when(eventoSaudeRepository.save(any(EventoSaude.class))).thenAnswer(inv -> inv.getArgument(0));

        EventoSaude evento = service.executarAtendimentoClinico(
                dtoValido().aplicarProtocoloPreventivo(true).build());

        assertThat(evento.getResultado()).contains("Protocolo Preventivo / Vacinal atualizado");
    }

    @Test
    @DisplayName("Impede novo atendimento sobre agendamento já concluído")
    void deveImpedirAtendimentoDuplicado() {
        agendamento.setStatus("CONCLUIDO");
        when(agendamentoRepository.findById(AGENDAMENTO_ID)).thenReturn(Optional.of(agendamento));

        assertThatThrownBy(() -> service.executarAtendimentoClinico(dtoValido().build()))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("já foi finalizado");

        verify(eventoSaudeRepository, never()).save(any());
        verify(petRepository, never()).save(any());
    }

    @Test
    @DisplayName("Impede atendimento sobre agendamento cancelado")
    void deveImpedirAtendimentoCancelado() {
        agendamento.setStatus("CANCELADO");
        when(agendamentoRepository.findById(AGENDAMENTO_ID)).thenReturn(Optional.of(agendamento));

        assertThatThrownBy(() -> service.executarAtendimentoClinico(dtoValido().build()))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("cancelada");

        verify(eventoSaudeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Sinaliza agendamento inexistente")
    void deveFalharQuandoAgendamentoNaoExiste() {
        when(agendamentoRepository.findById(AGENDAMENTO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.executarAtendimentoClinico(dtoValido().build()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Agendamento não encontrado");
    }

    @Test
    @DisplayName("Mantém o veterinário da consulta quando o informado no formulário não existe")
    void deveManterVeterinarioDaConsultaQuandoInformadoNaoExiste() {
        mockarAtendimentoDisponivel();
        when(veterinarioRepository.findById(99L)).thenReturn(Optional.empty());
        when(eventoSaudeRepository.save(any(EventoSaude.class))).thenAnswer(inv -> inv.getArgument(0));

        EventoSaude evento = service.executarAtendimentoClinico(
                dtoValido().veterinarioId(99L).build());

        assertThat(evento.getVeterinario()).isEqualTo(veterinario);
    }

    private void mockarAtendimentoDisponivel() {
        when(agendamentoRepository.findById(AGENDAMENTO_ID)).thenReturn(Optional.of(agendamento));
        when(petRepository.findById(PET_ID)).thenReturn(Optional.of(pet));
    }

    private AtendimentoConsultaDTO.AtendimentoConsultaDTOBuilder dtoValido() {
        return AtendimentoConsultaDTO.builder()
                .agendamentoId(AGENDAMENTO_ID)
                .petId(PET_ID)
                .pesoAtual(13.4)
                .condutaClinica("Exame fisico sem alteracoes relevantes, hidratacao preservada.")
                .prescricaoOuResultado("Dieta leve por cinco dias")
                .tipoProcedimento("CONSULTA_CLINICA")
                .aplicarProtocoloPreventivo(false);
    }
}
