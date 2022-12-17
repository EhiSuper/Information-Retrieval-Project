package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class ScoringFunction {
    protected HashMap<String, Double> idf;

    public ScoringFunction(HashMap<String, ArrayList<Posting>> postingLists, long nDocuments) {

        this.idf = new HashMap<>();
        for (String term : postingLists.keySet()){
            double df = postingLists.get(term).size();
            idf.put(term, Math.log(nDocuments/df));
        }
    }

    public abstract double score(String term, Posting posting);
}