package br.com.fiap.feedback.relatorio;

import java.time.Instant;
import java.time.LocalDate;

public record AvaliacaoRegistrada(
        String id,
        String descricao,
        int nota,
        String urgencia,
        Instant dataEnvio,
        LocalDate dia) {
}
