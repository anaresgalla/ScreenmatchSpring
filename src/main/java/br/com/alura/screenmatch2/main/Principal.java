package br.com.alura.screenmatch2.main;

import br.com.alura.screenmatch2.model.*;
import br.com.alura.screenmatch2.repository.SerieRepository;
import br.com.alura.screenmatch2.service.ConsumoAPI;
import br.com.alura.screenmatch2.service.ConverteDados;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "http://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=a0b5e147";
    private List<DadosSerie> dadosSeries = new ArrayList<>();

    private SerieRepository repositorio;

    private List<Serie> series = new ArrayList<>();

    private Optional<Serie> serieBusca;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu(){
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    Digite sua opção:
                                    
                    1 - Buscar Séries
                    2 - Buscar Episódios
                    3 - Listar Séries Buscadas
                    4 - Buscar Séries por Nome
                    5 - Buscar Séries por Elenco
                    6 - Buscar as TOP 5 Séries
                    7 - Buscar Séries por Gênero
                    8 - Filtrar Séries
                    9 - Buscar Episódio por Trecho
                    10 - Buscar Top 5 Episódios da Série
                    11 - Buscar Episódios a partir de uma Data
                                    
                    0 - Sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorElenco();
                case 6:
                    buscarTop5Series();
                case 7:
                    buscarSeriesPorGenero();
                    break;
                case 8:
                    filtrarSeriesPorTempEAval();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    topEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodiosAPartirDeData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void buscarSerieWeb(){
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        // Verifica se já existe
        Optional<Serie> serieExistente =
                repositorio.findByTituloContainingIgnoreCase(serie.getTitulo());
        if (serieExistente.isPresent()) {
            System.out.println("A série \"" + serie.getTitulo()
                    + "\" já está cadastrada no banco.");
            System.out.println(serieExistente.get()); // opcional, exibe a já existente
            return;  // evita tentar salvar novamente
        }
        // Se não existe, salva
        repositorio.save(serie);
        System.out.println("Série salva com sucesso:");
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie(){
        System.out.println("Digite o nome da série para busca: ");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO +
                nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        listarSeriesBuscadas();
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie =
                repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if(serie.isPresent()){
            var serieEncontrada = serie.get();
            List<DadosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO +
                        serieEncontrada.getTitulo()
                                .replace(" ", "+") +
                        "&season=" + i + API_KEY);
                DadosTemporadas dadosTemporadas =
                        conversor.obterDados(json, DadosTemporadas.class);

                if(dadosTemporadas == null ||
                    dadosTemporadas.episodios() == null ||
                    dadosTemporadas.numero() == null){
                    System.out.println("A temporada " + i
                            + " retornou dados inválidos." +
                            " Pulando...");
                    continue;
                }

                temporadas.add(dadosTemporadas);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(t -> t.episodios().stream()
                            .map(e -> new Episodio(t.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        } else {
            System.out.println("Série não encontrada.");
        }
    }

    private void listarSeriesBuscadas(){
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo(){
        System.out.println("Procure uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();
        serieBusca = repositorio
                .findByTituloContainingIgnoreCase(nomeSerie);
        if(serieBusca.isPresent()){
            System.out.println("Dados da série: " + serieBusca.get());
        } else{
            System.out.println("Série não encontrada!");
        }
    }

    private void buscarSeriePorElenco(){
        System.out.println("Digite o nome para busca: ");
        var nomeElenco = leitura.nextLine();
        System.out.println("Avaliações da série a partir do valor: ");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriesEncontradas =
                repositorio.findByElencoContainingIgnoreCaseAndAvaliacaoGreaterThanEqual
                        (nomeElenco, avaliacao);
        System.out.println("Series em que " + nomeElenco + " trabalhou: ");
        seriesEncontradas.forEach(s ->
                System.out.println("\"" + s.getTitulo() + "\" - Avaliação: "
                        + s.getAvaliacao()));
    }

    private void buscarTop5Series(){
        List<Serie> serieTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        serieTop.forEach(s ->
                System.out.println("\"" + s.getTitulo() + "\" - Avaliação: "
                        + s.getAvaliacao()));
    }

    private void buscarSeriesPorGenero(){
        System.out.println("Deseja buscar séries de que categoria/gênero? ");
        var nomeGenero = leitura.nextLine();
        Genero genero = Genero.fromPortugues(nomeGenero);
        List<Serie> seriesPorGenero = repositorio.findByGenero(genero);
        System.out.println("Séries do gênero: " + nomeGenero);
        seriesPorGenero.forEach(System.out::println);
    }

    private void filtrarSeriesPorTempEAval(){
        System.out.println("Filtrar séries até quantas temporadas? ");
        var totalTemporadas = leitura.nextInt();
        leitura.nextLine();
        System.out.println("Com avaliação a partir de que valor? ");
        var avaliacao = leitura.nextDouble();
        leitura.nextLine();
        List<Serie> filtroSeries = repositorio
                /*.findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual
                        (totalTemporadas, avaliacao);*/
                        .seriesPorTempoadaEAvaliacao(totalTemporadas, avaliacao);
        System.out.println("*** SÉRIES FILTRADAS ***");
        filtroSeries.forEach(s->
                System.out.println("Título: \"" + s.getTitulo() + "\"" +
                        " - Avaliação: " + s.getAvaliacao()));
    }

    private void buscarEpisodioPorTrecho(){
        System.out.println("Qual o nome do episódio para busca? ");
        var trechoEpisodio = leitura.nextLine();
        List<Episodio> episodiosEncontrados =
                repositorio.episodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach( e ->
                System.out.printf("Série: %s - Temporada: %s - Episódio: %s - %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(),
                        e.getNumeroEpisodio(), e.getTitulo()));
    }

    private void topEpisodiosPorSerie(){
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(
                    serie, PageRequest.of(0,5));
            topEpisodios.forEach(e ->
                    System.out.printf("Série: %s - Temporada: %s - Episódio: " +
                            "%s - %s - Avaliação: %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(),
                            e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
        }
    }

    private void buscarEpisodiosAPartirDeData(){
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            System.out.println("Digite o ano limite para busca: ");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno =
                    repositorio.episodiosPorSerieEAno(serie, anoLancamento);

            episodiosAno.forEach(System.out::println);
        }
    }
}
