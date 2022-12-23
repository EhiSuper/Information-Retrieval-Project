package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;

public class Main {
    public static void main(String[] args) {

        System.out.println("Welcome");
        String file = args[0];
        String type = args[1];
        if(!type.equals("text") && !type.equals("bytes")){
            System.out.println("Sorry the encoding type is wrong please try again");
        }
        else{
            Index index = new Index();
            long start = System.currentTimeMillis();
            index.processCollection(file, type);
            long end = System.currentTimeMillis();
            System.out.println("Elapsed Time in milliseconds: " + (end-start));
        }
    }
}