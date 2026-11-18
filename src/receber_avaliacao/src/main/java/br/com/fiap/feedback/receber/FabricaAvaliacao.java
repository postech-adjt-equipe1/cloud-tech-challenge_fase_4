package br.com.fiap.feedback.receber;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;
import java.util.function.Supplier;

public class FabricaAvaliacao {

    private final Clock clock;
    private final ZoneId fusoHorario;
    private final Supplier<String> geradorId;

    public FabricaAvaliacao(Clock clock, ZoneId fusoHorario, Supplier<String> geradorId) {
        this.clock = clock;
        this.fusoHorario = fusoHorario;
        this.geradorId = geradorId;
    }

    public FabricaAvaliacao(ZoneId fusoHorario) {
        this(Clock.systemUTC(), fusoHorario, () -> UUID.randomUUID().toString());
    }

    public Avaliacao criar(DadosAvaliacao dados) {
        Instant agora = clock.instant();
        return new Avaliacao(
                geradorId.get(),
                dados.descricao(),
                dados.nota(),
                Urgencia.classificar(dados.nota()),
                agora,
                agora.atZone(fusoHorario).toLocalDate());
    }
}
