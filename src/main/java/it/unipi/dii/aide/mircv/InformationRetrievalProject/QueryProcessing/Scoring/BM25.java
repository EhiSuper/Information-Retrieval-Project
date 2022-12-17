package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;

import java.util.ArrayList;
import java.util.HashMap;

public class BM25 extends ScoringFunction{
    private final HashMap<Integer, Integer> documentsSize;
    private final double k1;
    private final double b;
    private final double avgDocumentLength;

    public BM25(HashMap<String, ArrayList<Posting>> postingLists, HashMap<Integer, Integer> documentsSize, double k1, double b, long nDocuments, double avgDocumentLength){
        super(postingLists, nDocuments);

        this.k1 = k1;
        this.b = b;
        this.avgDocumentLength = avgDocumentLength;
        this.documentsSize = documentsSize;
    }

    public double score(String term, Posting posting){
        double tf = posting.getFreq();
        double denominator = k1 * ((1-b) + b * ((double) documentsSize.get(posting.getDocId()) / avgDocumentLength)) + tf;
        double idf = this.idf.get(term);

        return (tf * idf) / denominator;
    }
}
