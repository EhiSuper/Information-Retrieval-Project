package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;

import java.util.ArrayList;
import java.util.HashMap;

public class TFIDF extends ScoringFunction{

    public TFIDF(HashMap<String, ArrayList<Posting>> postingLists, String[] queryTerms, long nDocuments){
        super(postingLists, queryTerms, nDocuments);
    }

    public double documentWeight(String term, Posting posting){
        double tf = 1+Math.log(posting.getFreq());
        double idf = this.idf.get(term);
        return tf*idf;
    }
}
