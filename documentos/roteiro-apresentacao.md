# Roteiro de Apresentação em Vídeo — Clyvo VET (Sprint 4)

**Duração máxima permitida:** 15 minutos (meta recomendada: 9 a 11 minutos).  
**Requisito de comunicação em equipe:** Todos os 3 integrantes participam com fala e demonstração técnica.

---

## Divisão de Tempo e Responsabilidades

| Bloco | Integrante | Tempo Estimado | Foco Principal |
| :--- | :--- | :--- | :--- |
| **Bloco 1** | **Leandro Guarido de Oliveira** (RM 561760) | 00:00 – 03:30 | Narrativa da solução, arquitetura Spring Boot 3.2, segurança (RBAC), banco de dados Flyway/H2 e deploy online no Render |
| **Bloco 2** | **Gabriel Costa Solano** (RM 562325) | 03:30 – 07:00 | Demonstração funcional na URL em nuvem: visão do Tutor, triagem clínica com validação de conflitos e API REST / Swagger |
| **Bloco 3** | **Kaiky Pereira Rodrigues Da Silva** (RM 564578) | 07:00 – 10:30 | Demonstração funcional na URL em nuvem: visão do Veterinário, execução de consulta, atualização de prontuário e integração multidisciplinar (Mobile + Banco de Dados + QA) |

---

## Passo a Passo da Gravação

### Bloco 1 — Narrativa da Solução, Arquitetura e Deploy (Leandro — RM 561760)
1. **Abertura e Problema Real (00:00 – 01:00):**
   - Apresentar o grupo (Gabriel, Kaiky e Leandro) e contextualizar o desafio da **Clyvo VET**: clínicas veterinárias e tutores sofrem com prontuários fragmentados, perda de histórico vacinal e conflitos de agenda (double-booking).
   - Explicar a proposta de valor: plataforma web e API REST unificada que conecta a rotina clínica do médico veterinário ao acompanhamento preventivo contínuo do tutor.
2. **Decisões Arquiteturais e Stack (01:00 – 02:15):**
   - Mostrar o repositório no GitHub (`Sprint-4-java`), destacando a organização em camadas (`controller`, `controller/web`, `service`, `repository`, `entity`, `dto`, `config`), o uso de Git Flow (`main`, `develop`, feature branches) e a suíte com 26 testes automatizados.
   - Justificar as escolhas técnicas: **Java 17 + Spring Boot 3.2.5**, **Spring Security 6** (autenticação form-login e RBAC para `ROLE_VET`, `ROLE_TUTOR` e `ROLE_ADMIN`), **Flyway** (`V1`, `V2`, `V3`) com validação estrita de schema (`ddl-auto=validate`).
3. **Deploy em Nuvem no Render (02:15 – 03:30):**
   - Mostrar o `Dockerfile` multi-stage, o `render.yaml` e a aplicação rodando online na URL pública do Render (`https://...`), destacando o isolamento de segurança do perfil `prod` (console H2 desativado em produção).

---

### Bloco 2 — Fluxo do Tutor, Triagem Inteligente e Contrato OpenAPI (Gabriel — RM 562325)
1. **Login e Visão do Tutor (03:30 – 04:45):**
   - Na URL publicada no Render, autenticar como **Tutor** (`tutor@clyvo.com` / `tutor123`).
   - Mostrar o painel adaptado ao perfil do tutor (listando apenas seus próprios animais e agendamentos) e tentar acessar `/atendimentos` para evidenciar o bloqueio **HTTP 403 (Acesso Negado)** do Spring Security.
2. **Motor de Triagem e Prevenção de Conflitos (04:45 – 06:00):**
   - Acessar **Triagem & Agendar** (`/agendamentos/novo`).
   - Demonstrar a validação de regra de negócio tentando agendar fora do horário comercial ou em horário conflitante, exibindo o feedback claro na interface.
   - Realizar um agendamento válido com classificação de triagem e recomendação de jejum, confirmando a criação na lista de agendamentos.
3. **Contrato da API REST no Swagger UI (06:00 – 07:00):**
   - Abrir `/swagger-ui.html` na URL em nuvem e demonstrar os endpoints REST (`/api/pets`, `/api/agendamentos`, `/api/protocolos`, `/api/eventos-saude`) que alimentam o aplicativo móvel.

---

### Bloco 3 — Fluxo Clínico Veterinário e Integração Multidisciplinar (Kaiky — RM 564578)
1. **Fila Clínica e Execução de Consulta (07:00 – 08:45):**
   - Sair do perfil Tutor e entrar como **Médica Veterinária** (`veterinario@clyvo.com` / `admin123`).
   - Acessar a **Fila Clínica** (`/atendimentos`), clicar em **Iniciar Consulta** no agendamento recém-criado.
   - Preencher o formulário de atendimento clínico: atualizar o peso aferido do animal, registrar a anamnese/conduta, prescrição, marcar a atualização do protocolo preventivo e concluir o atendimento.
2. **Prontuário Unificado (08:45 – 09:30):**
   - Mostrar o redirecionamento automático para o **Prontuário Clínico** (`/pets/{id}/prontuario`), comprovando que o peso do pet foi atualizado, o agendamento mudou para `CONCLUIDO` e o novo `EventoSaude` entrou na linha do tempo junto aos protocolos preventivos da espécie.
3. **Integração Multidisciplinar e Encerramento (09:30 – 10:30):**
   - Demonstrar a conexão direta com as demais disciplinas do semestre:
     - **Mobile Application Development:** o aplicativo **Clyvo Connect** (React Native / Expo 57) consumindo a mesma API REST `/api/**` e disparando notificações locais a partir das regras de agendamento do backend.
     - **Mastering Relational and Non-Relational Database:** modelagem relacional normalizada nas migrations `V1__create_domain_tables.sql` e `V2__create_security_tables.sql`.
     - **DevOps Tools & Cloud Computing / QA:** containerização Docker multi-stage, deploy contínuo no Render e testes automatizados com JUnit 5 e MockMvc.
   - Encerrar agradecendo à banca.

---

## Checklist Final Antes de Gravar
- [ ] Aplicação no Render aberta e aquecida no navegador antes de iniciar a gravação.
- [ ] Aba com `/swagger-ui.html` e aba do repositório GitHub abertas para transição fluida.
- [ ] Áudio e participação dos 3 integrantes testados.
- [ ] Duração final do vídeo estritamente abaixo de 15 minutos.
