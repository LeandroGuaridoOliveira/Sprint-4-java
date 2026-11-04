# Roteiro de Gravação — Clyvo VET (Sprint 4)

**Duração máxima permitida:** 15 minutos. **Meta deste roteiro:** 14 minutos.
**URL de produção:** https://clyvo-vet.onrender.com

## Estado da gravação

| Bloco | Conteúdo | Situação |
| :--- | :--- | :--- |
| 1 | Abertura | ✅ gravado |
| 2 | Problema e proposta | ✅ gravado |
| 3 | Código na IDE | ✅ gravado |
| 4 | Perfil do tutor e RBAC (áudio do Gabriel) | ✅ gravado |
| 5 | Regras de negócio rodando | ✅ gravado |
| 6 | API e CRUD no Swagger | ✅ gravado |
| **7** | **Fluxo clínico do veterinário (áudio do Kaiky)** | ⬜ **pendente** |
| **7B** | **Supervisão administrativa** | ⬜ **pendente — bloco novo** |
| **8** | **Integração com as outras disciplinas** | ⬜ **pendente** |
| **9** | **Encerramento** | ⬜ **pendente** |

### O que mudou depois que você gravou o Bloco 6

O perfil **ADMIN** passou a ter acesso de supervisão clínica. Antes ele via o menu "Triagem & Agendar" e tomava 403 ao clicar — a interface oferecia o que o perfil não podia fazer. Agora as permissões estão alinhadas, e isso virou o **Bloco 7B**, que não existia antes.

**O material já gravado continua válido.** Verifiquei cada caso:

- **Bloco 4 (tutor):** o comportamento do TUTOR não mudou em nada. Ele continua recebendo 403 em `/atendimentos` e continua sem ver "Fila Clínica" no menu. A fala do Gabriel segue correta.
- **Bloco 5 e 6:** nada nesses fluxos foi tocado.
- **Bloco 3 (IDE):** aqui há uma diferença cosmética. Você filmou o `SecurityConfig` quando as linhas 66 e 69 diziam `hasRole("VET")` e `hasAnyRole("TUTOR","VET")`. Hoje elas incluem `ADMIN`. A estrutura e os números de linha são os mesmos, só as listas de perfis cresceram. **Não vale regravar** — o Bloco 7B explica a regra de supervisão em voz alta, o que cobre a diferença naturalmente.

---

## Como este roteiro foi montado

A gravação de tela é feita integralmente por **Leandro Guarido de Oliveira (RM 561760)**, que também conduz a maior parte da narração.

**Gabriel (RM 562325)** e **Kaiky (RM 564578)** gravam apenas o áudio dos blocos marcados com o nome deles. As falas estão escritas por extenso justamente para que possam ser lidas sem acompanhar a tela — basta gravar o áudio e enviar para o Leandro sincronizar na edição.

| Narrador | Blocos | Tempo aproximado |
| :--- | :--- | ---: |
| Leandro (RM 561760) | 1, 2, 3, 5, 6, 7B, 8, 9 | ~10 min |
| Gabriel (RM 562325) | 4 | ~1 min 30 s |
| Kaiky (RM 564578) | 7 | ~1 min 40 s |

---

## Antes de apertar REC

1. Abrir https://clyvo-vet.onrender.com/login no navegador e esperar a página carregar por completo. O plano gratuito do Render hiberna por inatividade e a primeira requisição leva cerca de 50 segundos. **Sem esse aquecimento o vídeo começa com quase um minuto de tela branca.**
2. Abrir a IDE com o projeto `Sprint4-java` já carregado e indexado, e **deixar previamente abertas, em abas, na ordem do Bloco 3**:
   - `TriagemAgendamentoService.java`
   - `ExecucaoConsultaService.java`
   - `ContextoAutenticacaoWeb.java`
   - `SecurityConfig.java`
   - `TriagemAgendamentoServiceTest.java`
   - `V1__create_domain_tables.sql`
   - `Dockerfile`
3. Na IDE: aumentar a fonte do editor (Ctrl + Scroll) até o código ficar legível em tela cheia, ativar a numeração de linhas e fechar os painéis laterais que não forem usados. Tema escuro grava melhor.
4. Deixar duas abas do navegador abertas e já carregadas: o portal (`/login`) e `https://clyvo-vet.onrender.com/swagger-ui.html`.
5. Fechar abas pessoais, notificações do sistema e qualquer extensão que gere pop-up.
6. Gravar em 1920x1080.
7. Ter o aplicativo **Clyvo Connect** aberto no celular, pronto para o Bloco 8.

