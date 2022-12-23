package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;

public class Main {
    public static void main(String[] args) {

        System.out.println("Welcome");
        String file = args[0];

        Index index = new Index();
        long start = System.currentTimeMillis();
        index.processCollection(file);
        long end = System.currentTimeMillis();
        System.out.println("Elapsed Time in milli seconds: "+ ((end-start)/1000));
    }
}