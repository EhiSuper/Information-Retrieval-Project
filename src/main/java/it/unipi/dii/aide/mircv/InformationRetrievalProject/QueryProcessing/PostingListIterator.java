package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring.ScoringFunction;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring.TFIDF;

import java.util.ArrayList;
import java.util.Iterator;

//Custom iterator for traversing the posting lists
public class PostingListIterator implements Iterator<Posting> {
    private ArrayList<Posting> PostingList;
    private ScoringFunction scoringFunction;

    // The current position of the iterator
    private int position = 0;

    // Constructor
    public PostingListIterator(ArrayList<Posting> PostingList, ScoringFunction scoringFunction) {
        this.PostingList = PostingList;
        this.scoringFunction = scoringFunction;
    }

    @Override
    public boolean hasNext() {
        return position < PostingList.size();
    }

    public int docid(){
        return PostingList.get(position).getDocId();
    }

    public double score(String term){
        return scoringFunction.score(term,PostingList.get(position));
    }


    // Returns the next element in the iteration
    public Posting next() {
        return PostingList.get(position++);
    }

    // Returns the next element in the iteration
    public Posting nextGEQ(int threshold) {
        // Iterate through the remaining postings in the list
        while (hasNext()) {
            // Get the next posting in the list
            Posting posting = next();

            // If the posting is greater than or equal to the threshold, return it
            if (docid() >= threshold) {
                return posting;
            }
        }
        // If no posting is greater than or equal to the threshold, return null
        return null;
    }
}

