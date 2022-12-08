package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring.BM25;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.TextPreprocessing.TextPreprocessing;

import java.util.*;

public class QueryProcessor {
    private int k;
    public QueryProcessor(int nResults){
        this.k = nResults;
    }

    public Set<Integer> processQuery(String query){
        String[] queryTerms = TextPreprocessing.parse(query).split(" "); //Parse the query

        //Lookup example
        HashMap<String,ArrayList<Posting>> postingLists = new HashMap<String,ArrayList<Posting>>();
        postingLists.put("dish", new ArrayList<Posting>(Arrays.asList(new Posting(5,1), new Posting(10,2))));
        postingLists.put("washing", new ArrayList<Posting>(Arrays.asList(new Posting(2,1), new Posting(10,1))));

        //HashMap<String, ArrayList<Posting>> postingLists = lookup(queryTerms); //Retrieve candidate postinglists

        HashMap<Integer, Double> final_scores = BM25.score(queryTerms, postingLists); //Rank documents

        TreeMap<Integer, Double> sortedScores = new TreeMap<>(final_scores); //Sort final scores

        return null; //Return scores
    }


    public HashMap<String,ArrayList<Posting>> lookup(String[] queryTerms){
        return null;

    }

    public HashMap<Integer,Double> score(String[] queryTerms, HashMap<String, ArrayList<Posting>> postingLists){
        return null;
    }
}
