# Roteiro de Gravação — Clyvo VET (Sprint 4)

**Duração máxima permitida:** 15 minutos. **Meta deste roteiro:** 11 minutos.
**URL de produção:** https://clyvo-vet.onrender.com

## Como este roteiro foi montado

A gravação de tela é feita integralmente por **Leandro Guarido de Oliveira (RM 561760)**, que também conduz a maior parte da narração.

**Gabriel (RM 562325)** e **Kaiky (RM 564578)** gravam apenas o áudio dos blocos marcados com o nome deles. As falas estão escritas por extenso justamente para que possam ser lidas sem acompanhar a tela — basta gravar o áudio e enviar para o Leandro sincronizar na edição.

| Narrador | Blocos | Tempo aproximado |
| :--- | :--- | ---: |
| Leandro (RM 561760) | 1, 2, 3, 5, 6, 8, 9 | ~8 min |
| Gabriel (RM 562325) | 4 | ~1 min 30 s |
| Kaiky (RM 564578) | 7 | ~1 min 30 s |

---

## Antes de apertar REC

1. Abrir https://clyvo-vet.onrender.com/login no navegador e esperar a página carregar por completo. O plano gratuito do Render hiberna por inatividade e a primeira requisição leva cerca de 50 segundos. **Sem esse aquecimento o vídeo começa com quase um minuto de tela branca.**
2. Deixar três abas abertas e já carregadas, na ordem em que serão usadas:
   - Aba 1: o portal (`/login`)
   - Aba 2: o repositório `Sprint-4-java` no GitHub
   - Aba 3: `https://clyvo-vet.onrender.com/swagger-ui.html`
3. Fechar abas pessoais, notificações do sistema e qualquer extensão que gere pop-up.
4. Gravar em 1920x1080 e aumentar o zoom do navegador para 110% — o texto das tabelas fica legível na compactação do YouTube.
5. Ter o aplicativo **Clyvo Connect** aberto no celular, pronto para o Bloco 8.

---

# BLOCO 1 — Abertura
**Narração: Leandro** · **Tempo: 00:00 – 00:40** · **Tela: portal em `/login`**

**Ação:** deixar a tela de login parada, sem clicar em nada.

**Fala:**

> "Olá, professor. Somos o grupo da turma 2TDSPV e este é o Clyvo VET, nosso projeto de Java Advanced da Sprint 4. Eu sou o Leandro Guarido de Oliveira, RM 561760, e comigo estão o Gabriel Costa Solano, RM 562325, e o Kaiky Pereira Rodrigues da Silva, RM 564578. Nos próximos minutos vamos mostrar a aplicação rodando em nuvem, a arquitetura por trás dela e como ela se conecta com as outras disciplinas do semestre."

---

# BLOCO 2 — O problema e a proposta
**Narração: Leandro** · **Tempo: 00:40 – 02:00** · **Tela: portal em `/login`**

**Ação:** continuar na tela de login. Por volta dos 90 segundos, fazer login como **`veterinario@clyvo.com` / `admin123`** e deixar o dashboard aberto enquanto termina a fala.

**Fala:**

> "A Clyvo VET nasce de um problema concreto das clínicas veterinárias: o histórico do animal fica espalhado. A carteira de vacinação está no papel, a agenda está num caderno ou numa planilha, e o tutor não tem visibilidade nenhuma do que já foi feito. O resultado são vacinas atrasadas, consultas remarcadas e, com frequência, duas consultas marcadas para o mesmo veterinário no mesmo horário.
>
> Nossa proposta é unificar isso em uma plataforma só. Um portal web para a rotina da clínica e uma API REST que alimenta o aplicativo do tutor. O mesmo dado, uma fonte só da verdade, acessível pelos dois lados.
>
> Aqui já estamos entrando como médica veterinária, e este é o dashboard adaptado ao perfil dela."

---

# BLOCO 3 — Arquitetura e repositório
**Narração: Leandro** · **Tempo: 02:00 – 03:20** · **Tela: aba do GitHub**

