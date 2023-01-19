package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessing.BoundedPriorityQueue;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessing.DAAT;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessing.MaxScore;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring.BM25;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring.TFIDF;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.TextPreprocessing.TextPreprocessing;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *  QueryProcessor class is responsible for processing a query and returning the top-k results
 */
public class QueryProcessor {
    private final int k;
    private final String relationType;
    public HandleIndex handleIndex;
    public String scoringFunction;
    public String documentProcessor;

    public TextPreprocessing textPreprocessing;

    public QueryProcessor(int nResults, String scoringFunction, String documentProcessor, String relationType, Boolean stopwordsRemoval, Boolean wordsStemming){
        /**
         * Constructor for QueryProcessor
         * @param nResults number of results to return
         * @param scoringFunction scoring function to use
         * @param documentProcessor document processing method to use
         * @param relationType type of relation between query terms
         * @param stopwordsRemoval flag to indicate if stopwords should be removed
         * @param wordsStemming flag to indicate if words should be stemmed
         */
        this.k = nResults;
        this.relationType = relationType;
        this.scoringFunction = scoringFunction;
        this.documentProcessor = documentProcessor;
        this.handleIndex = new HandleIndex();
        this.textPreprocessing = new TextPreprocessing(stopwordsRemoval, wordsStemming);
    }


    /**
     * processQuery method is used to process a query and return top-k results
     * @param query the query to process
     * @return BoundedPriorityQueue of top-k results
     */
    public BoundedPriorityQueue processQuery(String query){
        String[] queryTerms = textPreprocessing.parse(query).split(" "); //Parse the query
        HashMap<String, ArrayList<Posting>> postingLists;

        if (documentProcessor.equals("daat")){
            postingLists = handleIndex.lookup(queryTerms);
        }else{
            postingLists = handleIndex.initialLookUp(queryTerms); //Retrieve candidate postinglists
        }

        return scoreDocuments(queryTerms, postingLists); //Return scores
    }

    /**
     * exitQueryProcessing method is used to close lookup files
     */
    public void exitQueryProcessing(){
        handleIndex.getFileManager().closeLookupFiles();
    }


    /**
     * scoreDocuments method is used to score the documents using the specified scoring function
     * @param queryTerms terms of the query
     * @param postingLists postinglists of the query terms
     * @return BoundedPriorityQueue of top-k results
     */
    public BoundedPriorityQueue scoreDocuments(String[] queryTerms, HashMap<String, ArrayList<Posting>> postingLists){
        if(documentProcessor.equals("daat")){
            DAAT daat = new DAAT(relationType, handleIndex);
            if(scoringFunction.equals("tfidf")) {
                TFIDF tfidf = new TFIDF(postingLists, queryTerms, handleIndex);
                return daat.scoreDocuments(queryTerms, postingLists, tfidf, k);
            }else if(scoringFunction.equals("bm25")){
                BM25 bm25 = new BM25(postingLists, queryTerms, handleIndex, 1.2, 0.75);
                return daat.scoreDocuments(queryTerms, postingLists, bm25, k);
            }
        }else if(documentProcessor.equals("maxscore")){
            MaxScore maxScore = new MaxScore(relationType, handleIndex);
            if(scoringFunction.equals("tfidf")) {
                TFIDF tfidf = new TFIDF(postingLists, queryTerms, handleIndex);
                return maxScore.scoreDocuments(queryTerms, postingLists, tfidf, k);
            }else if(scoringFunction.equals("bm25")){
                BM25 bm25 = new BM25(postingLists, queryTerms, handleIndex, 1.2, 0.75);
                return maxScore.scoreDocuments(queryTerms, postingLists, bm25, k);
            }
        }
        return null;
    }
}
