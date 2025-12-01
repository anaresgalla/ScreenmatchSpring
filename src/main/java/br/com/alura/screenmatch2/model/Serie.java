package br.com.alura.screenmatch2.model;

import java.util.OptionalDouble;

public class Serie {
    private String titulo;
    private Integer totalTemporadas;
    private Double avaliacao;
    private Genero genero;
    private String sinopse;
    private String ano;
    private String elenco;
    private String poster;

    public Serie(DadosSerie dadosSerie){
        this.titulo = dadosSerie.titulo();
        this.totalTemporadas = dadosSerie.totalTemporadas();
        this.avaliacao = OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).
                orElse(0);
        this.genero = Genero.fromString(dadosSerie.genero().split(",")[0]
                .trim());
        this.ano = dadosSerie.ano();
        this.elenco = dadosSerie.elenco();
        this.sinopse = dadosSerie.sinopse();
        this.poster = dadosSerie.poster();
    }
}
