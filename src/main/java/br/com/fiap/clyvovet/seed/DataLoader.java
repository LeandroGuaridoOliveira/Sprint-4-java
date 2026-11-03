package br.com.fiap.clyvovet.seed;

import br.com.fiap.clyvovet.entity.*;
import br.com.fiap.clyvovet.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private final ClinicaRepository clinicaRepo;
    private final VeterinarioRepository vetRepo;
    private final TutorRepository tutorRepo;
    private final PetRepository petRepo;
    private final AgendamentoRepository agendamentoRepo;
    private final EventoSaudeRepository eventoRepo;
    private final ProtocoloRepository protocoloRepo;
    private final UsuarioRepository usuarioRepo;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public DataLoader(ClinicaRepository clinicaRepo, VeterinarioRepository vetRepo,
                      TutorRepository tutorRepo, PetRepository petRepo,
                      AgendamentoRepository agendamentoRepo, EventoSaudeRepository eventoRepo,
                      ProtocoloRepository protocoloRepo, UsuarioRepository usuarioRepo,
                      org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.clinicaRepo = clinicaRepo;
        this.vetRepo = vetRepo;
        this.tutorRepo = tutorRepo;
        this.petRepo = petRepo;
        this.agendamentoRepo = agendamentoRepo;
        this.eventoRepo = eventoRepo;
        this.protocoloRepo = protocoloRepo;
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Inicializa usuarios do Spring Security caso ainda nao existam
        if (usuarioRepo.count() == 0) {
            usuarioRepo.save(Usuario.builder()
                    .nome("Dra. Camila Rocha")
                    .email("veterinario@clyvo.com")
                    .senha(passwordEncoder.encode("admin123"))
                    .role("ROLE_VET")
                    .ativo(true)
                    .veterinarioId(1L)
                    .build());

            usuarioRepo.save(Usuario.builder()
                    .nome("Leandro Silva")
                    .email("tutor@clyvo.com")
                    .senha(passwordEncoder.encode("tutor123"))
                    .role("ROLE_TUTOR")
                    .ativo(true)
                    .tutorId(1L)
                    .build());

            usuarioRepo.save(Usuario.builder()
                    .nome("Administrador Clyvo VET")
                    .email("admin@clyvo.com")
                    .senha(passwordEncoder.encode("admin123"))
                    .role("ROLE_ADMIN")
                    .ativo(true)
                    .build());
        }

        if (clinicaRepo.count() > 0) return;

        var c1 = clinicaRepo.save(Clinica.builder().nome("PetLife Centro").cnpj("11.111.111/0001-01").endereco("Rua Augusta, 500 - SP").telefone("(11)3333-1111").email("contato@petlife.com.br").build());
        var c2 = clinicaRepo.save(Clinica.builder().nome("VetCare Moema").cnpj("22.222.222/0001-02").endereco("Av. Moema, 200 - SP").telefone("(11)3333-2222").email("moema@vetcare.com.br").build());
        var c3 = clinicaRepo.save(Clinica.builder().nome("Animal Plus Pinheiros").cnpj("33.333.333/0001-03").endereco("Rua dos Pinheiros, 800 - SP").telefone("(11)3333-3333").email("pinheiros@animalplus.com.br").build());

        var v1 = vetRepo.save(Veterinario.builder().nome("Dra. Ana Costa").crmv("SP-11111").especialidade("Clínica Geral").telefone("(11)99999-1111").clinica(c1).build());
        var v2 = vetRepo.save(Veterinario.builder().nome("Dr. Bruno Lima").crmv("SP-22222").especialidade("Ortopedia").telefone("(11)99999-2222").clinica(c1).build());
        var v3 = vetRepo.save(Veterinario.builder().nome("Dra. Carla Mendes").crmv("SP-33333").especialidade("Dermatologia").telefone("(11)99999-3333").clinica(c2).build());
        var v4 = vetRepo.save(Veterinario.builder().nome("Dr. Diego Rocha").crmv("SP-44444").especialidade("Cardiologia").telefone("(11)99999-4444").clinica(c2).build());
        var v5 = vetRepo.save(Veterinario.builder().nome("Dra. Elena Souza").crmv("SP-55555").especialidade("Oncologia").telefone("(11)99999-5555").clinica(c3).build());

        var t1 = tutorRepo.save(Tutor.builder().nome("Maria Silva").cpf("111.111.111-11").email("maria@email.com").telefone("(11)98888-1111").endereco("Rua A, 100 - SP").build());
        var t2 = tutorRepo.save(Tutor.builder().nome("João Santos").cpf("222.222.222-22").email("joao@email.com").telefone("(11)98888-2222").endereco("Rua B, 200 - SP").build());
        var t3 = tutorRepo.save(Tutor.builder().nome("Ana Oliveira").cpf("333.333.333-33").email("ana@email.com").telefone("(11)98888-3333").endereco("Rua C, 300 - SP").build());
        var t4 = tutorRepo.save(Tutor.builder().nome("Carlos Pereira").cpf("444.444.444-44").email("carlos@email.com").telefone("(11)98888-4444").endereco("Rua D, 400 - SP").build());
        var t5 = tutorRepo.save(Tutor.builder().nome("Fernanda Lima").cpf("555.555.555-55").email("fernanda@email.com").telefone("(11)98888-5555").endereco("Rua E, 500 - SP").build());

        var p1 = petRepo.save(Pet.builder().nome("Rex").especie("Canino").raca("Labrador").dataNascimento(LocalDate.of(2021,3,15)).peso(28.5).tutor(t1).build());
        var p2 = petRepo.save(Pet.builder().nome("Mia").especie("Felino").raca("Siamês").dataNascimento(LocalDate.of(2022,7,20)).peso(4.2).tutor(t1).build());
        var p3 = petRepo.save(Pet.builder().nome("Thor").especie("Canino").raca("Golden Retriever").dataNascimento(LocalDate.of(2020,1,10)).peso(32.0).tutor(t2).build());
        var p4 = petRepo.save(Pet.builder().nome("Luna").especie("Felino").raca("Persa").dataNascimento(LocalDate.of(2023,5,5)).peso(3.8).tutor(t3).build());
        var p5 = petRepo.save(Pet.builder().nome("Bob").especie("Canino").raca("Bulldog").dataNascimento(LocalDate.of(2019,11,25)).peso(22.0).tutor(t3).build());
        var p6 = petRepo.save(Pet.builder().nome("Nina").especie("Canino").raca("Poodle").dataNascimento(LocalDate.of(2022,2,14)).peso(8.5).tutor(t4).build());
        var p7 = petRepo.save(Pet.builder().nome("Simba").especie("Felino").raca("Maine Coon").dataNascimento(LocalDate.of(2021,9,1)).peso(7.0).tutor(t5).build());
        var p8 = petRepo.save(Pet.builder().nome("Mel").especie("Canino").raca("Shih Tzu").dataNascimento(LocalDate.of(2023,4,18)).peso(6.0).tutor(t5).build());

        agendamentoRepo.save(Agendamento.builder().dataAgendamento(LocalDateTime.of(2026,6,10,9,0)).tipo("Consulta").status("Agendado").observacao("Check-up semestral").pet(p1).veterinario(v1).build());
        agendamentoRepo.save(Agendamento.builder().dataAgendamento(LocalDateTime.of(2026,6,12,10,30)).tipo("Vacina").status("Agendado").observacao("V10 reforço anual").pet(p3).veterinario(v1).build());
        agendamentoRepo.save(Agendamento.builder().dataAgendamento(LocalDateTime.of(2026,6,15,14,0)).tipo("Exame").status("Agendado").observacao("Hemograma completo").pet(p5).veterinario(v3).build());
        agendamentoRepo.save(Agendamento.builder().dataAgendamento(LocalDateTime.of(2026,6,18,11,0)).tipo("Cirurgia").status("Agendado").observacao("Castração").pet(p6).veterinario(v2).build());
        agendamentoRepo.save(Agendamento.builder().dataAgendamento(LocalDateTime.of(2026,6,20,9,30)).tipo("Consulta").status("Agendado").observacao("Retorno dermatológico").pet(p2).veterinario(v3).build());

        eventoRepo.save(EventoSaude.builder().tipoEvento("Vacina").dataEvento(LocalDate.of(2025,6,15)).descricao("Vacina V10 aplicada").resultado("Sem reação adversa").pet(p1).veterinario(v1).build());
        eventoRepo.save(EventoSaude.builder().tipoEvento("Consulta").dataEvento(LocalDate.of(2025,8,10)).descricao("Check-up geral - bom estado").resultado("Peso adequado, sem alterações").pet(p1).veterinario(v1).build());
        eventoRepo.save(EventoSaude.builder().tipoEvento("Vacina").dataEvento(LocalDate.of(2025,7,20)).descricao("Vacina antirrábica").resultado("Aplicada com sucesso").pet(p2).veterinario(v3).build());
        eventoRepo.save(EventoSaude.builder().tipoEvento("Exame").dataEvento(LocalDate.of(2025,9,5)).descricao("Hemograma completo").resultado("Todos os valores dentro da normalidade").pet(p3).veterinario(v4).build());
        eventoRepo.save(EventoSaude.builder().tipoEvento("Cirurgia").dataEvento(LocalDate.of(2025,10,12)).descricao("Remoção de tumor benigno").resultado("Cirurgia bem-sucedida, recuperação normal").pet(p5).veterinario(v2).build());
        eventoRepo.save(EventoSaude.builder().tipoEvento("Consulta").dataEvento(LocalDate.of(2025,11,3)).descricao("Consulta dermatológica - alergia").resultado("Prescrito antialérgico por 15 dias").pet(p4).veterinario(v3).build());

        protocoloRepo.save(Protocolo.builder().especie("Canino").raca(null).tipoProtocolo("Vacina").descricao("Vacina V10 - reforço anual obrigatório").intervaloDias(365).faixaEtaria("Adulto").build());
        protocoloRepo.save(Protocolo.builder().especie("Canino").raca(null).tipoProtocolo("Vacina").descricao("Vacina antirrábica - reforço anual").intervaloDias(365).faixaEtaria("Todas").build());
        protocoloRepo.save(Protocolo.builder().especie("Felino").raca(null).tipoProtocolo("Vacina").descricao("Vacina V4 felina - reforço anual").intervaloDias(365).faixaEtaria("Adulto").build());
        protocoloRepo.save(Protocolo.builder().especie("Canino").raca(null).tipoProtocolo("Check-up").descricao("Check-up preventivo semestral").intervaloDias(180).faixaEtaria("Todas").build());
        protocoloRepo.save(Protocolo.builder().especie("Felino").raca(null).tipoProtocolo("Check-up").descricao("Check-up preventivo semestral").intervaloDias(180).faixaEtaria("Todas").build());
        protocoloRepo.save(Protocolo.builder().especie("Canino").raca("Bulldog").tipoProtocolo("Exame").descricao("Avaliação cardíaca - raça braquicefálica").intervaloDias(180).faixaEtaria("Adulto").build());
    }
}
