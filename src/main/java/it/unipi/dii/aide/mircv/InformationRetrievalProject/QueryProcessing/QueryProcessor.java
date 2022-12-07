package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.TextPreprocessing.TextPreprocessing;

import java.util.*;

public class QueryProcessor {

    public QueryProcessor(){}

    public Set<Integer> processQuery(String query){
        String[] queryTerms = TextPreprocessing.parse(query).split(" "); //Parse the query

        HashMap<String, ArrayList<Posting>> postingLists = lookup(queryTerms); //Retrieve candidate postinglists

        HashMap<Integer, Double> final_scores = rank(queryTerms, postingLists);

        TreeMap<Integer, Double> sortedScores = new TreeMap<>(final_scores);

        return null;
    }


    public HashMap<String,ArrayList<Posting>> lookup(String[] queryTerms){
        return null;

    }

    public HashMap<Integer,Double> rank(String[] queryTerms, HashMap<String, ArrayList<Posting>> postingLists){
        return null;
    }
}