---

> Os Blocos 1 a 6 já estão gravados. Ficam aqui como referência para a edição e para conferir continuidade. **Pule direto para o Bloco 7.**

---

# BLOCO 1 — Abertura ✅ gravado
**Narração: Leandro** · **Tempo: 00:00 – 00:40** · **Tela: portal em `/login`**

**Ação:** deixar a tela de login parada, sem clicar em nada.

**Fala:**

> "Olá, professor. Somos o grupo da turma 2TDSPV e este é o Clyvo VET, nosso projeto de Java Advanced da Sprint 4. Eu sou o Leandro Guarido de Oliveira, RM 561760, e comigo estão o Gabriel Costa Solano, RM 562325, e o Kaiky Pereira Rodrigues da Silva, RM 564578. Nos próximos minutos vamos mostrar o código da solução, a aplicação rodando em nuvem e como ela se conecta com as outras disciplinas do semestre."

---

# BLOCO 2 — O problema e a proposta ✅ gravado
**Narração: Leandro** · **Tempo: 00:40 – 02:00** · **Tela: portal em `/login`**

**Ação:** continuar na tela de login. Por volta dos 90 segundos, fazer login como **`veterinario@clyvo.com` / `admin123`** e deixar o dashboard aberto enquanto termina a fala.

**Fala:**

> "A Clyvo VET nasce de um problema concreto das clínicas veterinárias: o histórico do animal fica espalhado. A carteira de vacinação está no papel, a agenda está num caderno ou numa planilha, e o tutor não tem visibilidade nenhuma do que já foi feito. O resultado são vacinas atrasadas, consultas remarcadas e, com frequência, duas consultas marcadas para o mesmo veterinário no mesmo horário.
>
> Nossa proposta é unificar isso em uma plataforma só. Um portal web para a rotina da clínica e uma API REST que alimenta o aplicativo do tutor. O mesmo dado, uma fonte só da verdade, acessível pelos dois lados.
>
> Aqui já estamos entrando como médica veterinária, e este é o dashboard adaptado ao perfil dela. Antes de navegar pelos fluxos, vou abrir o código para mostrar como isso foi construído."

---

# BLOCO 3 — O código na IDE ✅ gravado
**Narração: Leandro** · **Tempo: 02:00 – 04:30** · **Tela: IDE**

Este é o bloco que sustenta os 20 pontos de Narrativa da Solução. A ideia é mostrar **pouco código, mas o código certo**, sempre explicando a decisão de projeto por trás dele. Não leia o código linha por linha: aponte o trecho e explique por que ele existe.

### 3.1 — Estrutura de pacotes (~20 s)

**Ação:** na árvore do projeto, expandir `src/main/java/br/com/fiap/clyvovet/` e passar o mouse pelas pastas `controller`, `controller/web`, `service`, `repository`, `entity`, `dto`, `config` e `exception`.

**Fala:**

> "A organização em camadas é a base do projeto. Os controllers de `controller` expõem a API REST e os de `controller/web` servem o portal Thymeleaf — mas nenhum dos dois contém regra de negócio. Tudo que é decisão fica em `service`, o acesso a dados fica em `repository`, e os `dto` existem para que as entidades JPA nunca sejam expostas diretamente na API."

### 3.2 — `TriagemAgendamentoService.java` — o coração da solução (~50 s)

**Ações:**
1. Abrir a classe.
2. Apontar o **construtor (linhas 27 a 35)**, destacando o `Clock clock` recebido por injeção.
3. Descer até o bloco de **validações (linhas 54 a 69)** e passar devagar pelas quatro regras.
4. Apontar as **linhas 72 e 73**, onde a janela de conflito é calculada.
5. Apontar as **linhas 75 a 85**, com os dois testes de conflito.

**Fala:**

