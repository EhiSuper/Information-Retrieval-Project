package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BM25{
    private long nDocuments;
    private long avgDocumentLength;
    private double k1;
    private double b;

    public BM25(double k1, double b, long nDocuments, long avgDocumentLength){
        this.nDocuments = nDocuments;
        this.avgDocumentLength = avgDocumentLength;
        this.k1 = k1;
        this.b = b;
    }

    public double score(String term, Posting posting){
        return 0;
    }
}
