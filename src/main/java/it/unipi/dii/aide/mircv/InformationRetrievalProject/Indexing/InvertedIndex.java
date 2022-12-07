package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class InvertedIndex {
    public int docId = 0;
    public int blockCounter = 0;
    public HashMap<String, ArrayList<Posting>> index = new HashMap<>();

    public void createIndex(String document, int docNo, Lexicon lexicon, DocumentIndex documentIndex){
        if (getMemoryUsage() >= 75 ){
            writeBlock(lexicon, lexicon.sortLexicon());
            lexicon.lexicon = new HashMap<>();
            index = new HashMap<>();
            blockCounter += 1;
            System.gc();
        }
        String[] terms = document.split(" ");
        docId += 1;
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

    public void writeBlock(Lexicon lexicon, ArrayList<String> sortedTerms){
        createFileBlock();
        int offsetDocId = 0;
        int offsetFreq = 0;
        try {
            FileWriter myWriterDocIds = new FileWriter("Data/Output/DocIds/docIds" + blockCounter + ".txt");
            FileWriter myWriterFreq = new FileWriter("Data/Output/Frequencies/freq" + blockCounter + ".txt");
            FileWriter myWriterLexicon = new FileWriter("Data/Output/Lexicon/lexicon" + blockCounter + ".txt");
            for (String term : sortedTerms){
                lexicon.lexicon.get(term).setPostingListLength(index.get(term).size());
                lexicon.lexicon.get(term).setPostingListOffset(offsetDocId);
                lexicon.lexicon.get(term).setPostingListOffsetFreq(offsetFreq);
                myWriterLexicon.write(term + " " + lexicon.lexicon.get(term).toString() + "\n");
                myWriterDocIds.write(term + " ");
                myWriterFreq.write(term + " ");
                for (Posting posting : index.get(term)){
                    myWriterDocIds.write(String.valueOf(posting.getDocId()) + " ");
                    myWriterFreq.write(String.valueOf(posting.getFreq()) + " ");
                    offsetDocId += 1;
                    offsetFreq += 1;
                }
                myWriterDocIds.write("\n");
                myWriterFreq.write("\n");
            }
            myWriterDocIds.close();
            myWriterFreq.close();
            myWriterLexicon.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void mergeBlocks(String outputFile){

    }

    public void createFileBlock (){
        try {
            File myObj = new File("Data/Output/DocIds/docIds" + blockCounter + ".txt");
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
            File myObj = new File("Data/Output/Frequencies/freq" + blockCounter + ".txt");
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
            File myObj = new File("Data/Output/Lexicon/lexicon" + blockCounter + ".txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred during the creation of the output file.");
            e.printStackTrace();
        }
    }
}
