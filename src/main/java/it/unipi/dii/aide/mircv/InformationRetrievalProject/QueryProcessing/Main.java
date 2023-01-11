package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessing.BoundedPriorityQueue;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        System.out.println("Welcome to Query Processing ");
        int nResults = Integer.parseInt(args[0]); //number of results
        String scoringFunction = args[1]; //Which scoring function to use
        String documentProcessor = args[2]; //How to process the postinglist
        String relationType = args[3]; //Type of relation (conjunctive or disjunctive)
        Boolean stopwordsRemoval = Boolean.valueOf(args[4]); //Stopwords Removal
        Boolean wordsStemming = Boolean.valueOf(args[5]); //Stemming

        QueryProcessor queryProcessor = new QueryProcessor(nResults, scoringFunction, documentProcessor, relationType, stopwordsRemoval, wordsStemming);

        Scanner sc = new Scanner(System.in);

        while(true){
            System.out.print("Insert the new query: ");
            String query = sc.nextLine();

            long start = System.currentTimeMillis();
            BoundedPriorityQueue results = queryProcessor.processQuery(query);
            long end = System.currentTimeMillis();

            System.out.println("Elapsed Time in milliseconds: "+ (end-start));
            results.printResults();
        }
    }
}