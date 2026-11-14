package br.com.fiap.feedback.notificar;

import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

public record AlertaUrgencia(String id, String descricao, String urgencia, Instant dataEnvio) {

    public static final String URGENCIA_CRITICA = "CRITICA";

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static Optional<AlertaUrgencia> deItem(Map<String, AttributeValue> item) {
        if (item == null || !URGENCIA_CRITICA.equals(texto(item, "urgencia"))) {
            return Optional.empty();
        }
        return Optional.of(new AlertaUrgencia(
                texto(item, "id"),
                texto(item, "descricao"),
                texto(item, "urgencia"),
                Instant.parse(texto(item, "dataEnvio"))));
    }

    public String assunto() {
        return "[URGENTE] Nova avaliação crítica recebida";
    }

    public String mensagem(ZoneId fusoHorario) {
        return """
                Uma avaliação com urgência crítica foi recebida na plataforma de feedback.

                Descrição: %s
                Urgência: %s
                Data de envio: %s (%s)

                Identificador: %s
                """.formatted(descricao, urgencia, FORMATO_DATA.format(dataEnvio.atZone(fusoHorario)), fusoHorario, id);
    }

    private static String texto(Map<String, AttributeValue> item, String atributo) {
        AttributeValue valor = item.get(atributo);
        if (valor == null || valor.getS() == null) {
            throw new IllegalArgumentException("Atributo ausente no item: " + atributo);
        }
        return valor.getS();
    }
}
