package br.com.fiap.feedback.receber;

public enum Urgencia {
    CRITICA,
    MEDIA,
    BAIXA;

    public static Urgencia classificar(int nota) {
        if (nota < Avaliacao.NOTA_MINIMA || nota > Avaliacao.NOTA_MAXIMA) {
            throw new IllegalArgumentException("Nota fora do intervalo 0 a 10: " + nota);
        }
        if (nota <= 3) {
            return CRITICA;
        }
        if (nota <= 6) {
            return MEDIA;
        }
        return BAIXA;
    }
}