**Ações, nesta ordem:**
1. Abrir a aba do GitHub no repositório `Sprint-4-java`.
2. Navegar até `src/main/java/br/com/fiap/clyvovet/` e mostrar as pastas `controller`, `controller/web`, `service`, `repository`, `entity`, `dto` e `config`.
3. Clicar na aba **Branches** e mostrar `main`, `develop` e as feature branches.
4. Abrir `src/test/java` e mostrar os arquivos de teste.

**Fala:**

> "O projeto está organizado em camadas. Os controllers só recebem requisição e devolvem resposta; toda a regra de negócio fica isolada na camada de service, e o acesso a dados fica nos repositories. Os DTOs existem justamente para que as entidades JPA nunca sejam expostas diretamente na API.
>
> O versionamento segue Git Flow: a `main` guarda as versões entregues, a `develop` integra o desenvolvimento e cada tarefa nasce na sua própria feature branch, com merge sem fast-forward para preservar o histórico. Os commits seguem o padrão Conventional Commits.
>
> Na stack usamos Java 17 com Spring Boot 3.2.5, Spring Security 6 para autenticação e controle de acesso por perfil, Flyway para versionar o banco e Thymeleaf no portal web. E temos 26 testes automatizados com JUnit 5 e MockMvc cobrindo a triagem de agendamento, a execução de consulta e as regras de permissão."

---

# BLOCO 4 — Perfil do tutor e controle de acesso
**Narração: GABRIEL (RM 562325)** · **Tempo: 03:20 – 04:50**

**Ações que o Leandro grava enquanto a fala do Gabriel roda por cima:**
1. Sair do perfil da veterinária (logout).
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

# BLOCO 5 — Regras de negócio da triagem
**Narração: Leandro** · **Tempo: 04:50 – 06:40** · **Tela: `/agendamentos/novo`, ainda como tutor**

Este é o bloco mais importante para a nota. Cada erro abaixo é uma regra implementada no `TriagemAgendamentoService`. Provoque os três erros **de propósito** e deixe cada mensagem visível na tela por uns 3 segundos.

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

> "Agora o ponto central do backend: a triagem. Em vez de aceitar qualquer agendamento e deixar o problema estourar na recepção, o sistema valida tudo no servidor.
>
> Vou tentar marcar às nove da noite. O sistema recusa: o expediente clínico vai das oito às dezoito horas.
>
> Agora um domingo. Recusado também, porque a clínica não faz atendimento eletivo aos domingos.
>
> E uma data que já passou. Também é barrado.
>
> Essas validações não estão no JavaScript da tela, estão na camada de serviço. Mesmo que alguém chame a API direto, sem passar pelo navegador, a regra continua valendo.
>
> Agora um agendamento válido: o Thor, com a Dra. Camila, numa terça às duas da tarde, com jejum prévio recomendado. Antes de gravar, o sistema ainda verifica se a veterinária já tem alguém marcado nesse horário e se o próprio pet já tem outra consulta no mesmo momento. Se houvesse conflito, ele diria exatamente qual é o choque. Não havendo, o agendamento entra na fila com a recomendação de jejum de oito horas registrada."

---

# BLOCO 6 — O contrato da API
**Narração: Leandro** · **Tempo: 06:40 – 07:30** · **Tela: aba do Swagger**

**Ações:**
1. Abrir a aba do Swagger (`/swagger-ui.html`).
2. Mostrar a lista de controllers.
3. Expandir **GET `/api/pets`**, clicar em **Try it out** e depois em **Execute**.
4. Mostrar o JSON de resposta com Thor, Luna e Pipoca.

**Fala:**

