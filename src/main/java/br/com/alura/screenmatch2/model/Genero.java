package br.com.alura.screenmatch2.model;

public enum Genero {
    ACAO("Action"),
    ROMANCE("Romance"),
    DRAMA("Drama"),
    COMEDIA("Comedy"),
    CRIME("Crime"),
    SUSPENSE("Thriller"),
    TERROR("Horror"),
    MUSICAL("Musical");
    private String generoOmdb;

    Genero(String generoOmdb){
        this.generoOmdb = generoOmdb;
    }

    public static Genero fromString(String text){
        for (Genero genero : Genero.values()){
            if(genero.generoOmdb.equalsIgnoreCase(text)){
                return genero;
            }
        }
        throw new IllegalArgumentException("Nenhum gênero encontrado.");
    }
}
