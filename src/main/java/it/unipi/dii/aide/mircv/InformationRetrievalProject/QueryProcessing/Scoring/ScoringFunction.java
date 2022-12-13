package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;

public abstract class ScoringFunction {
    /*
            tf (term frequency) = n° times that the term occures in the document
            df (document frequency) = n° documents in which the term occures
            */
    public abstract double score(String term, Posting posting);
}
