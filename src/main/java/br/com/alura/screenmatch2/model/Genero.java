package br.com.alura.screenmatch2.model;

public enum Genero {
    ACAO("Action", "Ação"),
    ROMANCE("Romance", "Romance"),
    DRAMA("Drama", "Drama"),
    COMEDIA("Comedy", "Comédia"),
    CRIME("Crime", "Crime"),
    SUSPENSE("Thriller", "Suspense"),
    TERROR("Horror", "Terror"),
    MUSICAL("Musical", "Musical"),
    FICCAO("Sci-Fi", "Ficção Científica"),
    AVENTURA("Adventure", "Aventura"),
    FANTASIA("Fantasy", "Fantasia");

    private String generoOmdb;

    private String generoPortugues;

    Genero(String generoOmdb, String generoPortugues){
        this.generoOmdb = generoOmdb;
        this.generoPortugues = generoPortugues;
    }

    public static Genero fromString(String text){
        for (Genero genero : Genero.values()){
            if(genero.generoOmdb.equalsIgnoreCase(text)){
                return genero;
            }
        }
        throw new IllegalArgumentException("Nenhum gênero encontrado para: " + text);
    }

    public static Genero fromPortugues(String text){
        for(Genero genero : Genero.values()){
            if(genero.generoPortugues.equalsIgnoreCase(text)){
                return genero;
            }
        }
        throw new IllegalArgumentException("Nenhum gẽnero encontrado para: " + text);
    }
}
