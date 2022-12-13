package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class TFIDF extends ScoringFunction{
    private String[] queryTerms;
    private HashMap<String, Double>  idf;
    private HashMap<Integer, Integer> documentsSize;
    private long nDocuments;

    public TFIDF(String[] queryTerms, HashMap<String, ArrayList<Posting>> postingLists, HashMap<Integer, Integer> documentsSize, long nDocuments){
        this.queryTerms = queryTerms;
        this.documentsSize = documentsSize;
        this.nDocuments = nDocuments;

        this.idf = new HashMap<>();
        for (String term : postingLists.keySet()){
            double df = postingLists.get(term).size();
            idf.put(term, Math.log10(nDocuments/df));
        }
    }

    /*
        tf (term frequency) = n° times that the term occures in the document
        df (document frequency) = n° documents in which the term occures
        */
    public double score(String term, Posting posting){
        return getQueryTermWeight(term) * getDocTermWeight(term, posting);
    }

    public double getQueryTermWeight(String term){
        int count = 0;
        for (int i = 0; i < queryTerms.length; i++) {
            if (queryTerms[i].equals(term)) {
                count++;
            }
        }
        return (double) count / queryTerms.length;
    }

    public double getDocTermWeight(String term, Posting posting){
        double tf = (double) posting.getFreq() / documentsSize.get(posting.docId);
        double idf = this.idf.get(term);
        return tf * idf;
    }
}
