package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.TextPreprocessing.TextPreprocessing;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Utils;

import java.io.*;
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
    public FileManager fileManager;

    public Index(){
        this.invertedIndex = new InvertedIndex();
        this.lexicon = new Lexicon();
        this.documentIndex = new DocumentIndex();
        this.fileManager = new FileManager();
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

    public FileManager getFileManager() {
        return fileManager;
    }

    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void processCollection(String file){
        try{
            File myFile = new File(file);
            Scanner myReader = new Scanner(myFile, StandardCharsets.UTF_8);
            while (myReader.hasNextLine()) {
                String[] line = myReader.nextLine().split("\t",2); //Read the line and split it (cause the line is composed by (docNo \t document))

                int docNo;
                try{
                    docNo = Integer.parseInt(line[0]); //Get docNo
                }catch (NumberFormatException e){ continue; }
                String document = TextPreprocessing.parse(line[1]); //Get document
                createIndex(document, docNo);
            }
            myReader.close();
        }catch (FileNotFoundException e) {
            System.out.println("The specified file is not found. Please try again.");
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeBlock(lexicon, lexicon.sortLexicon(), documentIndex.sortDocumentIndex());
        invertedIndex.setInvertedIndex(new HashMap<>());
        lexicon.setLexicon(new HashMap<>());
        documentIndex.setDocumentIndex(new HashMap<>());
        blockCounter += 1;
        System.gc();

        mergeBlocks();
    }

    public void createIndex(String document, int docNo){
        if (Utils.getMemoryUsage() >= 75 ){
            writeBlock(lexicon, lexicon.sortLexicon(), documentIndex.sortDocumentIndex());
            lexicon.setLexicon(new HashMap<>());
            invertedIndex.setInvertedIndex(new HashMap<>());
            documentIndex.setDocumentIndex(new HashMap<>());
            blockCounter += 1;
            System.gc();
        }
        String[] terms = document.split(" ");
        HashMap<String, Integer> counter = new HashMap<>();
        for (String term : terms){
            counter.put(term, counter.containsKey(term) ? counter.get(term) + 1 : 1);
        }
        for (String term : counter.keySet()) {
            lexicon.addInformation(term, 0, 0, 0);
            invertedIndex.addPosting(term, docId, counter.get(term));
        }
        documentIndex.addDocument(docId, docNo, terms.length);
        docId += 1;
        System.out.println("Document Processed: " + docId);
    }


    public void writeBlock(Lexicon lexicon, ArrayList<String> sortedTerms, ArrayList<Integer> sortedDocIds){
        fileManager.createBlockFiles(blockCounter);
        fileManager.openBlockFiles(blockCounter);
        for(Integer docId : sortedDocIds){
            fileManager.writeOnFile(fileManager.getMyWriterDocumentIndex(), String.valueOf(docId) + " " + documentIndex.getDocumentIndex().get(docId) + "\n");
            fileManager.writeOnFile(fileManager.getMyWriterDocumentIndexEncoded(), docId);
            fileManager.writeOnFile(fileManager.getMyWriterDocumentIndexEncoded(), documentIndex.getDocumentIndex().get(docId).getDocNo());
            fileManager.writeOnFile(fileManager.getMyWriterDocumentIndexEncoded(), documentIndex.getDocumentIndex().get(docId).getSize());
        }
        for (String term : sortedTerms){
            lexicon.getLexicon().get(term).setPostingListLength(invertedIndex.getInvertedIndex().get(term).size());
            fileManager.writeOnFile(fileManager.getMyWriterLexicon(), term + " " + lexicon.getLexicon().get(term).toString() + "\n");
            for (Posting posting : invertedIndex.getInvertedIndex().get(term)){
                fileManager.writeOnFile(fileManager.getMyWriterDocIds(), posting.getDocId() + " ");
                fileManager.writeOnFile(fileManager.getMyWriterDocIdsEncoded(), posting.getDocId());
                fileManager.writeOnFile(fileManager.getMyWriterFreq(), posting.getFreq() + " ");
                fileManager.writeOnFile(fileManager.getMyWriterFreqEncoded(), posting.getFreq());
            }
        }
        fileManager.closeBlockFiles();
        System.out.println("Successfully wrote to the files.");
    }

    public void mergeBlocks(){
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

        fileManager.createFinalFiles();
        fileManager.openScanners(blockCounter);
        fileManager.openMergeFiles();
        for(int i = 0; i<blockCounter; i++){
            while(fileManager.getDocumentIndexScanners()[i].hasNextLine()){
                fileManager.writeLineOnFile(fileManager.getMyWriterDocumentIndex(), fileManager.readLineFromFile(fileManager.getDocumentIndexScanners()[i]));
                for(int j = 0; j<3; j++) // 3 times because a documentIndex is saved as 3 int.
                {
                    fileManager.writeOnFile(fileManager.getMyWriterDocumentIndexEncoded(), fileManager.readFromFile(fileManager.getDocumentIndexEncodedScanners()[i]));
                }
            }
        }
        while(true){
            advancePointers(fileManager.getLexiconScanners(), scannerToRead, terms, scannerFinished);
            if(!continueMerging(scannerFinished)){break;}
            minTerm = minTerm(terms, scannerFinished);
            postingListLength = 0;
            fileManager.writeOnFile(fileManager.getMyWriterLexicon(), minTerm + " " + offsetDocIds + " " + offsetFreq + " ");
            for(int i = 0; i<blockCounter; i++){
                if(terms[i][0].equals(minTerm)){
                    scannerToRead[i] = true;
                    localPostingListLength = Integer.parseInt(terms[i][3]);
                    postingListLength += localPostingListLength;
                    for(int j = 0; j<localPostingListLength; j++){
                        fileManager.writeOnFile(fileManager.getMyWriterDocIds(),
                                fileManager.readFromFile(fileManager.getDocIdsScanners()[i]) + " ");
                        fileManager.writeOnFile(fileManager.getMyWriterDocIdsEncoded(),
                                fileManager.readFromFile(fileManager.getDocIdsEncodedScanners()[i]));
                        offsetDocIds += 4;
                        fileManager.writeOnFile(fileManager.getMyWriterFreq(),
                                fileManager.readFromFile(fileManager.getFreqScanners()[i]) + " ");
                        fileManager.writeOnFile(fileManager.getMyWriterFreqEncoded(),
                                fileManager.readFromFile(fileManager.getFreqEncodedScanners()[i]));
                        offsetFreq += 4;
                    }
                }
                else{
                    scannerToRead[i] = false;
                }
            }
            fileManager.writeOnFile(fileManager.getMyWriterLexicon(), postingListLength + "\n");
        }
        fileManager.closeMergeFiles();
        fileManager.closeScanners();
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

    public String minTerm(String[][] terms, boolean[] scannerFinished){
        String minTerm = "}";
        for(int i=0; i<blockCounter; i++){
            if(terms[i][0].compareTo(minTerm) < 0 && !scannerFinished[i]){
                minTerm = terms[i][0];
            }
        }
        return minTerm;
    }


}
