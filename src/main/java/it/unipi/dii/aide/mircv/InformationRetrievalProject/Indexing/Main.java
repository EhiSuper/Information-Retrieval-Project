package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.TextPreprocessing.TextPreprocessing;

public class Main {
    public static void main(String[] args) {

        System.out.println("Welcome");
        String file = args[0];
        String type = args[1];
        Boolean stopwordsRemoval = Boolean.valueOf(args[2]); //Stopwords Removal
        Boolean wordsStemming = Boolean.valueOf(args[3]); //Stemming

        if(!type.equals("text") && !type.equals("bytes")){
            System.out.println("Sorry the encoding type is wrong please try again");
        }
        else{
            Index index = new Index(new TextPreprocessing(stopwordsRemoval, wordsStemming));
            long start = System.currentTimeMillis();
            index.processCollection(file, type);
            long end = System.currentTimeMillis();
            System.out.println("Elapsed Time in milliseconds: " + (end-start));
        }
    }
}