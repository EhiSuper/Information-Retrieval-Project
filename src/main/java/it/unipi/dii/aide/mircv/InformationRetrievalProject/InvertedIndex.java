package it.unipi.dii.aide.mircv.InformationRetrievalProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class InvertedIndex {
    public static int docId = 0;
    public int blockCounter = 0;
    public HashMap<String, ArrayList<Posting>> index = new HashMap<>();

    public void createIndex(String document, int docNo, Lexicon lexicon, DocumentIndex documentIndex){
        if (getMemoryUsage() >= 30 ){
            writeBlock(lexicon.sortLexicon());
            lexicon.lexicon = new HashMap<>();
            index = new HashMap<>();
            blockCounter += 1;
            System.gc();
        }
        String[] terms = document.split(" ");
        InvertedIndex.docId += 1;
        HashMap<String, Integer> counter = new HashMap<>();
        for (String term : terms){
            counter.put(term, counter.containsKey(term) ? counter.get(term) + 1 : 1);
        }
        for (String term : counter.keySet()) {
            lexicon.addTerm(term);
            addPosting(term, docId, counter.get(term));
        }
    }

    public void addPosting(String term, int docId, int freq){
        if (!index.containsKey(term)){
            index.put(term, new ArrayList<>());
        }
        index.get(term).add(new Posting(docId, freq));
    }

    public float getMemoryUsage(){
        float totalMemory = Runtime.getRuntime().totalMemory();
        float memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        return (memoryUsage / totalMemory) * 100;
    }

    public void writeBlock(ArrayList<String> sortedTerms){
        try {
            File myObj = new File("Data/Output/block" + blockCounter + ".txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred during the creation of the output file.");
            e.printStackTrace();
        }

        try {
            FileWriter myWriter = new FileWriter("Data/Output/block" + blockCounter + ".txt", StandardCharsets.UTF_8);
            for (String term : sortedTerms){
                myWriter.write(term + "" + index.get(term) + "\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void mergeBlocks(String outputFile){

    }
}
