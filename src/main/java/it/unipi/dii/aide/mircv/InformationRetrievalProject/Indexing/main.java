package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;

public class main {
    public static void main(String[] args) {

        System.out.println("Welcome");
        String file = args[0];

        Index index = new Index();
        index.processCollection(file);
    }
}