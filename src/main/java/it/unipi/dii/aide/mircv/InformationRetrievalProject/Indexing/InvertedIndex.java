package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;

import java.util.ArrayList;
import java.util.HashMap;

//class representing the inverted index of the document collection.
public class InvertedIndex {

    //the inverted index is a hash map between term and a posting list.
    private HashMap<String, ArrayList<Posting>> invertedIndex;

    public InvertedIndex(){
        this.invertedIndex = new HashMap<>();
    }

    public HashMap<String, ArrayList<Posting>> getInvertedIndex(){ return invertedIndex; }
    public void setInvertedIndex(HashMap<String, ArrayList<Posting>> invertedIndex){ this.invertedIndex = invertedIndex; }

    //function used to add a posting to the inverted index.
    public void addPosting(String term, int docId, int freq){
        if (!invertedIndex.containsKey(term)){
            invertedIndex.put(term, new ArrayList<>());
        }
        invertedIndex.get(term).add(new Posting(docId, freq));
    }
}
