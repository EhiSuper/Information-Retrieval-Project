package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;

import java.util.ArrayList;
import java.util.HashMap;

public class TFIDF extends ScoringFunction{

    public TFIDF(HashMap<String, ArrayList<Posting>> postingLists, long nDocuments){
        super(postingLists, nDocuments);
    }

    /*
        tf (term frequency) = n° times that the term occures in the document
        df (document frequency) = n° documents in which the term occures
        */
    public double score(String term, Posting posting){
        double tf = 1+Math.log(posting.getFreq());
        double idf = this.idf.get(term);
        return tf*idf;
    }
}
