package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing;

import java.util.PriorityQueue;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        int nDocuments = 5;
        double avdl = 3;

        System.out.println("Welcome to Query Processing ");
        //String file = args[0];
        //String outputFile = args[1];

        QueryProcessor queryProcessor = new QueryProcessor(10,nDocuments,avdl);

        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.print("Insert the new query: ");
            String query = sc.nextLine();
            PriorityQueue<FinalScore> results = queryProcessor.processQuery(query);
            printResults(results);
            System.out.println();

        }
    }

    public static void printResults(PriorityQueue<FinalScore> results){
        System.out.print("[ ");
        while(!results.isEmpty()){
            System.out.print("("+ results.poll() + "), ");
        }
        System.out.print(" ]");
    }
}