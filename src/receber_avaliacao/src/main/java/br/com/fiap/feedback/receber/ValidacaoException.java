package br.com.fiap.feedback.receber;

import java.util.List;

public class ValidacaoException extends RuntimeException {

    private final List<String> erros;

    public ValidacaoException(List<String> erros) {
        super(String.join("; ", erros));
        this.erros = List.copyOf(erros);
    }

    public List<String> getErros() {
        return erros;
    }
}
