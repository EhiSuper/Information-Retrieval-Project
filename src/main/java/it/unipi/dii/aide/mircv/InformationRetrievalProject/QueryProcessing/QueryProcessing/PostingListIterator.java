package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessing;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring.ScoringFunction;

import java.util.ArrayList;
import java.util.Iterator;

//Custom iterator for traversing the posting lists
public class PostingListIterator implements Iterator<Posting> {
    private final String term;
    private final ArrayList<Posting> PostingList;
    private final ScoringFunction scoringFunction;

    // The current position of the iterator
    private int position;

    // Constructor
    public PostingListIterator(String term, ArrayList<Posting> PostingList, ScoringFunction scoringFunction) {
        this.PostingList = PostingList;
        this.position = 0;
        this.scoringFunction = scoringFunction;
        this.term = term;
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

    public boolean isFinished(){
        //Devo controllare che sia l'ultimo blocco chiamando la loadNextBlock che mi ritorna ->
        //Hashmap con un termine - arrayListPosting
        //Altrimenti vuota
        return position >= PostingList.size();
    }

    // Returns the next element in the iteration
    public Posting next() {
        return PostingList.get(position++);
    }

    // Returns the next element in the iteration
    public Posting nextGEQ(int docId) {
        //Controllare se nel blocco attuale ho già il docID (confronto con l'ultimo DOCID dell'arraylist attuale associata all'iterator)

        //se ce l'ho
        // Iterate through the remaining postings in the list
        while (hasNext()) {
            Posting posting = PostingList.get(position);
            if (posting.getDocId() >= docId) {
                return posting;
            }

            next();
        }

        //Se non ce l'ho -> chiama lookup docID(term, docID) -> Mi ritorna un hashmap -> PS: controlla che ci sia il termine all'interno dell'hashmap ->
        // chiamo la get sul termine -> ArrayList di postings
        //Sostituisci postingList con quella nuova e setta position a 0 e poi riesegui da while(hasNext())

        return null;
    }
}

