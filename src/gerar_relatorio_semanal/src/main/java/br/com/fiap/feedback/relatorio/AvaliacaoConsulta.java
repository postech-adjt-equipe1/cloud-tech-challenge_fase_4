package br.com.fiap.feedback.relatorio;

import java.time.LocalDate;
import java.util.List;

public interface AvaliacaoConsulta {

    List<AvaliacaoRegistrada> buscarPorDia(LocalDate dia);
}
