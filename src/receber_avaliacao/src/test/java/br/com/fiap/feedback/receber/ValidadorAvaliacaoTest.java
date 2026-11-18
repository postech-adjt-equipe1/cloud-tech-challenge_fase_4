package br.com.fiap.feedback.receber;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidadorAvaliacaoTest {

    private final ValidadorAvaliacao validador = new ValidadorAvaliacao(new ObjectMapper());

    @Test
    void aceitaAvaliacaoValida() {
        DadosAvaliacao dados = validador.validar("{\"descricao\":\"  Aula ótima  \",\"nota\":9}");

        assertEquals("Aula ótima", dados.descricao());
        assertEquals(9, dados.nota());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 10})
    void aceitaNotasNosLimites(int nota) {
        assertEquals(nota, validador.validar("{\"descricao\":\"ok\",\"nota\":" + nota + "}").nota());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "não é json", "[1,2]", "\"texto\""})
    void rejeitaCorpoInvalido(String corpo) {
        assertThrows(ValidacaoException.class, () -> validador.validar(corpo));
    }

    @Test
    void rejeitaCorpoNulo() {
        assertThrows(ValidacaoException.class, () -> validador.validar(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"nota\":5}",
            "{\"descricao\":null,\"nota\":5}",
            "{\"descricao\":\"   \",\"nota\":5}",
            "{\"descricao\":123,\"nota\":5}"
    })
    void rejeitaDescricaoInvalida(String corpo) {
        ValidacaoException e = assertThrows(ValidacaoException.class, () -> validador.validar(corpo));
        assertTrue(e.getErros().get(0).contains("descricao"));
    }

    @Test
    void rejeitaDescricaoMuitoLonga() {
        String longa = "a".repeat(Avaliacao.TAMANHO_MAXIMO_DESCRICAO + 1);
        assertThrows(ValidacaoException.class,
                () -> validador.validar("{\"descricao\":\"" + longa + "\",\"nota\":5}"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"descricao\":\"ok\"}",
            "{\"descricao\":\"ok\",\"nota\":null}",
            "{\"descricao\":\"ok\",\"nota\":\"7\"}",
            "{\"descricao\":\"ok\",\"nota\":7.5}",
            "{\"descricao\":\"ok\",\"nota\":7.0}",
            "{\"descricao\":\"ok\",\"nota\":-1}",
            "{\"descricao\":\"ok\",\"nota\":11}",
            "{\"descricao\":\"ok\",\"nota\":99999999999}"
    })
    void rejeitaNotaInvalida(String corpo) {
        ValidacaoException e = assertThrows(ValidacaoException.class, () -> validador.validar(corpo));
        assertTrue(e.getErros().get(0).contains("nota"));
    }

    @Test
    void reportaTodosOsErrosDeUmaVez() {
        ValidacaoException e = assertThrows(ValidacaoException.class, () -> validador.validar("{}"));
        assertEquals(2, e.getErros().size());
    }
}
