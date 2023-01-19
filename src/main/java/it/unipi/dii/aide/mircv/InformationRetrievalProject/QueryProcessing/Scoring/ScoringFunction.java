package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.HandleIndex;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class ScoringFunction {
    protected String[] queryTerms;
    protected HashMap<String, Double> idf;

    public ScoringFunction(HashMap<String, ArrayList<Posting>> postingLists, String[] queryTerms, HandleIndex handleIndex) {
        double nDocuments = handleIndex.getCollectionStatistics().getDocuments();
        this.queryTerms = queryTerms;

        this.idf = new HashMap<>();
        for (String term : postingLists.keySet()){
            double df = handleIndex.getLexicon().getLexicon().get(term).getPostingListLength();
            idf.put(term, Math.log(nDocuments/df));
        }
    }

    public double score(String term, Posting posting){
        return documentWeight(term, posting);
    }



    public abstract double documentWeight(String term, Posting posting);


}