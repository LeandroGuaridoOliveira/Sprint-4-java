package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.AgendamentoDTO;
import br.com.fiap.clyvovet.dto.PetDTO;
import br.com.fiap.clyvovet.dto.SolicitacaoAgendamentoDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Invalidação de Cache nos Fluxos Web e Atualização Imediata da REST API")
class CacheInvalidacaoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PetService petService;

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    @DisplayName("Finalização de atendimento web deve invalidar caches de pets e agendamentos imediatamente")
    @WithMockUser(username = "veterinario@clyvo.com", roles = "VET")
    void finalizacaoDeAtendimentoDeveInvalidarCachesERefletirNaApiRest() throws Exception {
        // 1. Acesso à API REST para popular o cache de pets e agendamentos (usando Luna id=2 e Agendamento id=3)
        mockMvc.perform(get("/api/pets/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.peso", is(4.2)));

        mockMvc.perform(get("/api/agendamentos/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("AGENDADO")));

        // Confirma que os valores estão armazenados em cache
        assertThat(cacheManager.getCache("pets")).isNotNull();
        assertThat(cacheManager.getCache("pets").get(2L)).isNotNull();
        assertThat(cacheManager.getCache("agendamentos")).isNotNull();
        assertThat(cacheManager.getCache("agendamentos").get(3L)).isNotNull();

        // 2. Executa finalização da consulta via POST web (/atendimentos/finalizar) alterando o peso de Luna para 4.6
        mockMvc.perform(post("/atendimentos/finalizar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("agendamentoId", "3")
                        .param("petId", "2")
                        .param("veterinarioId", "2")
                        .param("pesoAtual", "4.6")
                        .param("tipoProcedimento", "Consulta")
                        .param("condutaClinica", "Exame clínico de rotina com aferição biométrica.")
                        .param("prescricaoOuResultado", "Animal em excelente estado clínico.")
                        .param("aplicarProtocoloPreventivo", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pets/2/prontuario"));

        // 3. Verifica que a API REST (GET /api/pets/2) imediatamente retorna o novo peso (4.6), sem stale cache
        mockMvc.perform(get("/api/pets/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.peso", is(4.6)));

        // 4. Verifica que a API REST (GET /api/agendamentos/3) imediatamente retorna status CONCLUIDO
        mockMvc.perform(get("/api/agendamentos/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONCLUIDO")));
    }

    @Test
    @DisplayName("Criação de agendamento por triagem deve invalidar o cache de listagem de agendamentos")
    @WithMockUser(username = "veterinario@clyvo.com", roles = "VET")
    void criacaoDeAgendamentoPorTriagemDeveInvalidarCacheDeAgendamentos() throws Exception {
        // 1. Chama a listagem da API REST para popular o cache "agendamentos"
        mockMvc.perform(get("/api/agendamentos?page=0&size=50"))
                .andExpect(status().isOk());

        // 2. Submete novo agendamento via fluxo web (/agendamentos/solicitar)
        LocalDateTime dataFutura = LocalDateTime.now().plusDays(5).withHour(11).withMinute(0).withSecond(0).withNano(0);
        mockMvc.perform(post("/agendamentos/solicitar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("petId", "3")
                        .param("veterinarioId", "1")
                        .param("dataHora", dataFutura.toString())
                        .param("tipo", "Consulta")
                        .param("tipoTriagem", "Check-up preventivo")
                        .param("queixaPrincipal", "Consulta preventiva de rotina sem queixas")
                        .param("jejumRecomendado", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamentos"));

        // 3. Consulta imediatamente a API REST e confirma que o cache foi invalidado e contém o agendamento
        mockMvc.perform(get("/api/agendamentos?petId=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)));
    }
}
