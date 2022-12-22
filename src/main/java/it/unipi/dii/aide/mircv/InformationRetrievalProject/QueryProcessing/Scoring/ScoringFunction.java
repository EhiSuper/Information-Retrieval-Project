package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class ScoringFunction {
    protected String[] queryTerms;
    protected HashMap<String, Double> idf;

    public ScoringFunction(HashMap<String, ArrayList<Posting>> postingLists, String[] queryTerms, long nDocuments) {

        this.queryTerms = queryTerms;

        this.idf = new HashMap<>();
        for (String term : postingLists.keySet()){
            double df = postingLists.get(term).size();
            idf.put(term, Math.log(nDocuments/df));
        }
    }

    public double score(String term, Posting posting){
        return termWeight(term) * documentWeight(term, posting);
    }



    public abstract double documentWeight(String term, Posting posting);

    public double termWeight(String term){
        int numOccurrences = 0;
        for (String queryTerm : this.queryTerms) {
            if (queryTerm.equals(term)) {
                numOccurrences++;
            }
        }

        return (double) numOccurrences / queryTerms.length;
    }
}