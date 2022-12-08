package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing;

import java.util.Scanner;
import java.util.Set;

public class main {
    public static void main(String[] args) {

        System.out.println("Welcome to Query Processing ");
        //String file = args[0];
        //String outputFile = args[1];

        QueryProcessor queryProcessor = new QueryProcessor(10);

        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.print("Insert the new query: ");
            String query = sc.nextLine();
            Set<Integer> results = queryProcessor.processQuery(query);
            System.out.println(results);
        }



    }
}