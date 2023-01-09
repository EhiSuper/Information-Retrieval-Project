package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans.Reader;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans.TextReader;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans.TextWriter;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.FileManager;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.TextPreprocessing.TextPreprocessing;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

//class that performs the main operations during the indexing phase.
public class Index {

    public int docId = 0;
    public int blockCounter = 0;

    public InvertedIndex invertedIndex;
    public Lexicon lexicon;
    public DocumentIndex documentIndex;
    public FileManager fileManager;
    public CollectionStatistics collectionStatistics;
    public String encodingType;
    public int postingListBlockLength;

    public TextPreprocessing textPreprocessing;

    public Index(TextPreprocessing textPreprocessing){
        this.invertedIndex = new InvertedIndex();
        this.lexicon = new Lexicon();
        this.documentIndex = new DocumentIndex();
        this.fileManager = new FileManager();
        this.collectionStatistics = new CollectionStatistics(0, 0, 0, 0);

        this.textPreprocessing = textPreprocessing;
        this.postingListBlockLength = 10;
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public int getBlockCounter() {
        return blockCounter;
    }

    public void setBlockCounter(int blockCounter) {
        this.blockCounter = blockCounter;
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

    public CollectionStatistics getCollectionStatistics() {
        return collectionStatistics;
    }

    public void setCollectionStatistics(CollectionStatistics collectionStatistics) {
        this.collectionStatistics = collectionStatistics;
    }

    public int getPostingListBlockLength() {
        return postingListBlockLength;
    }

    public void setPostingListBlockLength(int postingListBlockLength) {
        this.postingListBlockLength = postingListBlockLength;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    //function that taken the compressed document collection, preprocess and elaborate every document.
    public void processCollection(String file, String type){
        setEncodingType(type);
        try {
            // Open the compressed file
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));

            // Create a zip input stream from the compressed file
            ZipInputStream zin = new ZipInputStream(in);

            // Read the first entry in the zip file
            ZipEntry entry = zin.getNextEntry();

            // Create a reader for reading the uncompressed data
            BufferedReader reader = new BufferedReader(new InputStreamReader(zin, "UTF-8"));

            // Read data from the zip file and process it
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split("\t",2); //Read the line and split it (cause the line is composed by (docNo \t document))

                int docNo;
                try{
                    docNo = Integer.parseInt(columns[0]); //Get docNo
                }catch (NumberFormatException e){ continue; }
                //preprocess the document
                String document = textPreprocessing.parse(columns[1]); //Get document
                //elaborate the document
                createIndex(document, docNo);
            }

            // Close the zip input stream
            zin.closeEntry();
            zin.close();
            reader.close();

            // Close the input stream
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //after finishing the documents saves block that is in the main memory.
        writeBlock(lexicon, lexicon.sortLexicon(), documentIndex.sortDocumentIndex());
        invertedIndex.setInvertedIndex(new HashMap<>());
        lexicon.setLexicon(new HashMap<>());
        documentIndex.setDocumentIndex(new HashMap<>());
        blockCounter += 1;
        System.gc();

        mergeBlocks();
        saveCollectionStatistics();
    }

    //function that taken a document and a doc no elaborate the document to create the index.
    public void createIndex(String document, int docNo){
        //if the available memory is not enough to process the document saves the current block into the disk.
        if (Utils.getMemoryUsage() >= 75 ){
            writeBlock(lexicon, lexicon.sortLexicon(), documentIndex.sortDocumentIndex()); //writes the current block to disk
            lexicon.setLexicon(new HashMap<>());
            invertedIndex.setInvertedIndex(new HashMap<>());
            documentIndex.setDocumentIndex(new HashMap<>());
            blockCounter += 1;
            System.gc(); //calls the garbage collector to force to free memory.
        }
        String[] terms = document.split(" ");
        HashMap<String, Integer> counter = new HashMap<>();
        //associate to every term the term count in the document.
        for (String term : terms){
            counter.put(term, counter.containsKey(term) ? counter.get(term) + 1 : 1);
        }
        // update the index information for every term.
        for (String term : counter.keySet()) {
            lexicon.addInformation(term, 0, 0, 0, 0, 0);
            invertedIndex.addPosting(term, docId, counter.get(term));
            collectionStatistics.setPostings(collectionStatistics.getPostings() + 1);
        }
        documentIndex.addDocument(docId, docNo, terms.length);
        docId += 1;
        collectionStatistics.setDocuments(collectionStatistics.getDocuments() + 1);
        collectionStatistics.setAvgDocumentLength(collectionStatistics.getAvgDocumentLength() + terms.length);
    }

    //function that writes the current block that is in memory to the disk.
    public void writeBlock(Lexicon lexicon, ArrayList<String> sortedTerms, ArrayList<Integer> sortedDocIds){
        fileManager.openBlockFiles(blockCounter, encodingType);
        //saves the document index.
        for(Integer docId : sortedDocIds){
            fileManager.writeOnFile(fileManager.getMyWriterDocumentIndex(), docId);
            fileManager.writeOnFile(fileManager.getMyWriterDocumentIndex(), documentIndex.documentIndex.get(docId).getDocNo());
            fileManager.writeOnFile(fileManager.getMyWriterDocumentIndex(), documentIndex.documentIndex.get(docId).getSize());
        }
        //saves the lexicon and the docIds and frequencies in the relative files.
        for (String term : sortedTerms){
            lexicon.getLexicon().get(term).setPostingListLength(invertedIndex.getInvertedIndex().get(term).size());
            fileManager.writeLineOnFile((TextWriter) fileManager.getMyWriterLexicon(), term + " " + lexicon.getLexicon().get(term).toString() + "\n");
            for (Posting posting : invertedIndex.getInvertedIndex().get(term)){
                fileManager.writeOnFile(fileManager.getMyWriterDocIds(), posting.getDocId());
                fileManager.writeOnFile(fileManager.getMyWriterFreq(), posting.getFreq());
            }
        }
        fileManager.closeBlockFiles();
        System.out.println("Successfully wrote to the files.");
    }

    //function that implements the merging phase of the SPIMI algorithm.
    public void mergeBlocks(){
        String[][] terms = new String[blockCounter][]; //terms read from the current pointer in the lexicon
        boolean[] scannerToRead = new boolean[blockCounter]; //array of boolean to indicate if a scanner to a file need to be read.
        boolean[] scannerFinished = new boolean[blockCounter]; //array of boolean to indicate if a scanner has scanned al the block file.
        int postingBlockCounter;
        for(int i = 0; i<blockCounter; i++){
            scannerToRead[i] = true;
            scannerFinished[i] = false;
        }
        int localPostingListLength;
        int postingListLength;
        int offsetDocIds = 0;
        int offsetFreq = 0;
        int offsetLastDocIds = 0;
        int offsetSkipPointers = 0;
        int docId = 0;
        String minTerm;

        fileManager.openScanners(blockCounter, encodingType); //open the scanners of the block files
        fileManager.openMergeFiles(encodingType); //open the final marge files
        //in this for we do the merging of the document index first. It reads a number to check if the file is ended.
        //Every row of the document index is saved as 3 integers.
        for (int i = 0; i < blockCounter; i++) {
            int number = fileManager.readFromFile(fileManager.getDocumentIndexScanners()[i]); //read the first integer
            while(number != -1){ //continue until the file is not ended
                fileManager.writeOnFile(fileManager.getMyWriterDocumentIndex(), number); //write on the final file the read integer
                for (int j = 0; j < 2; j++) // reads other 2 times an integer from the current block file and writes it to the final file
                {
                    fileManager.writeOnFile(fileManager.getMyWriterDocumentIndex(), fileManager.readFromFile(fileManager.getDocumentIndexScanners()[i]));
                }
                number = fileManager.readFromFile(fileManager.getDocumentIndexScanners()[i]);
            }
        }
        //here the merging loop of the lexicon and of the documentIds and frequencies files is performed.
        while(true){
            //it read from the lexicon files that needs to be read.
            advancePointers(fileManager.getLexiconScanners(), scannerToRead, terms, scannerFinished);
            //it checks if the merging phase is finished.
            if(!continueMerging(scannerFinished)){break;}
            //it gets the minimum term.
            minTerm = minTerm(terms, scannerFinished);
            postingListLength = 0;
            postingBlockCounter = 0;
            //it writes the term information to the lexicon in text format.
            fileManager.writeLineOnFile((TextWriter) fileManager.getMyWriterLexicon(), minTerm + " "
                    + offsetDocIds + " " + offsetFreq + " " + offsetLastDocIds + " " + offsetSkipPointers + " ");
            //for every block if the current pointed term is the minimum term we perform merging.
            for(int i = 0; i<blockCounter; i++){
                if(terms[i][0].equals(minTerm)){
                    scannerToRead[i] = true; //we are using the information so the next time we need to read new information.
                    //obtain the posting list length of the current block
                    localPostingListLength = Integer.parseInt(terms[i][3]);
                    //update the global posting list length
                    postingListLength += localPostingListLength;
                    for(int j = 0; j<localPostingListLength; j++){
                        //if it is at the start of the posting list block it saves the skip pointers for the block
                        if(postingBlockCounter == 0){
                            //it saves in the skiPointers file 2 integers: one for the docId offset and one for the frequency offset.
                            offsetSkipPointers += fileManager.writeOnFile(fileManager.getMyWriterSkipPointers(), offsetDocIds);
                            offsetSkipPointers += fileManager.writeOnFile(fileManager.getMyWriterSkipPointers(), offsetFreq);
                        }

                        docId = fileManager.readFromFile(fileManager.getDocIdsScanners()[i]);
                        //it saves in the final files the information arriving from the block files.
                        offsetDocIds += fileManager.writeOnFile(fileManager.getMyWriterDocIds(), docId);
                        offsetFreq += fileManager.writeOnFile(fileManager.getMyWriterFreq(),
                                fileManager.readFromFile(fileManager.getFreqScanners()[i]));

                        postingBlockCounter += 1;
                        //if we are at the end of the posting list block we save the current docId in the lastDocId file.
                        if (postingBlockCounter == postingListBlockLength){
                            offsetLastDocIds += fileManager.writeOnFile(fileManager.getMyWriterLastDocIds(), docId);
                            postingBlockCounter = 0;
                        }
                    }
                }
                else{
                    //if the current term of the lexicon pointer is not the min term the next time we do not need to read
                    //from the scanner again
                    scannerToRead[i] = false;
                }
            }
            //at the end of the merging for a specific term we save the docId of the last posting of the posting list of that term.
            if(postingBlockCounter != postingListBlockLength){
                offsetLastDocIds += fileManager.writeOnFile(fileManager.getMyWriterLastDocIds(), docId);
            }
            //we conclude the lexicon merging adding the global posting list length information.
            fileManager.writeLineOnFile((TextWriter) fileManager.getMyWriterLexicon(), postingListLength + "\n");
        }
        fileManager.closeMergeFiles();
        fileManager.closeScanners();
    }

    //function to check if we need to continue the merging phase.
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

    //function that advance the right pointers during the merge phase.
    public void advancePointers(Reader[] lexiconScanners, boolean[] scannerToRead, String[][] terms, boolean[] scannerFinished){
        for(int i = 0; i<blockCounter; i++){
            if(scannerToRead[i]) {
                if(fileManager.hasNextLine((TextReader) fileManager.getLexiconScanners()[i])){
                    terms[i] = fileManager.readLineFromFile((TextReader) lexiconScanners[i]).split(" ");
                }
                else{
                    scannerFinished[i] = true;
                }
            }
        }
    }

    //function that given the terms currently pointed during the merging phase returns the minimum term.
    public String minTerm(String[][] terms, boolean[] scannerFinished){
        String minTerm = "}";
        for(int i=0; i<blockCounter; i++){
            if(terms[i][0].compareTo(minTerm) < 0 && !scannerFinished[i]){
                minTerm = terms[i][0];
            }
        }
        return minTerm;
    }

    //function that saves the collection statistics.
    public void saveCollectionStatistics(){
        collectionStatistics.setAvgDocumentLength(collectionStatistics.getAvgDocumentLength() / collectionStatistics.getDocuments());
        try{
            FileWriter writer = new FileWriter("Data/Output/CollectionStatistics/collectionStatistics.txt");
            writer.write(collectionStatistics.getDocuments() + " "
                    + collectionStatistics.getAvgDocumentLength() + " " + collectionStatistics.getPostings());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
