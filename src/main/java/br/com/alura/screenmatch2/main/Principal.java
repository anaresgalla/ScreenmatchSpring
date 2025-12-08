package br.com.alura.screenmatch2.main;

import br.com.alura.screenmatch2.model.DadosSerie;
import br.com.alura.screenmatch2.model.DadosTemporadas;
import br.com.alura.screenmatch2.model.Episodio;
import br.com.alura.screenmatch2.model.Serie;
import br.com.alura.screenmatch2.repository.SerieRepository;
import br.com.alura.screenmatch2.service.ConsumoAPI;
import br.com.alura.screenmatch2.service.ConverteDados;
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
        Optional<Serie> serieBuscada =
                repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        if(serieBuscada.isPresent()){
            System.out.println("Dados da série: " + serieBuscada.get());
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
}
