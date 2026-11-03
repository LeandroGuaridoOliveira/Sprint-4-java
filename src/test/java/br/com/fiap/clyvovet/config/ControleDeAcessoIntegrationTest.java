package br.com.fiap.clyvovet.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Controle de acesso por perfil")
class ControleDeAcessoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("A tela de login é pública")
    void loginDeveSerPublico() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Visitante anônimo é redirecionado ao login ao tentar abrir o painel")
    void visitanteAnonimoDeveSerRedirecionado() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("Tutor não acessa a fila de atendimento clínico")
    @WithMockUser(username = "tutor@clyvo.com", roles = "TUTOR")
    void tutorNaoDeveAcessarFilaClinica() throws Exception {
        mockMvc.perform(get("/atendimentos"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Veterinário acessa a fila de atendimento clínico")
    @WithMockUser(username = "veterinario@clyvo.com", roles = "VET")
    void veterinarioDeveAcessarFilaClinica() throws Exception {
        mockMvc.perform(get("/atendimentos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("A documentação OpenAPI permanece pública")
    void documentacaoOpenApiDeveSerPublica() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("As rotas REST consumidas pelo aplicativo móvel permanecem acessíveis")
    void rotasRestDevemPermanecerAcessiveis() throws Exception {
        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isOk());
    }
}
