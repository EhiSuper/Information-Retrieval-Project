package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.TextPreprocessing.TextPreprocessing;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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




    public void processCollection(String file){
        try{
            File myFile = new File(file);
            Scanner myReader = new Scanner(myFile, StandardCharsets.UTF_8);
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeBlock(lexicon, lexicon.sortLexicon());
        invertedIndex.setInvertedIndex(new HashMap<>());
        lexicon.setLexicon(new HashMap<>());
        blockCounter += 1;
        System.gc();

        mergeBlocks();
    }

    public void createIndex(String document, int docNo){
        if (Utils.getMemoryUsage() >= 17 ){
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
                for (Posting posting : invertedIndex.getInvertedIndex().get(term)){
                    myWriterDocIds.write(posting.getDocId() + " ");
                    myWriterFreq.write(posting.getFreq() + " ");
                    offsetDocId += 1;
                    offsetFreq += 1;
                }
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

    public void mergeBlocks(){
        Scanner[] lexiconScanners = new Scanner[blockCounter];
        Scanner[] docIdsScanners = new Scanner[blockCounter];
        Scanner[] freqScanners = new Scanner[blockCounter];
        String[][] terms = new String[blockCounter][];
        boolean[] scannerToRead = new boolean[blockCounter];
        boolean[] scannerFinished = new boolean[blockCounter];
        for(int i = 0; i<blockCounter; i++){
            scannerToRead[i] = true;
            scannerFinished[i] = false;
        }
        int localPostingListLength;
        int postingListLength;
        int offsetDocIds = 0;
        int offsetFreq = 0;
        String minTerm;

        createFinalFiles();
        try{
            FileWriter lexiconWriter = new FileWriter("Data/Output/Lexicon/lexicon.txt");
            FileWriter docIdsWriter = new FileWriter("Data/Output/DocIds/docIds.txt");
            FileWriter freqWriter = new FileWriter("Data/Output/Frequencies/freq.txt");
            initializeScanners(lexiconScanners, docIdsScanners, freqScanners);

            while(true){
                advancePointers(lexiconScanners, scannerToRead, terms, scannerFinished);
                if(!continueMerging(scannerFinished)){break;}
                minTerm = minTerm(terms, scannerFinished);
                postingListLength = 0;
                lexiconWriter.write(minTerm + " " + offsetDocIds + " " + offsetFreq + " ");
                for(int i = 0; i<blockCounter; i++){
                    if(terms[i][0].equals(minTerm)){
                        scannerToRead[i] = true;
                        localPostingListLength = Integer.parseInt(terms[i][3]);
                        postingListLength += localPostingListLength;
                        for(int j = 0; j<localPostingListLength; j++){
                            docIdsWriter.write(docIdsScanners[i].next() + " ");
                            offsetDocIds += 1; // successivamente sarà la dimensione scritta in bytes.
                            freqWriter.write(freqScanners[i].next() + " ");
                            offsetFreq += 1;
                        }
                    }
                    else{
                        scannerToRead[i] = false;
                    }
                }
                lexiconWriter.write(postingListLength + "\n");
            }
            lexiconWriter.close();
            docIdsWriter.close();
            freqWriter.close();
            closeScanners(lexiconScanners, docIdsScanners, freqScanners);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean continueMerging(boolean[] scannerFinished){
        boolean continueMerging;
        continueMerging = false;
        for(int i = 0; i<blockCounter; i++){
            if (!scannerFinished[i]) {
                continueMerging = true;
                break;
            }
        }
        return continueMerging;
    }

    public void advancePointers(Scanner[] lexiconScanners, boolean[] scannerToRead, String[][] terms, boolean[] scannerFinished){
        for(int i = 0; i<blockCounter; i++){
            if(scannerToRead[i]) {
                if(lexiconScanners[i].hasNextLine()){
                    terms[i] = lexiconScanners[i].nextLine().split(" ");
                }
                else{
                    scannerFinished[i] = true;
                }
            }
        }
    }

    public void initializeScanners(Scanner[] lexiconScanners, Scanner[] docIdsScanners, Scanner[] freqScanners) throws IOException {
        for(int i = 0; i<blockCounter; i++){
            lexiconScanners[i] = new Scanner(new File("Data/Output/Lexicon/lexicon" + i + ".txt"));
            docIdsScanners[i] = new Scanner(new File("Data/Output/DocIds/docIds" + i + ".txt"));
            freqScanners[i] = new Scanner(new File("Data/Output/Frequencies/freq" + i + ".txt"));
        }
    }

    public void closeScanners(Scanner[] lexiconScanners, Scanner[] docIdsScanners, Scanner[] freqScanners) throws IOException {
        for (int i = 0; i<blockCounter; i++){
            lexiconScanners[i].close();
            docIdsScanners[i].close();
            freqScanners[i].close();
        }
    }

    public String minTerm(String[][] terms, boolean[] scannerFinished){
        String minTerm = "}";
        for(int i=0; i<blockCounter; i++){
            if(terms[i][0].compareTo(minTerm) < 0 && !scannerFinished[i]){
                minTerm = terms[i][0];
            }
        }
        return minTerm;
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

    public void createFinalFiles(){
        try {
            File myObj = new File("Data/Output/DocIds/docIds.txt");
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
            File myObj = new File("Data/Output/Frequencies/freq.txt");
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
            File myObj = new File("Data/Output/Lexicon/lexicon.txt");
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
