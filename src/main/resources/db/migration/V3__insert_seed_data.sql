-- ============================================================================
-- V3__insert_seed_data.sql
-- Clyvo VET — Carga inicial de dados de dominio para testes e homologacao
-- ============================================================================

-- Clinicas
INSERT INTO t_clyvo_clinica (nm_clinica, nr_cnpj, ds_endereco, nr_telefone, ds_email)
VALUES ('Clyvo VET - Matriz Jardins', '12.345.678/0001-90', 'Av. Paulista, 1106, Bela Vista, SP', '(11) 3100-2000', 'contato@clyvovet.com.br');

INSERT INTO t_clyvo_clinica (nm_clinica, nr_cnpj, ds_endereco, nr_telefone, ds_email)
VALUES ('Clyvo VET - Unidade Pinheiros', '98.765.432/0001-10', 'Rua dos Pinheiros, 450, Pinheiros, SP', '(11) 3200-4000', 'pinheiros@clyvovet.com.br');

-- Veterinarios
INSERT INTO t_clyvo_veterinario (nm_veterinario, nr_crmv, ds_especialidade, nr_telefone, id_clinica)
VALUES ('Dra. Camila Rocha', 'CRMV-SP 12345', 'Clínica Geral e Dermatologia', '(11) 98765-4321', 1);

INSERT INTO t_clyvo_veterinario (nm_veterinario, nr_crmv, ds_especialidade, nr_telefone, id_clinica)
VALUES ('Dr. Rafael Mendes', 'CRMV-SP 67890', 'Ortopedia e Cirurgia Geral', '(11) 97654-3210', 1);

-- Tutores
INSERT INTO t_clyvo_tutor (nm_tutor, nr_cpf, ds_email, nr_telefone, ds_endereco)
VALUES ('Leandro Silva', '123.456.789-00', 'tutor@clyvo.com', '(11) 91234-5678', 'Rua Augusta, 1500, Apto 42, Sao Paulo - SP');

INSERT INTO t_clyvo_tutor (nm_tutor, nr_cpf, ds_email, nr_telefone, ds_endereco)
VALUES ('Mariana Costa', '234.567.890-11', 'mariana@email.com', '(11) 92345-6789', 'Rua da Consolacao, 800, Sao Paulo - SP');

-- Pets
INSERT INTO t_clyvo_pet (nm_pet, ds_especie, ds_raca, dt_nascimento, nr_peso, id_tutor)
VALUES ('Thor', 'Canino', 'Golden Retriever', '2021-06-15', 32.5, 1);

INSERT INTO t_clyvo_pet (nm_pet, ds_especie, ds_raca, dt_nascimento, nr_peso, id_tutor)
VALUES ('Luna', 'Felino', 'Siamês', '2022-03-20', 4.2, 1);

INSERT INTO t_clyvo_pet (nm_pet, ds_especie, ds_raca, dt_nascimento, nr_peso, id_tutor)
VALUES ('Pipoca', 'Canino', 'Poodle Toy', '2023-01-10', 5.8, 2);

-- Protocolos Preventivos
INSERT INTO t_clyvo_protocolo (ds_especie, ds_raca, ds_tipo_protocolo, ds_descricao, nr_intervalo_dias, ds_faixa_etaria)
VALUES ('Canino', 'Todas', 'Vacina V10', 'Vacina polivalente canina com reforço anual obrigatório contra cinomose, parvovirose e leptospirose.', 365, 'Adulto');

INSERT INTO t_clyvo_protocolo (ds_especie, ds_raca, ds_tipo_protocolo, ds_descricao, nr_intervalo_dias, ds_faixa_etaria)
VALUES ('Canino', 'Todas', 'Vermifugação', 'Administração preventiva de antiparasitário de amplo espectro a cada trimestre.', 90, 'Todas');

INSERT INTO t_clyvo_protocolo (ds_especie, ds_raca, ds_tipo_protocolo, ds_descricao, nr_intervalo_dias, ds_faixa_etaria)
VALUES ('Felino', 'Todas', 'Vacina V4', 'Imunização felina contra panleucopenia, calicivirose, rinotraqueíte e clamidiose.', 365, 'Adulto');

-- Agendamentos Iniciais
INSERT INTO t_clyvo_agendamento (dt_agendamento, ds_tipo, ds_status, ds_observacao, id_pet, id_veterinario)
VALUES (TIMESTAMP '2026-09-15 10:00:00', 'Consulta', 'AGENDADO', 'Check-up geral anual e avaliação de rotina.', 1, 1);

INSERT INTO t_clyvo_agendamento (dt_agendamento, ds_tipo, ds_status, ds_observacao, id_pet, id_veterinario)
VALUES (TIMESTAMP '2026-09-15 14:30:00', 'Vacina', 'CONFIRMADO', 'Dose de reforço anual de V10.', 1, 1);

INSERT INTO t_clyvo_agendamento (dt_agendamento, ds_tipo, ds_status, ds_observacao, id_pet, id_veterinario)
VALUES (TIMESTAMP '2026-09-16 09:00:00', 'Exame', 'AGENDADO', 'Coleta de sangue em jejum de 8 horas.', 2, 2);

-- Eventos de Saude
INSERT INTO t_clyvo_evento_saude (ds_tipo_evento, dt_evento, ds_descricao, ds_resultado, id_pet, id_veterinario)
VALUES ('Consulta', '2026-03-10', 'Consulta de rotina com aferição de sinais vitais.', 'Animal saudável, peso adequado de 32kg.', 1, 1);

INSERT INTO t_clyvo_evento_saude (ds_tipo_evento, dt_evento, ds_descricao, ds_resultado, id_pet, id_veterinario)
VALUES ('Vacina', '2025-09-12', 'Aplicação de vacina antirrábica com lote VET-9821.', 'Procedimento realizado com sucesso, sem reações adversas.', 1, 1);
