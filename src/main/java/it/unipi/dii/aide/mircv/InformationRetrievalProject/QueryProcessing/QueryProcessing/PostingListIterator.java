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
    private int globalCounter;

    // Constructor
    public PostingListIterator(String term, ArrayList<Posting> PostingList, ScoringFunction scoringFunction, HandleIndex handleIndex, String documentProcessor) {
        this.postingList = PostingList;
        this.position = 0;
        this.globalCounter = 0;
        this.scoringFunction = scoringFunction;
        this.term = term;
        this.handleIndex = handleIndex;
        this.documentProcessor = documentProcessor;
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
        if (documentProcessor.equals("maxscore")) {
            if (position >= postingList.size()) {
                if (globalCounter >= this.handleIndex.getLexicon().getLexicon().get(term).postingListLength) {
                    return true;
                } else {
                    int docId = postingList.get(postingList.size() - 1).getDocId();
                    HashMap<String, ArrayList<Posting>> newBlock = handleIndex.loadNextBlock(this.term, docId);
                    postingList = newBlock.get(term);
                    position = 0;
                    return false;
                }
            }
            return false;

        }else{
            return position >= postingList.size();
        }
    }

    // Returns the next element in the iteration
    public Posting next() {
        this.globalCounter++;
        return postingList.get(position++);
    }

    // Returns the next element in the iteration
    public void nextGEQ(int docId) {
        //Controllare se nel blocco attuale ho già il docID (confronto con l'ultimo DOCID dell'arraylist attuale associata all'iterator)
        //Se non ce l'ho -> chiama lookup docID(term, docID) -> Mi ritorna un hashmap -> PS: controlla che ci sia il termine all'interno dell'hashmap ->
        // chiamo la get sul termine -> ArrayList di postings
        //Sostituisci postingList con quella nuova e setta position a 0 e poi riesegui da while(hasNext())

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