> "Esta é a classe mais importante do backend. Ela recebe uma solicitação de agendamento e decide se aquilo é possível na operação de uma clínica real.
>
> Repare no construtor: além dos repositories, a classe recebe um `Clock`. Em vez de chamar `LocalDateTime.now` direto no meio da regra, a gente injeta o relógio. Isso parece detalhe, mas é o que torna a validação temporal testável — nos testes a gente congela o relógio numa data fixa e o resultado é sempre o mesmo.
>
> Aqui está a cascata de validações: a data tem que ser futura, domingo é bloqueado porque a clínica não faz atendimento eletivo, o expediente é das oito às dezoito, e existe uma antecedência mínima de uma hora para preparar a sala.
>
> E esta é a parte que resolve o problema de agenda que eu mencionei: a gente monta uma janela de vinte e nove minutos para trás e para frente do horário pedido, e consulta o banco duas vezes. Uma para ver se a veterinária já tem alguém nesse intervalo, outra para ver se o próprio animal já está marcado em outro lugar. É assim que o double-booking é impedido na origem, e não na recepção."

### 3.3 — `ExecucaoConsultaService.java` — transação (~30 s)

**Ações:**
1. Abrir a classe.
2. Apontar a anotação **`@Transactional` na linha 51**.
3. Rolar pelos comentários **PASSO A, PASSO B e PASSO C (linhas 75, 79 e 101)**.

**Fala:**

> "Quando a veterinária conclui um atendimento, três coisas precisam acontecer juntas: o peso do animal é atualizado, um evento de saúde é criado no histórico e o agendamento muda para concluído.
>
> O `@Transactional` aqui não é decoração. Se a criação do histórico falhasse depois do peso já ter sido alterado, a gente terminaria com um animal pesado mas sem registro de consulta — um dado órfão no prontuário. Com a transação, ou os três passos acontecem, ou nenhum acontece."

### 3.4 — `ContextoAutenticacaoWeb.java` — refatoração desta sprint (~20 s)

**Ação:** abrir a classe e mostrar os três métodos.

**Fala:**

> "Esta classe é nova nesta sprint e nasceu de uma refatoração. Os controllers do portal repetiam a mesma lógica para descobrir quem era o usuário logado e qual o perfil dele. A gente extraiu isso para um componente único, e os controllers passaram a só perguntar. Menos código duplicado e um lugar só para alterar se a regra de acesso mudar."

### 3.5 — `SecurityConfig.java` — RBAC (~20 s)

**Ação:** abrir a classe e apontar o bloco de `requestMatchers`, **linhas 54 a 72**.

**Fala:**

> "Aqui está o controle de acesso. Cada rota declara qual perfil pode entrar: a área clínica exige `ROLE_VET`, o agendamento aceita tutor ou veterinário, e os endpoints da API ficam liberados porque são consumidos pelo aplicativo. O ponto importante é que isso é validado no servidor, a cada requisição — não é interface escondendo botão."

### 3.6 — `TriagemAgendamentoServiceTest.java` — testes (~20 s)

**Ações:**
1. Abrir a classe de teste.
2. Apontar a **linha 61**, onde o `Clock.fixed` é criado.
3. Rolar pela lista de métodos `@Test` mostrando os nomes.
4. Rodar a suíte completa e mostrar o resultado verde.

**Fala:**

> "E é aqui que o `Clock` injetado se paga. Nesta linha a gente congela o tempo numa data fixa, então o teste que verifica o bloqueio de domingo vale hoje e vale no ano que vem.
>
> Os nomes dos testes descrevem as regras: recusar data passada, recusar domingo, recusar horário após o expediente, aceitar o limite inferior do expediente. São vinte e seis testes no total, entre validação de triagem, execução de consulta e controle de acesso."

### 3.7 — Banco e deploy (~20 s)

**Ações:**
1. Abrir `src/main/resources/db/migration` e mostrar os arquivos `V1`, `V2` e `V3`.
2. Abrir o `Dockerfile` e mostrar os dois estágios.

**Fala:**

> "O banco é versionado em migrations Flyway: a V1 cria as tabelas de domínio, a V2 as tabelas de segurança e a V3 insere a carga inicial. E o Hibernate está configurado para validar o schema, não para gerá-lo — o banco é a fonte da verdade, não o código.
>
> Por último, o Dockerfile multi-stage: um estágio compila com Maven e o outro carrega só o JAR numa imagem enxuta. É essa imagem que está rodando no Render, e é de lá que vem tudo o que vamos navegar agora."

---

