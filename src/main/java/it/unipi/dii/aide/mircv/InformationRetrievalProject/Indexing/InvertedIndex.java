package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class InvertedIndex {
    private HashMap<String, ArrayList<Posting>> invertedIndex;

    public InvertedIndex(){
        this.invertedIndex = new HashMap<>();
    }

    public HashMap<String, ArrayList<Posting>> getInvertedIndex(){ return invertedIndex; }
    public void setInvertedIndex(HashMap<String, ArrayList<Posting>> invertedIndex){ this.invertedIndex = invertedIndex; }

    public void addPosting(String term, int docId, int freq){
        if (!invertedIndex.containsKey(term)){
            invertedIndex.put(term, new ArrayList<>());
        }
        invertedIndex.get(term).add(new Posting(docId, freq));
    }
}
