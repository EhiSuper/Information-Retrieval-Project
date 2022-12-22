package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Lexicon {

    private HashMap<String, PostingListInformation> lexicon = new HashMap<>();

    public class PostingListInformation{
        public int postingListOffsetDocId;
        public int postingListOffsetFreq;
        public int postingListLength;

        public PostingListInformation(int postingListOffsetDocId, int postingListOffsetFreq, int postingListLength) {
            this.postingListOffsetDocId = postingListOffsetDocId;
            this.postingListOffsetFreq = postingListOffsetFreq;
            this.postingListLength = postingListLength;
        }

        public int getPostingListOffsetDocId() {
            return postingListOffsetDocId;
        }

        public void setPostingListOffset(int postingListOffset) {
            this.postingListOffsetDocId = postingListOffset;
        }

        public int getPostingListLength() {
            return postingListLength;
        }

        public void setPostingListLength(int postingListLength) {
            this.postingListLength = postingListLength;
        }

        public int getPostingListOffsetFreq() { return postingListOffsetFreq; }

        public void setPostingListOffsetFreq(int postingListOffsetFreq) {
            this.postingListOffsetFreq = postingListOffsetFreq;
        }

        @Override
        public String toString() {
            return postingListOffsetDocId + " " + postingListOffsetFreq + " " + postingListLength;
        }
    }

    public void setLexicon(HashMap<String, PostingListInformation> lexicon){ this.lexicon = lexicon; }
    public HashMap<String, PostingListInformation> getLexicon(){ return lexicon; }

    public void addInformation(String term, int offsetDocIds, int offsetFreq, int postingListLength){
        if(!lexicon.containsKey(term)){
            lexicon.put(term, new PostingListInformation(offsetDocIds, offsetFreq, postingListLength));
        }
    }

    public ArrayList<String> sortLexicon(){
        ArrayList<String> sortedTerms = new ArrayList<>(lexicon.keySet());
        Collections.sort(sortedTerms);
        return sortedTerms;
    }



}
