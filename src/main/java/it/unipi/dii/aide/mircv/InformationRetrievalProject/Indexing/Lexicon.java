package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

//class that represents the lexicon of the document collection.
public class Lexicon {

    //the lexicon is represented as a hashmap  between a term and the posting list information.
    private HashMap<String, PostingListInformation> lexicon = new HashMap<>();

    public class PostingListInformation{
        public int postingListOffsetDocId;
        public int postingListOffsetFreq;
        public int postingListOffsetLastDocIds;
        public int postingListOffsetSkipPointers;
        public int postingListLength;

        public PostingListInformation(int postingListOffsetDocId, int postingListOffsetFreq, int postingListOffsetLastDocIds, int postingListOffsetSkipPointers, int postingListLength) {
            this.postingListOffsetDocId = postingListOffsetDocId;
            this.postingListOffsetFreq = postingListOffsetFreq;
            this.postingListOffsetLastDocIds = postingListOffsetLastDocIds;
            this.postingListOffsetSkipPointers = postingListOffsetSkipPointers;
            this.postingListLength = postingListLength;
        }

        public int getPostingListOffsetDocId() {
            return postingListOffsetDocId;
        }

        public void setPostingListOffsetDocId(int postingListOffsetDocId) {
            this.postingListOffsetDocId = postingListOffsetDocId;
        }

        public int getPostingListOffsetFreq() {
            return postingListOffsetFreq;
        }

        public void setPostingListOffsetFreq(int postingListOffsetFreq) {
            this.postingListOffsetFreq = postingListOffsetFreq;
        }

        public int getPostingListOffsetLastDocIds() {
            return postingListOffsetLastDocIds;
        }

        public void setPostingListOffsetLastDocIds(int postingListOffsetLastDocIds) {
            this.postingListOffsetLastDocIds = postingListOffsetLastDocIds;
        }

        public int getPostingListOffsetSkipPointers() {
            return postingListOffsetSkipPointers;
        }

        public void setPostingListOffsetSkipPointers(int postingListOffsetSkipPointers) {
            this.postingListOffsetSkipPointers = postingListOffsetSkipPointers;
        }

        public int getPostingListLength() {
            return postingListLength;
        }

        public void setPostingListLength(int postingListLength) {
            this.postingListLength = postingListLength;
        }

        @Override
        public String toString() {
            return postingListOffsetDocId + " " + postingListOffsetFreq + " " + postingListLength;
        }
    }

    public void setLexicon(HashMap<String, PostingListInformation> lexicon){ this.lexicon = lexicon; }
    public HashMap<String, PostingListInformation> getLexicon(){ return lexicon; }

    public void addInformation(String term, int offsetDocIds, int offsetFreq, int offsetLastDocIds, int offsetSkipPointers, int postingListLength){
        if(!lexicon.containsKey(term)){
            lexicon.put(term, new PostingListInformation(offsetDocIds, offsetFreq, offsetLastDocIds, offsetSkipPointers, postingListLength));
        }
    }

    //function that returns a list of sorted terms taken from the lexicon.
    public ArrayList<String> sortLexicon(){
        ArrayList<String> sortedTerms = new ArrayList<>(lexicon.keySet());
        Collections.sort(sortedTerms);
        return sortedTerms;
    }
}
