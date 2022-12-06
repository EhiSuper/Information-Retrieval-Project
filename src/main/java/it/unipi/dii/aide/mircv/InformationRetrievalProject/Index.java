package it.unipi.dii.aide.mircv.InformationRetrievalProject;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.TextPreprocessing.TextPreprocessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Index {

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
        try {
            File myFile = new File(file);
            Scanner myReader = new Scanner(myFile, "UTF-8");
            while (myReader.hasNextLine()) {
                String[] line = myReader.nextLine().split("\t",2);
                int docNo = Integer.parseInt(line[0]);
                String document = TextPreprocessing.parse(line[1]);
                invertedIndex.createIndex(document, docNo, lexicon, documentIndex);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("The specified file is not found. Please try again.");
            e.printStackTrace();
        }

        invertedIndex.writeBlock(lexicon.sortLexicon());
        invertedIndex.index = new HashMap<>();
        lexicon.lexicon = new HashMap<>();
        invertedIndex.blockCounter += 1;
        System.gc();

        invertedIndex.mergeBlocks(outputFile);
    }
}
