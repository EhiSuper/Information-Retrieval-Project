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

public class Index {

    public int docId = 0;
    public int blockCounter = 0;

    public InvertedIndex invertedIndex;
    public Lexicon lexicon;
    public DocumentIndex documentIndex;
    public FileManager fileManager;
    public CollectionStatistics collectionStatistics;
    public String encodingType;

    public Index(){
        this.invertedIndex = new InvertedIndex();
        this.lexicon = new Lexicon();
        this.documentIndex = new DocumentIndex();
        this.fileManager = new FileManager();
        this.collectionStatistics = new CollectionStatistics(0, 0, 0, 0);
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

    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

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
                String document = TextPreprocessing.parse(columns[1]); //Get document
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

        writeBlock(lexicon, lexicon.sortLexicon(), documentIndex.sortDocumentIndex());
        invertedIndex.setInvertedIndex(new HashMap<>());
        lexicon.setLexicon(new HashMap<>());
        documentIndex.setDocumentIndex(new HashMap<>());
        blockCounter += 1;
        System.gc();

        mergeBlocks();
        saveCollectionStatistics();
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
            collectionStatistics.setPostings(collectionStatistics.getPostings() + 1);
        }
        documentIndex.addDocument(docId, docNo, terms.length);
        docId += 1;
        collectionStatistics.setDocuments(collectionStatistics.getDocuments() + 1);
        collectionStatistics.setAvgDocumentLength(collectionStatistics.getAvgDocumentLength() + terms.length);
    }


    public void writeBlock(Lexicon lexicon, ArrayList<String> sortedTerms, ArrayList<Integer> sortedDocIds){
        fileManager.openBlockFiles(blockCounter, encodingType);
        for(Integer docId : sortedDocIds){
            fileManager.writeOnFile(fileManager.getMyWriterDocumentIndex(), docId);
            fileManager.writeOnFile(fileManager.getMyWriterDocumentIndex(), documentIndex.documentIndex.get(docId).getDocNo());
            fileManager.writeOnFile(fileManager.getMyWriterDocumentIndex(), documentIndex.documentIndex.get(docId).getSize());
        }
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

        fileManager.openScanners(blockCounter, encodingType);
        fileManager.openMergeFiles(encodingType);
        for (int i = 0; i < blockCounter; i++) {
            int number = fileManager.readFromFile(fileManager.getDocumentIndexScanners()[i]);
            while(number != -1){
                fileManager.writeOnFile(fileManager.getMyWriterDocumentIndex(), number);
                for (int j = 0; j < 2; j++) // 2 times because a documentIndex is saved as 3 int.
                {
                    fileManager.writeOnFile(fileManager.getMyWriterDocumentIndex(), fileManager.readFromFile(fileManager.getDocumentIndexScanners()[i]));
                }
                number = fileManager.readFromFile(fileManager.getDocumentIndexScanners()[i]);
            }
        }
        while(true){
            advancePointers(fileManager.getLexiconScanners(), scannerToRead, terms, scannerFinished);
            if(!continueMerging(scannerFinished)){break;}
            minTerm = minTerm(terms, scannerFinished);
            postingListLength = 0;
            fileManager.writeLineOnFile((TextWriter) fileManager.getMyWriterLexicon(), minTerm + " " + offsetDocIds + " " + offsetFreq + " ");
            for(int i = 0; i<blockCounter; i++){
                if(terms[i][0].equals(minTerm)){
                    scannerToRead[i] = true;
                    localPostingListLength = Integer.parseInt(terms[i][3]);
                    postingListLength += localPostingListLength;
                    for(int j = 0; j<localPostingListLength; j++){
                        offsetDocIds += fileManager.writeOnFile(fileManager.getMyWriterDocIds(),
                                fileManager.readFromFile(fileManager.getDocIdsScanners()[i]));

                        offsetFreq += fileManager.writeOnFile(fileManager.getMyWriterFreq(),
                                fileManager.readFromFile(fileManager.getFreqScanners()[i]));
                    }
                }
                else{
                    scannerToRead[i] = false;
                }
            }
            fileManager.writeLineOnFile((TextWriter) fileManager.getMyWriterLexicon(), postingListLength + "\n");
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

    public String minTerm(String[][] terms, boolean[] scannerFinished){
        String minTerm = "}";
        for(int i=0; i<blockCounter; i++){
            if(terms[i][0].compareTo(minTerm) < 0 && !scannerFinished[i]){
                minTerm = terms[i][0];
            }
        }
        return minTerm;
    }

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
