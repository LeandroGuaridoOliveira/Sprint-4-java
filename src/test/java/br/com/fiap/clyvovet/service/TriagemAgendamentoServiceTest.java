package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.SolicitacaoAgendamentoDTO;
import br.com.fiap.clyvovet.entity.Agendamento;
import br.com.fiap.clyvovet.entity.Pet;
import br.com.fiap.clyvovet.entity.Veterinario;
import br.com.fiap.clyvovet.exception.RegraDeNegocioException;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.repository.AgendamentoRepository;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Triagem clínica e prevenção de conflitos de agenda")
class TriagemAgendamentoServiceTest {

    private static final Long PET_ID = 1L;
    private static final Long VET_ID = 2L;
    private static final ZoneId FUSO = ZoneOffset.UTC;

    /** Quarta-feira, 10:00. Referência fixa para todas as validações temporais. */
    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 6, 10, 10, 0);

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private VeterinarioRepository veterinarioRepository;

    private TriagemAgendamentoService service;
    private Pet pet;
    private Veterinario veterinario;

    @BeforeEach
    void setUp() {
        Clock relogioFixo = Clock.fixed(AGORA.toInstant(ZoneOffset.UTC), FUSO);
        service = new TriagemAgendamentoService(agendamentoRepository, petRepository, veterinarioRepository, relogioFixo);

        pet = Pet.builder().id(PET_ID).nome("Thor").especie("Canino").peso(12.0).build();
        veterinario = Veterinario.builder().id(VET_ID).nome("Dra. Helena").crmv("SP-12345").build();
    }

    @Test
    @DisplayName("Confirma o agendamento e registra o resumo da triagem quando não há conflito")
    void deveConfirmarAgendamentoSemConflito() {
        mockarEntidadesExistentes();
        mockarAgendaLivre();
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(inv -> inv.getArgument(0));

        Agendamento resultado = service.processarTriagemEAgendamento(solicitacaoValida().build());

        assertThat(resultado.getStatus()).isEqualTo("CONFIRMADO");
        assertThat(resultado.getPet()).isEqualTo(pet);
        assertThat(resultado.getVeterinario()).isEqualTo(veterinario);
        assertThat(resultado.getObservacao())
                .contains("[Triagem: ROTINA]")
                .contains("Queixa: Apatia e recusa alimentar ha dois dias");
    }

    @Test
    @DisplayName("Acrescenta o protocolo de jejum à observação quando solicitado na triagem")
    void deveRegistrarProtocoloDeJejum() {
        mockarEntidadesExistentes();
        mockarAgendaLivre();
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(inv -> inv.getArgument(0));

        Agendamento resultado = service.processarTriagemEAgendamento(
                solicitacaoValida().jejumRecomendado(true).build());

        assertThat(resultado.getObservacao()).contains("Jejum prévio de 8h solicitado");
    }

    @Test
    @DisplayName("Recusa agendamento com data passada")
    void deveRecusarDataPassada() {
        mockarEntidadesExistentes();

        assertThatThrownBy(() -> service.processarTriagemEAgendamento(
                solicitacaoValida().dataHora(AGORA.minusDays(1)).build()))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("data futura");

        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Recusa atendimento eletivo marcado para domingo")
    void deveRecusarAgendamentoNoDomingo() {
        mockarEntidadesExistentes();

        // 14/06/2026 é domingo
        LocalDateTime domingo = LocalDateTime.of(2026, 6, 14, 10, 0);

        assertThatThrownBy(() -> service.processarTriagemEAgendamento(
                solicitacaoValida().dataHora(domingo).build()))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("domingos");

        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Recusa horário depois das 18:00")
    void deveRecusarHorarioAposExpediente() {
        mockarEntidadesExistentes();

        assertThatThrownBy(() -> service.processarTriagemEAgendamento(
                solicitacaoValida().dataHora(AGORA.plusDays(1).withHour(19)).build()))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("08:00 às 18:00");

        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Recusa horário antes das 08:00")
    void deveRecusarHorarioAntesDoExpediente() {
        mockarEntidadesExistentes();

        assertThatThrownBy(() -> service.processarTriagemEAgendamento(
                solicitacaoValida().dataHora(AGORA.plusDays(1).withHour(7)).build()))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("08:00 às 18:00");

        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Aceita atendimento marcado exatamente às 08:00")
    void deveAceitarLimiteInferiorDoExpediente() {
        mockarEntidadesExistentes();
        mockarAgendaLivre();
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(inv -> inv.getArgument(0));

        Agendamento resultado = service.processarTriagemEAgendamento(
                solicitacaoValida().dataHora(AGORA.plusDays(1).withHour(8)).build());

        assertThat(resultado.getStatus()).isEqualTo("CONFIRMADO");
    }

    @Test
    @DisplayName("Recusa agendamento com menos de uma hora de antecedência")
    void deveExigirAntecedenciaMinima() {
        mockarEntidadesExistentes();

        assertThatThrownBy(() -> service.processarTriagemEAgendamento(
                solicitacaoValida().dataHora(AGORA.plusMinutes(30)).build()))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("antecedência mínima");

        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Bloqueia sobreposição na agenda do veterinário dentro da janela de 30 minutos")
    void deveBloquearConflitoDeVeterinario() {
        mockarEntidadesExistentes();
        when(agendamentoRepository.existsConflitoVeterinario(anyLong(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.processarTriagemEAgendamento(solicitacaoValida().build()))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Dra. Helena");

        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Aplica janela de conflito de 29 minutos antes e depois do horário solicitado")
    void deveConsultarJanelaDeConflitoCorreta() {
        mockarEntidadesExistentes();
        mockarAgendaLivre();
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(inv -> inv.getArgument(0));

        LocalDateTime dataHora = AGORA.plusDays(1).withHour(14);
        service.processarTriagemEAgendamento(solicitacaoValida().dataHora(dataHora).build());

        ArgumentCaptor<LocalDateTime> inicio = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> fim = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(agendamentoRepository).existsConflitoVeterinario(eq(VET_ID), inicio.capture(), fim.capture());

        assertThat(inicio.getValue()).isEqualTo(dataHora.minusMinutes(29));
        assertThat(fim.getValue()).isEqualTo(dataHora.plusMinutes(29));
    }

    @Test
    @DisplayName("Bloqueia o mesmo pet em dois atendimentos concorrentes")
    void deveBloquearConflitoDoPet() {
        mockarEntidadesExistentes();
        when(agendamentoRepository.existsConflitoVeterinario(anyLong(), any(), any())).thenReturn(false);
        when(agendamentoRepository.existsConflitoPet(anyLong(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.processarTriagemEAgendamento(solicitacaoValida().build()))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Thor");

        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Sinaliza pet inexistente antes de qualquer validação de agenda")
    void deveFalharQuandoPetNaoExiste() {
        when(petRepository.findById(PET_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.processarTriagemEAgendamento(solicitacaoValida().build()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pet não encontrado");
    }

    @Test
    @DisplayName("Sinaliza veterinário inexistente")
    void deveFalharQuandoVeterinarioNaoExiste() {
        when(petRepository.findById(PET_ID)).thenReturn(Optional.of(pet));
        when(veterinarioRepository.findById(VET_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.processarTriagemEAgendamento(solicitacaoValida().build()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Veterinário não encontrado");
    }

    private void mockarEntidadesExistentes() {
        when(petRepository.findById(PET_ID)).thenReturn(Optional.of(pet));
        when(veterinarioRepository.findById(VET_ID)).thenReturn(Optional.of(veterinario));
    }

    private void mockarAgendaLivre() {
        when(agendamentoRepository.existsConflitoVeterinario(anyLong(), any(), any())).thenReturn(false);
        when(agendamentoRepository.existsConflitoPet(anyLong(), any(), any())).thenReturn(false);
    }

    private SolicitacaoAgendamentoDTO.SolicitacaoAgendamentoDTOBuilder solicitacaoValida() {
        return SolicitacaoAgendamentoDTO.builder()
                .petId(PET_ID)
                .veterinarioId(VET_ID)
                .tipo("CONSULTA")
                .dataHora(AGORA.plusDays(1))
                .tipoTriagem("ROTINA")
                .queixaPrincipal("Apatia e recusa alimentar ha dois dias")
                .jejumRecomendado(false);
    }
}
