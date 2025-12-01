package br.com.alura.screenmatch2.main;

import br.com.alura.screenmatch2.model.DadosSerie;
import br.com.alura.screenmatch2.model.DadosTemporadas;
import br.com.alura.screenmatch2.model.Episodio;
import br.com.alura.screenmatch2.service.ConsumoAPI;
import br.com.alura.screenmatch2.service.ConverteDados;

import java.util.*;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "http://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=a0b5e147";
    private List<DadosSerie> dadosSeries = new ArrayList<>();

    public void exibeMenu(){
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    Digite sua opção:
                                    
                    1 - Buscar Séries
                    2 - Buscar Episódios
                    3 - Listar Séries Buscadas
                                    
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
        dadosSeries.add(dados);
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
        DadosSerie dadosSerie = getDadosSerie();
        List<DadosTemporadas> temporadas = new ArrayList<>();

        for (int i = 0; i <= dadosSerie.totalTemporadas() ; i++) {
            var json = consumo.obterDados(ENDERECO +
                    dadosSerie.titulo().replace(" ", "+") +
                    "&season=" + i + API_KEY);
            DadosTemporadas dadosTemporadas = conversor.obterDados(json, DadosTemporadas.class);
            temporadas.add(dadosTemporadas);
        }
        temporadas.forEach(System.out::println);
    }

    private void listarSeriesBuscadas(){
        dadosSeries.forEach(System.out::println);
    }
}
