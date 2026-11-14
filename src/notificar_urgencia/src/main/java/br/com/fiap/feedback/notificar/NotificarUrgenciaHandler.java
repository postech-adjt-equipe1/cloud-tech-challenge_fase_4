package br.com.fiap.feedback.notificar;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.StreamsEventResponse;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.services.sns.SnsClient;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NotificarUrgenciaHandler implements RequestHandler<DynamodbEvent, StreamsEventResponse> {

    private final Notificador notificador;
    private final ZoneId fusoHorario;

    public NotificarUrgenciaHandler() {
        this(
                new SnsNotificador(
                        SnsClient.builder().httpClient(UrlConnectionHttpClient.create()).build(),
                        env("TOPICO_ALERTA_ARN", null)),
                ZoneId.of(env("FUSO_HORARIO", "America/Sao_Paulo")));
    }

    public NotificarUrgenciaHandler(Notificador notificador, ZoneId fusoHorario) {
        this.notificador = notificador;
        this.fusoHorario = fusoHorario;
    }

    @Override
    public StreamsEventResponse handleRequest(DynamodbEvent evento, Context context) {
        List<StreamsEventResponse.BatchItemFailure> falhas = new ArrayList<>();
        if (evento == null || evento.getRecords() == null) {
            return new StreamsEventResponse(falhas);
        }

        for (DynamodbEvent.DynamodbStreamRecord registro : evento.getRecords()) {
            try {
                if (!"INSERT".equals(registro.getEventName())) {
                    continue;
                }
                Optional<AlertaUrgencia> alerta = AlertaUrgencia.deItem(registro.getDynamodb().getNewImage());
                if (alerta.isEmpty()) {
                    continue;
                }
                notificador.enviar(alerta.get().assunto(), alerta.get().mensagem(fusoHorario));
                log(context, "Alerta enviado para a avaliação " + alerta.get().id());
            } catch (RuntimeException e) {
                log(context, "Falha ao processar registro " + registro.getEventID() + ": " + e);
                falhas.add(new StreamsEventResponse.BatchItemFailure(registro.getDynamodb().getSequenceNumber()));
                break;
            }
        }
        return new StreamsEventResponse(falhas);
    }

    private static void log(Context context, String mensagem) {
        if (context != null && context.getLogger() != null) {
            context.getLogger().log(mensagem);
        }
    }

    private static String env(String nome, String padrao) {
        String valor = System.getenv(nome);
        if (valor == null || valor.isBlank()) {
            if (padrao == null) {
                throw new IllegalStateException("Variável de ambiente obrigatória ausente: " + nome);
            }
            return padrao;
        }
        return valor;
    }
}