# BLOCO 4 — Perfil do tutor e controle de acesso ✅ gravado
**Narração: GABRIEL (RM 562325)** · **Tempo: 04:30 – 06:00**

**Ações que o Leandro grava enquanto a fala do Gabriel roda por cima:**
1. Voltar ao navegador e sair do perfil da veterinária (logout).
2. Entrar como **`tutor@clyvo.com` / `tutor123`**.
3. Mostrar o dashboard do tutor.
4. Clicar em **Meus Pets** (`/pets`) e mostrar que aparecem apenas o **Thor** e a **Luna** — os animais do tutor Leandro Silva. O **Pipoca**, que pertence a outra tutora, não aparece.
5. Digitar `/atendimentos` na barra de endereço e dar Enter.
6. Deixar a tela de **403 — Acesso Negado** visível por alguns segundos.

**Fala do Gabriel (ler na gravação de áudio):**

> "Agora entramos no sistema como tutor. Repare que o painel muda completamente em relação ao da veterinária: o tutor vê apenas o que é dele.
>
> Na listagem de pets aparecem só o Thor e a Luna, que são os animais deste tutor. Os animais de outros tutores simplesmente não existem para ele — e isso não é um filtro aplicado na tela, é uma restrição aplicada na camada de serviço, antes dos dados chegarem ao navegador.
>
> Para provar que o controle de acesso é real, vamos tentar acessar a fila clínica digitando a rota direto na barra de endereço. Essa é uma área exclusiva do veterinário.
>
> O Spring Security intercepta a requisição e devolve 403, Acesso Negado. Ou seja, mesmo conhecendo a URL, um usuário sem o perfil correto não passa. O controle é feito por role no servidor, com ROLE_VET, ROLE_TUTOR e ROLE_ADMIN, e não por esconder botões na interface."

---

# BLOCO 5 — As regras de negócio rodando ✅ gravado
**Narração: Leandro** · **Tempo: 06:00 – 07:50** · **Tela: `/agendamentos/novo`, ainda como tutor**

Aqui a gente fecha o ciclo: as validações que apareceram no código do Bloco 3 agora são provocadas de propósito na interface. Deixe cada mensagem visível na tela por uns 3 segundos.

**Ações, nesta ordem:**
1. Acessar **Triagem & Agendar** (`/agendamentos/novo`).
2. **Erro 1 — horário fora do expediente:** selecionar o pet Thor, escolher uma data futura e marcar **21:00**. Enviar.
   → Aparece: *"O expediente clínico funciona exclusivamente das 08:00 às 18:00."*
3. **Erro 2 — domingo:** trocar a data para o próximo domingo, às 10:00. Enviar.
   → Aparece: *"A clínica não realiza atendimentos eletivos aos domingos. Selecione um dia de segunda a sábado."*
4. **Erro 3 — data no passado:** colocar uma data de ontem. Enviar.
   → Aparece: *"A data do agendamento deve ser uma data futura."*
5. **Agendamento válido:** pet **Thor**, veterinária **Dra. Camila Rocha**, um dia útil futuro às **14:00**, marcar a opção de **jejum recomendado** e descrever o motivo. Enviar.
6. Mostrar o agendamento criado na lista, com o status e a observação de jejum.

**Fala:**

> "Agora vamos ver aquelas validações da classe de triagem funcionando de verdade.
>
> Vou tentar marcar às nove da noite. O sistema recusa: é exatamente a regra do expediente que a gente viu no código.
>
> Agora um domingo. Recusado também.
>
> E uma data que já passou. Também é barrado.
>
> Vale reforçar: nenhuma dessas mensagens vem de validação de JavaScript na tela. Elas sobem da camada de serviço. Mesmo que alguém chame a API direto, sem passar pelo navegador, as regras continuam valendo.
>
> Agora um agendamento válido: o Thor, com a Dra. Camila, numa terça às duas da tarde, com jejum prévio recomendado. Aqui entra a verificação de conflito que mostrei no código — o sistema checa a janela de trinta minutos na agenda da veterinária e na do próprio animal. Não havendo choque, o agendamento é confirmado com a recomendação de jejum de oito horas registrada na observação."

---

# BLOCO 6 — O contrato da API e o CRUD completo ✅ gravado
**Narração: Leandro** · **Tempo: 07:50 – 09:30** · **Tela: aba do Swagger**

