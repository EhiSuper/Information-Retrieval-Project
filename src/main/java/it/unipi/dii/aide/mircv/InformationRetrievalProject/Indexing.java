package it.unipi.dii.aide.mircv.InformationRetrievalProject;

public class Indexing {
    public static void main(String[] args) {

        System.out.println("Welcome");
        String file = args[0];
        String outputFile = args[1];

        Index index = new Index();
        index.processCollection(file, outputFile);
    }
}