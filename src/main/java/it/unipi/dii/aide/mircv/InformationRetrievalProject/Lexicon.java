package it.unipi.dii.aide.mircv.InformationRetrievalProject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Lexicon {

    public HashMap<String, PostingListInformation> lexicon = new HashMap<>();

    class PostingListInformation{
        public int postingListOffset;
        public int postingListLength;

        public PostingListInformation(){

        }

        public int getPostingListOffset() {
            return postingListOffset;
        }

        public void setPostingListOffset(int postingListOffset) {
            this.postingListOffset = postingListOffset;
        }

        public int getPostingListLength() {
            return postingListLength;
        }

        public void setPostingListLength(int postingListLength) {
            this.postingListLength = postingListLength;
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
