package br.com.fiap.feedback.relatorio;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DynamoDbAvaliacaoConsulta implements AvaliacaoConsulta {

    public static final String INDICE_POR_DIA = "por-dia";

    private final DynamoDbClient dynamoDb;
    private final String nomeTabela;

    public DynamoDbAvaliacaoConsulta(DynamoDbClient dynamoDb, String nomeTabela) {
        this.dynamoDb = dynamoDb;
        this.nomeTabela = nomeTabela;
    }

    @Override
    public List<AvaliacaoRegistrada> buscarPorDia(LocalDate dia) {
        QueryRequest consulta = QueryRequest.builder()
                .tableName(nomeTabela)
                .indexName(INDICE_POR_DIA)
                .keyConditionExpression("dia = :dia")
                .expressionAttributeValues(Map.of(":dia", AttributeValue.fromS(dia.toString())))
                .build();

        return dynamoDb.queryPaginator(consulta).items().stream()
                .map(DynamoDbAvaliacaoConsulta::converter)
                .toList();
    }

    private static AvaliacaoRegistrada converter(Map<String, AttributeValue> item) {
        return new AvaliacaoRegistrada(
                item.get("id").s(),
                item.get("descricao").s(),
                Integer.parseInt(item.get("nota").n()),
                item.get("urgencia").s(),
                Instant.parse(item.get("dataEnvio").s()),
                LocalDate.parse(item.get("dia").s()));
    }
}
