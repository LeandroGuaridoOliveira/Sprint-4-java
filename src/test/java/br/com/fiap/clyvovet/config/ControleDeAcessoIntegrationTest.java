package br.com.fiap.clyvovet.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
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

    @Test
    @DisplayName("Ordenação inválida na API REST retorna 400 Bad Request amigável")
    void ordenacaoInvalidaDeveRetornar400() throws Exception {
        mockMvc.perform(get("/api/pets").param("sort", "string"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Requisição inválida"));
    }

    @Test
    @DisplayName("POST no logout mesmo sem CSRF ou com sessão expirada redireciona para login sem erro 500")
    void postLogoutSemCsrfRedirecionaParaLogin() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout=true"));
    }

    @Test
    @DisplayName("POST em /403 é aceito e responde com status 403 Forbidden")
    void post403DeveRetornarForbidden() throws Exception {
        mockMvc.perform(post("/403"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Método HTTP não suportado na API REST retorna 405 Method Not Allowed")
    void metodoNaoSuportadoDeveRetornar405() throws Exception {
        mockMvc.perform(patch("/api/pets"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }
}
