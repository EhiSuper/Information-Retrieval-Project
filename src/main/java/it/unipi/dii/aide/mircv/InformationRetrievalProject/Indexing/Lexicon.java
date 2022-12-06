package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Lexicon {

    public HashMap<String, PostingListInformation> lexicon = new HashMap<>();

    class PostingListInformation{
        public int postingListOffsetDocId;
        public int postingListOffsetFreq;
        public int postingListLength;

        public PostingListInformation(){

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
            return "[ " + postingListOffsetDocId + " " + postingListOffsetFreq + " " + postingListLength + " ]";
        }
    }

    public void addTerm(String term){
        if(!lexicon.containsKey(term)){
            lexicon.put(term, new PostingListInformation());
        }
    }

    public ArrayList<String> sortLexicon(){
        ArrayList<String> sortedTerms = new ArrayList<>(lexicon.keySet());
        Collections.sort(sortedTerms);
        return sortedTerms;
    }
}