> ⚠️ **Regra absoluta deste bloco: nunca use DELETE no Thor (id 1), na Luna (id 2) nem no Pipoca (id 3).**
> O Thor é usado nos Blocos 7 e 8. Apagá-lo inutiliza o resto da gravação. O ciclo CRUD é feito num animal descartável, criado e removido dentro do próprio bloco.

### 6.1 — Visão geral do contrato (~20 s)

**Ações:**
1. Abrir a aba do Swagger (`/swagger-ui.html`).
2. Mostrar a lista de controllers, recolhida, para dar noção do tamanho da API.

**Fala:**

> "Toda a API REST está documentada com OpenAPI, publicada junto com a aplicação. São sete recursos expostos: pets, agendamentos, tutores, veterinários, clínicas, protocolos preventivos e eventos de saúde. Esses são exatamente os endpoints que o nosso aplicativo mobile consome — não existe backend separado nem dado duplicado."

### 6.2 — Ciclo CRUD completo no recurso Pets (~1 min 20 s)

Executar as chamadas abaixo na ordem, usando **Try it out → Execute** em cada uma. Os retornos foram verificados contra o servidor em produção e são exatamente estes.

| # | Operação | Corpo / parâmetro | Resposta esperada |
| :-- | :--- | :--- | :--- |
| 1 | `GET /api/pets` | todos os campos vazios | **200** · 3 pets |
| 1b | `GET /api/pets` | `sort` = `string` | **400** · `Propriedade de ordenação inválida: 'string'` |
| 2 | `POST /api/pets` | sem o campo `nome` | **400** · `{"nome":"Nome do pet é obrigatório"}` |
| 3 | `POST /api/pets` | corpo completo (abaixo) | **201** · devolve o `id` criado |
| 4 | `PUT /api/pets/{id}` | mesmo corpo com `peso: 12.5` | **200** · peso atualizado |
| 5 | `DELETE /api/pets/{id}` | o `id` criado no passo 3 | **204** · sem conteúdo |

Corpo para o passo 3 (o passo 2 é este mesmo, só que sem a linha do `nome`):

```json
{
  "nome": "Mel",
  "especie": "Canino",
  "raca": "SRD",
  "dataNascimento": "2024-01-10",
  "peso": 10.0,
  "tutorId": 1
}
```

No passo 4, copiar o `id` devolvido no passo 3 e trocar apenas o `peso` para `12.5`.

**Fala (narrar por cima das execuções):**

> "Vou percorrer o ciclo completo de CRUD aqui no Swagger, contra o servidor em produção, para mostrar que a API está realmente funcional.
>
> Começando pela listagem: duzentos, três pets cadastrados. A resposta vem paginada, no formato padrão do Spring, com o conteúdo e os metadados de paginação.
>
> Vou aproveitar para mostrar o tratamento de erro da paginação. Se eu pedir ordenação por um campo que não existe na entidade, a API responde quatrocentos dizendo exatamente qual propriedade é inválida — e não um erro interno genérico. Erro de entrada do cliente é problema do cliente, e o código HTTP tem que refletir isso.
>
> Agora vou tentar cadastrar um animal sem o nome, de propósito. O retorno é quatrocentos, e repare que a resposta não é um erro genérico: ela diz exatamente qual campo falhou e por quê. Isso vem das anotações de validação no DTO, tratadas por um handler global de exceções.
>
> Corrigindo e enviando o cadastro completo: duzentos e um, criado, e a API devolve o registro já com o identificador gerado.
>
> Atualizando o peso desse mesmo animal com o PUT: duzentos, e o novo peso já volta na resposta.
>
> E por fim o DELETE: duzentos e quatro, sem conteúdo, que é a resposta correta para uma remoção bem-sucedida.
>
> Criar, ler, atualizar e remover, com validação e com os códigos HTTP corretos em cada operação. Os outros seis recursos seguem exatamente o mesmo padrão."

### 6.3 — Fechamento do bloco (~10 s)

**Ação:** executar `GET /api/pets` de novo.

**Fala:**

> "Listando outra vez, voltamos aos três animais originais: o registro de teste foi removido e a base está consistente."

---

# ⬜ RETOME A GRAVAÇÃO AQUI

**Antes de voltar a gravar:**

