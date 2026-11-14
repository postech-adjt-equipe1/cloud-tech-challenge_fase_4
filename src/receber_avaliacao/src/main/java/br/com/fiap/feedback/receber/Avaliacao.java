package br.com.fiap.feedback.receber;

import java.time.Instant;
import java.time.LocalDate;

public record Avaliacao(
        String id,
        String descricao,
        int nota,
        Urgencia urgencia,
        Instant dataEnvio,
        LocalDate dia) {

    public static final int NOTA_MINIMA = 0;
    public static final int NOTA_MAXIMA = 10;
    public static final int TAMANHO_MAXIMO_DESCRICAO = 2000;
}
