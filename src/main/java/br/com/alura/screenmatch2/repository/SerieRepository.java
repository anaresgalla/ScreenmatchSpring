package br.com.alura.screenmatch2.repository;

import br.com.alura.screenmatch2.model.Episodio;
import br.com.alura.screenmatch2.model.Genero;
import br.com.alura.screenmatch2.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);
    List<Serie> findByElencoContainingIgnoreCaseAndAvaliacaoGreaterThanEqual
            (String nomeElenco, Double avaliacao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();
    List<Serie> findByGenero(Genero genero);

    /* List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual
            (int totalTemporadas, double avaliacao);*/
    @Query("select s from Serie s WHERE s.totalTemporadas <= :totalTemporadas " +
            "AND s.avaliacao >= :avaliacao")
    List<Serie> seriesPorTempoadaEAvaliacao(int totalTemporadas, double avaliacao);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo " +
            "ILIKE %:trechoEpisodio%")
    List<Episodio> episodiosPorTrecho(String trechoEpisodio);

    @Query("SELECT e FROM Episodio e WHERE e.serie = :serie AND e.avaliacao > 0"
            + "ORDER BY e.avaliacao DESC")
    List<Episodio> topEpisodiosPorSerie(
            @Param("serie") Serie serie,
            Pageable pageable
    );

    @Query("SELECT e FROM Episodio e WHERE e.serie = :serie " +
            "AND YEAR(e.dataLancamento) >= :anoLancamento")
    List<Episodio> episodiosPorSerieEAno(
            @Param("serie") Serie serie,
            @Param("anoLancamento") int anoLancamento);
}