1. Abrir https://clyvo-vet.onrender.com/login e esperar carregar — o Render free hiberna e a primeira requisição leva ~50 s.
2. **Fazer login de novo, com sessão fresca.** Se você deixou o navegador aberto desde a gravação anterior, a sessão pode ter expirado no meio do container reiniciar.
3. Conferir que o agendamento do **Thor** criado no Bloco 5 ainda está na fila. **Se o Render tiver hibernado, o banco em memória foi zerado e esse agendamento sumiu** — nesse caso, refaça rapidamente o agendamento válido do Bloco 5 (Thor, Dra. Camila, dia útil às 14:00, jejum marcado) sem gravar, só para recriar o estado. O Flyway e o `DataLoader` recriam os três animais e os três logins sozinhos, mas não o agendamento que você criou na gravação.
4. Manter o mesmo enquadramento, zoom e tema do navegador usados antes, para a edição não ficar com salto visual.

---

# BLOCO 7 — Fluxo clínico do veterinário
**Narração: KAIKY (RM 564578)** · **Tempo: 09:30 – 11:10**

**Ações que o Leandro grava enquanto a fala do Kaiky roda por cima:**
1. Fazer logout do tutor e entrar como **`veterinario@clyvo.com` / `admin123`**.
2. Acessar a **Fila Clínica** (`/atendimentos`) e mostrar o agendamento do Thor criado no Bloco 5.
3. Clicar em **Iniciar Consulta**.
4. No formulário, preencher:
   - Peso aferido: **33,2**
   - Conduta clínica: *"Animal ativo, mucosas normocoradas. Exame físico sem alterações."*
   - Prescrição: *"Vermífugo dose única. Retorno em 30 dias."*
   - Marcar **aplicar protocolo preventivo**.
5. Concluir o atendimento.
6. Na tela de prontuário para a qual o sistema redireciona, mostrar: o peso atualizado para 33,2 kg, o agendamento com status **CONCLUÍDO** e o novo evento na linha do tempo.

**Fala do Kaiky (ler na gravação de áudio):**

> "Voltamos ao sistema, agora como a médica veterinária. Na fila clínica aparece o agendamento do Thor que acabou de ser criado pelo tutor — o mesmo registro, visto do outro lado da operação.
>
> Ao iniciar a consulta, abrimos o formulário de atendimento. Registramos o peso aferido no momento, a conduta clínica, a prescrição, e marcamos a atualização do protocolo preventivo do animal.
>
> Quando concluímos, uma única operação transacional faz quatro coisas ao mesmo tempo: atualiza o peso no cadastro do pet, cria o evento de saúde no histórico médico, registra o reforço do protocolo preventivo e muda o status do agendamento para concluído. É aquele `@Transactional` que apareceu no código: se qualquer um desses passos falhasse, nada seria gravado.
>
> O sistema redireciona direto para o prontuário, e aqui está o resultado: o peso já aparece atualizado, o agendamento consta como concluído e o atendimento entrou na linha do tempo do animal. O histórico do Thor deixou de ser papel solto e virou registro rastreável."

---

# BLOCO 7B — Supervisão administrativa
**Narração: Leandro** · **Tempo: 11:10 – 11:55** · **Tela: portal, trocando para o perfil ADMIN**

Bloco novo, criado depois que o acesso de supervisão do ADMIN foi implementado. Fecha a demonstração do controle de acesso mostrando os **três** perfis, não só dois.

**Ações, nesta ordem:**
1. Sair do perfil da veterinária.
2. Entrar como **`admin@clyvo.com` / `admin123`**.
3. Apontar o badge **ADMIN** no canto superior direito.
4. Mostrar o menu: ele tem **Fila Clínica**, igual ao do veterinário — diferente do menu do tutor, que não tinha.
5. Abrir a **Fila Clínica** (`/atendimentos`) e mostrar que o atendimento do Thor concluído no Bloco 7 já aparece com status **CONCLUÍDO**.
6. Abrir **Pets & Prontuários** e mostrar que o admin enxerga **os três animais** — Thor, Luna e Pipoca — enquanto o tutor via apenas os dois dele.

**Fala:**