> "Toda a API REST está documentada com OpenAPI, publicada junto com a aplicação. São sete recursos expostos: pets, agendamentos, tutores, veterinários, clínicas, protocolos preventivos e eventos de saúde.
>
> Vou executar o GET de pets direto por aqui, contra o servidor em produção. A resposta vem paginada, no formato padrão do Spring, com o conteúdo e os metadados de paginação.
>
> Esses são exatamente os endpoints que o nosso aplicativo mobile consome. Não existe backend separado nem dado duplicado: é a mesma aplicação respondendo para o portal web e para o celular."

---

# BLOCO 7 — Fluxo clínico do veterinário
**Narração: KAIKY (RM 564578)** · **Tempo: 07:30 – 09:10**

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
> Quando concluímos, uma única operação transacional faz quatro coisas ao mesmo tempo: atualiza o peso no cadastro do pet, cria o evento de saúde no histórico médico, registra o reforço do protocolo preventivo e muda o status do agendamento para concluído. Se qualquer um desses passos falhasse, nada seria gravado.
>
> O sistema redireciona direto para o prontuário, e aqui está o resultado: o peso já aparece atualizado, o agendamento consta como concluído e o atendimento entrou na linha do tempo do animal. O histórico do Thor deixou de ser papel solto e virou registro rastreável."

---

# BLOCO 8 — Integração com as outras disciplinas
**Narração: Leandro** · **Tempo: 09:10 – 10:30** · **Tela: celular com o app + prontuário no navegador**

**Ações:**
1. Com o prontuário ainda aberto no navegador, trazer a tela do celular para o vídeo.
2. No app **Clyvo Connect**, abrir a lista de pets e mostrar o **Thor com o peso 33,2 kg** — o valor que acabou de ser gravado pelo portal web.
3. Alternar o tema claro/escuro no app.
4. Voltar ao navegador e abrir as migrations em `src/main/resources/db/migration` no GitHub.

**Fala:**

> "Este é o ponto que amarra o semestre inteiro. No celular está o Clyvo Connect, nosso aplicativo de Mobile Application Development, feito em React Native com Expo. Ele não tem banco próprio nem backend próprio: consome a mesma API REST que acabamos de ver no Swagger.
>
> Repare no peso do Thor: trinta e três vírgula dois quilos. É o valor que a veterinária registrou no portal web há poucos segundos. O dado atravessou as duas disciplinas sem nenhuma sincronização manual.
>
> De Banco de Dados, a modelagem relacional está versionada em migrations Flyway, com as tabelas de domínio e as tabelas de segurança separadas, e o Hibernate configurado para validar o schema em vez de gerá-lo — o banco é a fonte da verdade, não o código.
>
> De DevOps e Cloud, a aplicação está containerizada num Dockerfile multi-stage e publicada no Render, que é de onde vem tudo o que mostramos hoje. E de Qualidade de Software, os 26 testes automatizados garantem que as regras de triagem e de atendimento continuem válidas a cada alteração."

---

# BLOCO 9 — Encerramento
**Narração: Leandro** · **Tempo: 10:30 – 11:00** · **Tela: dashboard do portal**

**Ação:** voltar ao dashboard e deixar a tela parada.

**Fala:**

> "Para fechar: o Clyvo VET é uma aplicação Spring Boot em camadas, com segurança por perfil, regras de negócio validadas no servidor, banco versionado, testes automatizados e deploy em nuvem, servindo ao mesmo tempo um portal web e um aplicativo móvel.
>
> O repositório e a URL de produção estão no README. Agradecemos a atenção, professor."

---

## Checklist pós-gravação

- [ ] Áudios do Gabriel e do Kaiky recebidos e sincronizados sobre os Blocos 4 e 7.
- [ ] Os três integrantes aparecem nominalmente no Bloco 1, com RM.
- [ ] As três mensagens de erro da triagem ficaram legíveis na tela.
- [ ] O peso 33,2 kg aparece tanto no prontuário web quanto no app.
- [ ] Duração final abaixo de 15 minutos.
- [ ] Nenhuma aba pessoal, notificação ou dado sensível visível em nenhum momento.
- [ ] Vídeo publicado no YouTube como **não listado** e o link adicionado ao README.
