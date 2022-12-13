package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring.TFIDF;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.TextPreprocessing.TextPreprocessing;

import java.util.*;

public class QueryProcessor {
    private int k;
    private int nDocuments = 3;
    private double avdl = 3;
    private TFIDF tfidf;

    public QueryProcessor(int nResults, int nDocuments, double avdl){
        this.k = nResults;
        this.nDocuments = nDocuments;
        this.avdl = avdl;
    }

    public PriorityQueue<FinalScore> processQuery(String query){
        String[] queryTerms = TextPreprocessing.parse(query).split(" "); //Parse the query

        //Lookup example
        HashMap<String,ArrayList<Posting>> postingLists = new HashMap<String,ArrayList<Posting>>();
        postingLists.put("beijing", new ArrayList<Posting>(Arrays.asList(new Posting(1,2), new Posting(4,1))));
        postingLists.put("duck", new ArrayList<Posting>(Arrays.asList(new Posting(0,4), new Posting(1,2), new Posting(2,2), new Posting(4,1))));
        postingLists.put("recipe", new ArrayList<Posting>(Arrays.asList(new Posting(2,1), new Posting(3,1), new Posting(4,1))));

        HashMap<Integer,Integer> documentsSize = new HashMap<>();
        documentsSize.put(0, 4);
        documentsSize.put(1,4);
        documentsSize.put(2,3);
        documentsSize.put(3,1);
        documentsSize.put(4,3);


        //HashMap<String, ArrayList<Posting>> postingLists = lookup(queryTerms); //Retrieve candidate postinglists
        PriorityQueue<FinalScore> final_scores = score(queryTerms, postingLists, documentsSize); //Rank documents

        return final_scores; //Return scores
    }


    public HashMap<String,ArrayList<Posting>> lookup(String[] queryTerms){
        return null;

    }


    //Get the minimum docID over all the posting lists
    public int minDocId(ArrayList<PostingListIterator> postingIterators){
        int minDocId = nDocuments; //N° docs in the collection (maximum docID)
        for(PostingListIterator postingIterator : postingIterators){
            if(postingIterator.docid() < minDocId){
                minDocId = postingIterator.docid();
            }
        }
        return minDocId;
    }

    //Check if the query processing is finished, i.e all the posting lists has been fully processed
    public boolean notFinished(ArrayList<PostingListIterator> postingIterators){
        boolean finished = true;
        for(PostingListIterator postingIterator : postingIterators){
            if(postingIterator.hasNext()){
                finished = false;
            }
        }
        return finished;
    }


    public PriorityQueue<FinalScore> score(String[] queryTerms, HashMap<String, ArrayList<Posting>> postingLists, HashMap<Integer, Integer> documentsSize){
        PriorityQueue<FinalScore> scores = new PriorityQueue<>(k);
        TFIDF tfidf = new TFIDF(queryTerms, postingLists, documentsSize, nDocuments);

        ArrayList<PostingListIterator> postingIterators = new ArrayList<>(); //List of iterators
        //Create an iterator foreach posting list related to each query term (like a pointer)
        for(String term : queryTerms){
            postingIterators.add(new PostingListIterator(postingLists.get(term), tfidf));
        }

        while(!notFinished(postingIterators)){
            int minDocid = minDocId(postingIterators); //Get minimum docID over all posting lists
            double score = 0.0;
            for(int i = 0; i < queryTerms.length; i++){ //Foreach posting list check if the current posting correspond to the minimum docID
                PostingListIterator term_iterator = postingIterators.get(i);
                if(term_iterator.docid() == minDocid){ //If the current posting has the docID equal to the minimum docID
                    score += term_iterator.score(queryTerms[i]); //Compute the score using the posting score function
                    term_iterator.next(); //Go to the next element of the posting list
                }

            }
            scores.add(new FinalScore(minDocid,score)); //Add the final score to the priorityQueue

        }

        return scores; //Return the top K scores
    }
}