> "Para fechar o controle de acesso, falta o terceiro perfil. Este é o administrador, um papel de supervisão.
>
> Repare no menu: ele tem a Fila Clínica, como o veterinário. O do tutor não tinha — e isso não é a interface escondendo um botão por estética, é o menu refletindo exatamente o que o Spring Security autoriza para cada papel. Nenhum perfil vê aqui uma opção que vá recusar o acesso depois.
>
> Na fila clínica, o atendimento do Thor que acabamos de concluir já aparece como concluído. O administrador acompanha a operação inteira da clínica em tempo real.
>
> E a diferença mais clara está nos prontuários: o administrador enxerga os três animais cadastrados. O tutor, no bloco anterior, via apenas os dois que são dele. É o mesmo sistema, a mesma tela, com o escopo de dados recortado por perfil no servidor.
>
> São três papéis, três níveis de acesso, todos definidos de forma declarativa na configuração de segurança."

---

# BLOCO 8 — Integração com as outras disciplinas
**Narração: Leandro** · **Tempo: 11:55 – 13:15** · **Tela: celular com o app + prontuário no navegador**

**Ações:**
1. Com o prontuário ainda aberto no navegador, trazer a tela do celular para o vídeo.
2. No app **Clyvo Connect**, abrir a lista de pets e mostrar o **Thor com o peso 33,5 kg** — o valor que acabou de ser gravado pelo portal web.

> O peso correto aqui é **33,5 kg**, e não os 33,2 previstos no roteiro original. Foi esse o valor digitado na gravação do Bloco 7, confirmado consultando a API de produção. O número narrado no Bloco 8 precisa bater com o que o app exibe.
3. Alternar o tema claro/escuro no app.

**Fala:**

> "Este é o ponto que amarra o semestre inteiro. No celular está o Clyvo Connect, nosso aplicativo de Mobile Application Development, feito em React Native com Expo. Ele não tem banco próprio nem backend próprio: consome a mesma API REST que acabamos de ver no Swagger.
>
> Repare no peso do Thor: trinta e três vírgula cinco quilos. É o valor que a veterinária registrou no portal web há poucos segundos. O dado atravessou as duas disciplinas sem nenhuma sincronização manual.
>
> De Banco de Dados, a modelagem relacional está nas migrations Flyway que mostrei na IDE, com as tabelas de domínio e as de segurança separadas e versionadas.
>
> De DevOps e Cloud, é o Dockerfile multi-stage publicado no Render que está servindo tudo isto. E de Qualidade de Software, os vinte e seis testes automatizados garantem que as regras de triagem e de atendimento continuem válidas a cada alteração."

---

# BLOCO 9 — Encerramento
**Narração: Leandro** · **Tempo: 13:15 – 13:45** · **Tela: dashboard do portal**

**Ação:** voltar ao dashboard e deixar a tela parada.

**Fala:**

> "Para fechar: o Clyvo VET é uma aplicação Spring Boot em camadas, com segurança por perfil, regras de negócio validadas no servidor, banco versionado, testes automatizados e deploy em nuvem, servindo ao mesmo tempo um portal web e um aplicativo móvel.
>
> O repositório e a URL de produção estão no README. Agradecemos a atenção, professor."

---

## Checklist pós-gravação

- [ ] Áudio do Kaiky recebido e sincronizado sobre o Bloco 7 (o do Gabriel, no Bloco 4, já está gravado).
- [ ] A emenda entre o Bloco 6 (gravado antes) e o Bloco 7 não tem salto de enquadramento, zoom ou tema.
- [ ] No Bloco 7B, os três perfis ficaram evidentes: tutor restrito, veterinário clínico e administrador supervisor.
- [ ] Os três integrantes aparecem nominalmente no Bloco 1, com RM.
- [ ] No Bloco 3, o código ficou legível em tela cheia e a suíte de testes apareceu verde.
- [ ] As três mensagens de erro da triagem ficaram legíveis na tela no Bloco 5.
- [ ] No Bloco 6, o ciclo CRUD foi feito num animal descartável e o Thor, a Luna e o Pipoca permaneceram intactos.
- [ ] O peso 33,5 kg aparece tanto no prontuário web quanto no app.
- [ ] Duração final abaixo de 15 minutos.
- [ ] Nenhuma aba pessoal, notificação ou dado sensível visível em nenhum momento.
- [ ] Vídeo publicado no YouTube como **não listado** e o link adicionado ao README.
