package br.com.fiap.feedback.receber;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FabricaAvaliacaoTest {

    private static final Instant AGORA = Instant.parse("2026-10-06T02:30:00Z");

    private final FabricaAvaliacao fabrica = new FabricaAvaliacao(
            Clock.fixed(AGORA, ZoneOffset.UTC),
            ZoneId.of("America/Sao_Paulo"),
            () -> "id-fixo");

    @Test
    void criaAvaliacaoComIdDataEDadosRecebidos() {
        Avaliacao avaliacao = fabrica.criar(new DadosAvaliacao("Aula ótima", 9));

        assertEquals("id-fixo", avaliacao.id());
        assertEquals("Aula ótima", avaliacao.descricao());
        assertEquals(9, avaliacao.nota());
        assertEquals(AGORA, avaliacao.dataEnvio());
    }

    @Test
    void usaODiaDoFusoHorario() {
        assertEquals(LocalDate.of(2026, 10, 5), fabrica.criar(new DadosAvaliacao("Aula ótima", 9)).dia());
    }

    @Test
    void classificaUrgenciaPelaNota() {
        assertEquals(Urgencia.BAIXA, fabrica.criar(new DadosAvaliacao("Aula ótima", 9)).urgencia());
        assertEquals(Urgencia.CRITICA, fabrica.criar(new DadosAvaliacao("Áudio ruim", 0)).urgencia());
    }
}
