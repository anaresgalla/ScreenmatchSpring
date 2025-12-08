package br.com.alura.screenmatch2.model;

import br.com.alura.screenmatch2.service.traducao.ConsultaMyMemory;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Entity
@Table(name = "series")
public class Serie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titulo;
    private Integer totalTemporadas;
    private Double avaliacao;
    @Enumerated(EnumType.STRING)
    private Genero genero;
    private String sinopse;
    private String ano;
    private String elenco;
    private String poster;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    private List<Episodio> episodios = new ArrayList<>();
    public Serie(){}

    public Serie(DadosSerie dadosSerie){
        this.titulo = dadosSerie.titulo();
        try {
            this.totalTemporadas = Integer.valueOf(dadosSerie.totalTemporadas());
        } catch (Exception e) {
            this.totalTemporadas = 0;
        }
        this.avaliacao = OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).
                orElse(0);
        this.genero = Genero.fromString(dadosSerie.genero().split(",")[0]
                .trim());
        this.ano = dadosSerie.ano();
        this.elenco = dadosSerie.elenco();
        this.sinopse = ConsultaMyMemory.obterTraducao(dadosSerie.sinopse()).trim();
        this.poster = dadosSerie.poster();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getElenco() {
        return elenco;
    }

    public void setElenco(String elenco) {
        this.elenco = elenco;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public List<Episodio> getEpisodios() {
        return episodios;
    }

    public void setEpisodios(List<Episodio> episodios) {
        episodios.forEach(e -> e.setSerie(this));
        this.episodios = episodios;
    }

    @Override
    public String toString() {
        return "genero = " + genero +
                ", titulo = " + titulo +
                ", totalTemporadas = " + totalTemporadas +
                ", avaliacao = " + avaliacao +
                ", sinopse = '" + sinopse + "'" +
                ", episodios = " + episodios +
                ", ano = " + ano +
                ", elenco = '" + elenco + "'" +
                ", poster = '" + poster + "'";
    }
}
