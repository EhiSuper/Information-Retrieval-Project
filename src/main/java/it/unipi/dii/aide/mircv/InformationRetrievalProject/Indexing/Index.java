package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.TextPreprocessing.TextPreprocessing;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Index {

    public int docId = 0;
    public int blockCounter = 0;

    public InvertedIndex invertedIndex;
    public Lexicon lexicon;
    public DocumentIndex documentIndex;

    public Index(){
        this.invertedIndex = new InvertedIndex();
        this.lexicon = new Lexicon();
        this.documentIndex = new DocumentIndex();
    }

    public InvertedIndex getInvertedIndex() {
        return invertedIndex;
    }

    public void setInvertedIndex(InvertedIndex invertedIndex) {
        this.invertedIndex = invertedIndex;
    }

    public Lexicon getLexicon() {
        return lexicon;
    }

    public void setLexicon(Lexicon lexicon) {
        this.lexicon = lexicon;
    }

    public DocumentIndex getDocumentIndex() {
        return documentIndex;
    }

    public void setDocumentIndex(DocumentIndex documentIndex) {
        this.documentIndex = documentIndex;
    }




    public void processCollection(String file, String outputFile){
        try{
            File myFile = new File(file);
            Scanner myReader = new Scanner(myFile, "UTF-8");
            while (myReader.hasNextLine()) {
                String[] line = myReader.nextLine().split("\t",2); //Read the line and split it (cause the line is composed by (docNo \t document))

                int docNo = Integer.parseInt(line[0]); //Get docNo
                String document = TextPreprocessing.parse(line[1]); //Get document
                //invertedIndex.createIndex(document, docNo, lexicon, documentIndex);
                createIndex(document, docNo);
            }
            myReader.close();
        }catch (FileNotFoundException e) {
            System.out.println("The specified file is not found. Please try again.");
            e.printStackTrace();
        }

        writeBlock(lexicon, lexicon.sortLexicon());
        invertedIndex.setInvertedIndex(new HashMap<>());
        lexicon.setLexicon(new HashMap<>());
        blockCounter += 1;
        System.gc();

        mergeBlocks(outputFile);
    }

    public void createIndex(String document, int docNo){
        if (Utils.getMemoryUsage() >= 75 ){
            writeBlock(lexicon, lexicon.sortLexicon());
            lexicon.setLexicon(new HashMap<>());
            invertedIndex.setInvertedIndex(new HashMap<>());
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
            invertedIndex.addPosting(term, docId, counter.get(term));
        }
        documentIndex.addDocument(docId, docNo, terms.length);
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
                lexicon.getLexicon().get(term).setPostingListLength(invertedIndex.getInvertedIndex().get(term).size());
                lexicon.getLexicon().get(term).setPostingListOffset(offsetDocId);
                lexicon.getLexicon().get(term).setPostingListOffsetFreq(offsetFreq);
                myWriterLexicon.write(term + " " + lexicon.getLexicon().get(term).toString() + "\n");
                myWriterDocIds.write(term + " ");
                myWriterFreq.write(term + " ");
                for (Posting posting : invertedIndex.getInvertedIndex().get(term)){
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
