package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessing;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.HandleIndex;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring.ScoringFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

//Custom iterator for traversing the posting lists
public class PostingListIterator implements Iterator<Posting> {
    private final String term;
    private ArrayList<Posting> postingList;
    private final ScoringFunction scoringFunction;
    private final HandleIndex handleIndex;
    private final String documentProcessor;

    // The current position of the iterator
    private int position;
    private boolean isFinished;

    // Constructor
    public PostingListIterator(String term, ArrayList<Posting> PostingList, ScoringFunction scoringFunction, HandleIndex handleIndex, String documentProcessor) {
        this.postingList = PostingList;
        this.position = 0;
        this.scoringFunction = scoringFunction;
        this.term = term;
        this.handleIndex = handleIndex;
        this.documentProcessor = documentProcessor;
        this.isFinished = false;
    }

    @Override
    public boolean hasNext() {
        return position < postingList.size();
    }

    public int docid(){
        return postingList.get(position).getDocId();
    }

    public double score(String term){
        return scoringFunction.score(term,postingList.get(position));
    }

    public boolean isFinished(){
        if (this.postingList.size() == 0) {
            return true;
        }
        //check if it's the last block or there are other blocks to load and then it's not "really" finished
        if (documentProcessor.equals("maxscore")) {
            if(this.isFinished) {
                return true;
            }
            if (position >= postingList.size()) {
                int docId = postingList.get(postingList.size() - 1).getDocId(); //Gets the docID of the last posting to use it to load the nextBlock (using loadNextBlock)
                HashMap<String, ArrayList<Posting>> newBlock = handleIndex.loadNextBlock(this.term, docId);
                if(!newBlock.containsKey(term)){
                    this.isFinished = true;
                    return true;
                }
                postingList = newBlock.get(term);
                position = 0;
                return false;
            }
            return false;

        }else{
            return position >= postingList.size();
        }
    }

    // Returns the next element in the iteration
    public Posting next() {
        return postingList.get(position++);
    }

    public void nextGEQ(int docId) {
        //Load another block if the docID searched is not in the currentBlock
        if (docId > postingList.get(postingList.size()-1).getDocId()){
            HashMap<String,ArrayList<Posting>> newBlock =  handleIndex.lookupDocId(this.term, docId);
            if (newBlock.containsKey(term)){
                this.postingList = newBlock.get(term);
                this.position = 0;
            }else{
                return;
            }
        }

        while (hasNext()) {
            Posting posting = postingList.get(position);
            if (posting.getDocId() >= docId) {
                return;
            }

            next();
        }
    }

    public ArrayList<Posting> getPostingList() {
        return postingList;
    }

    public String getTerm() {
        return term;
    }
}

