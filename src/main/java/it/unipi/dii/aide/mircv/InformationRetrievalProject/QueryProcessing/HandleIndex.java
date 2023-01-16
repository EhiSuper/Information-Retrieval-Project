package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.*;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans.RandomByteReader;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans.TextReader;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.FileManager;

import java.util.*;

//class that handles the index files during query processing.
public class HandleIndex {
    public FileManager fileManager;
    public Lexicon lexicon;
    public CollectionStatistics collectionStatistics;
    public DocumentIndex documentIndex;
    public int postingListBlockLength;


    public HandleIndex(){
        this.fileManager = new FileManager();
        this.lexicon = new Lexicon();
        this.documentIndex = new DocumentIndex();

        fileManager.openLookupFiles();
        fileManager.openObtainFiles();
        obtainLexicon(lexicon, fileManager);
        obtainCollectionStatistics();
        obtainDocumentIndex();
        fileManager.closeObtainFiles();
        postingListBlockLength = 500;
    }

    //function that given a query returns a hashmap between the term and the relative whole posting list.
    public HashMap<String, ArrayList<Posting>> lookup(String[] queryTerms){
        int offsetDocId;
        int offsetFreq;
        int postingListLength;
        int docId;
        int freq;
        HashMap<String, ArrayList<Posting>> postingLists = new HashMap<>();
        Set<String> queryTermsSet = new HashSet<>(List.of(queryTerms));
        for(String term : queryTermsSet){
            try {
                //it gets the offset information from the lexicon to read in the docIds and freq files.
                offsetDocId = lexicon.getLexicon().get(term).getPostingListOffsetDocId();
                offsetFreq = lexicon.getLexicon().get(term).getPostingListOffsetFreq();
                postingListLength = lexicon.getLexicon().get(term).getPostingListLength();
                fileManager.goToOffset((RandomByteReader) fileManager.getDocIdsReader(), offsetDocId);
                fileManager.goToOffset((RandomByteReader) fileManager.getFreqReader(), offsetFreq);
                for (int i = 0; i < postingListLength; i++) {
                    //for the length of the posting list it reads docId and frequency from the relative files
                    //and adds a posting to the relative posting list.
                    docId = fileManager.readFromFile(fileManager.getDocIdsReader());
                    freq = fileManager.readFromFile(fileManager.getFreqReader());
                    addPosting(postingLists, term, docId, freq);
                }
            }catch (NullPointerException e){
                postingLists.put(term, new ArrayList<>());
            }
        }
        return postingLists;
    }

    //lookup function that given a query returns a hashmap between the term and the first block on the relative posting list
    public HashMap<String, ArrayList<Posting>> initialLookUp(String[] queryTerms){
        int offsetDocId;
        int offsetFreq;
        int postingListLength;
        int docId;
        int freq;
        int postingToRead;
        HashMap<String, ArrayList<Posting>> postingLists = new HashMap<>();
        Set<String> queryTermsSet = new HashSet<>(List.of(queryTerms));
        for(String term : queryTermsSet){
            try {
                //it gets the offset information from the lexicon to read in the docIds and freq files.
                offsetDocId = lexicon.getLexicon().get(term).getPostingListOffsetDocId();
                offsetFreq = lexicon.getLexicon().get(term).getPostingListOffsetFreq();
                postingListLength = lexicon.getLexicon().get(term).getPostingListLength();
                fileManager.goToOffset((RandomByteReader) fileManager.getDocIdsReader(), offsetDocId);
                fileManager.goToOffset((RandomByteReader) fileManager.getFreqReader(), offsetFreq);
                postingToRead = Math.min(postingListLength, postingListBlockLength);
                for (int i = 0; i < postingToRead; i++) {
                    //for the number of posting to read it reads docId and frequency from the relative files
                    //and adds a posting to the relative posting list.
                    docId = fileManager.readFromFile(fileManager.getDocIdsReader());
                    freq = fileManager.readFromFile(fileManager.getFreqReader());
                    addPosting(postingLists, term, docId, freq);
                }
            }catch (NullPointerException e){
                postingLists.put(term, new ArrayList<>());
            }
        }
        return postingLists;
    }

    //function that given a term and a doc id returns a hashmap between the term and the posting list block containing
    //that docId.
    public HashMap<String, ArrayList<Posting>> lookupDocId(String term, int docId){
        HashMap<String, ArrayList<Posting>> postingLists = new HashMap<>();
        int[] skipPointers = new int[4];
        int newDocId;
        int newFreq;
        int postingToRead;
        int postingListLength;
        int numberOfBlocks;
        searchBlock(skipPointers, term, docId); //search the block to read.
        //if a posting with a docId greater or equal to the one passed as argument doesn't exist the returned hash map is empty.
        if(skipPointers[3] == 0) return postingLists;
        fileManager.goToOffset((RandomByteReader) fileManager.getDocIdsReader(), skipPointers[0]);
        fileManager.goToOffset((RandomByteReader) fileManager.getFreqReader(), skipPointers[1]);
        postingListLength = lexicon.getLexicon().get(term).getPostingListLength();
        //skiPointers[2] == 0 if the docId is not contained in the last block of the posting list, 1 otherwise.
        if(skipPointers[2] == 0){
            postingToRead = Math.min(postingListLength, postingListBlockLength);
        }
        else {
            //if the posting containing the docId is the last block of the posting list it computes the right
            //length to read.
            numberOfBlocks = (lexicon.getLexicon().get(term).getPostingListLength() / postingListBlockLength) + 1;
            postingToRead = postingListLength - (numberOfBlocks - 1) * postingListBlockLength;
        }
        for(int i = 0; i<postingToRead; i++){
            //for the number of posting to read it reads docId and frequency from the relative files
            //and adds a posting to the relative posting list.
            newDocId = fileManager.readFromFile(fileManager.getDocIdsReader());
            newFreq = fileManager.readFromFile(fileManager.getFreqReader());
            addPosting(postingLists, term, newDocId, newFreq);
        }
        return postingLists;
    }

    //function that given a term and a docId returns the skip pointers to the relative block of the term's posting list
    //containing that docId.
    public void searchBlock(int[] skipPointers, String term, int docId){
        ArrayList<Integer> pointersDocIds = new ArrayList<>();
        ArrayList<Integer> pointerFreq = new ArrayList<>();
        ArrayList<Integer> docIds = new ArrayList<>();
        int offsetLastDocIds;
        int offsetSkipPointers;
        //it gets the number of blocks of the term's posting list
        int blockNumber = (lexicon.getLexicon().get(term).getPostingListLength() / postingListBlockLength) + 1;
        offsetLastDocIds = lexicon.getLexicon().get(term).getPostingListOffsetLastDocIds();
        offsetSkipPointers = lexicon.getLexicon().get(term).getPostingListOffsetSkipPointers();
        fileManager.goToOffset((RandomByteReader) fileManager.getLastDocIdsReader(), offsetLastDocIds);
        fileManager.goToOffset((RandomByteReader) fileManager.getSkipPointersReader(), offsetSkipPointers);
        for(int i = 0; i<blockNumber; i++){
            //for the number of blocks it reads and add to the relative array the posting list block information.
            docIds.add(fileManager.readFromFile(fileManager.getLastDocIdsReader()));
            pointersDocIds.add(fileManager.readFromFile(fileManager.getSkipPointersReader()));
            pointerFreq.add(fileManager.readFromFile(fileManager.getSkipPointersReader()));
        }
        for(int i = 0; i<docIds.size(); i++){
            //it searches for the block containing the posting with the docId grater or equal to the one passed as argument.
            //if a posting with a docId greater than the docId passed is not present in the posting list, skipPointers[3] is put equal 0.
            if(i == 0){
                if(docId < docIds.get(i)){
                    skipPointers[0] = pointersDocIds.get(i);
                    skipPointers[1] = pointerFreq.get(i);
                    skipPointers[2] = 0;
                    skipPointers[3] = 1;
                    return;
                }
            }
            else{
                if(docId <= docIds.get(i) && docId > docIds.get(i-1)){
                    skipPointers[0] = pointersDocIds.get(i);
                    skipPointers[1] = pointerFreq.get(i);
                    skipPointers[2] = 0;
                    if(i == (docIds.size() - 1)){
                        skipPointers[2] = 1; //this is the last block of the posting list
                    }
                    skipPointers[3] = 1;
                    return;
                }
                else{
                    skipPointers[3] = 0; //no posting with a docId greater or equal to the one passed is found.
                }
            }
        }
    }

    //function to add a posting to the posting list of a term.
    public void addPosting(HashMap<String, ArrayList<Posting>> postingLists, String term, int docId, int freq){
        if (!postingLists.containsKey(term)){
            postingLists.put(term, new ArrayList<>());
        }
        postingLists.get(term).add(new Posting(docId, freq));
    }

    //function the load in main memory the lexicon from the disk.
    public static void obtainLexicon(Lexicon lexicon, FileManager fileManager) {
        String line;
        String[] terms;
        while (fileManager.hasNextLine((TextReader) fileManager.getLexiconReader())) {
            line = fileManager.readLineFromFile((TextReader) fileManager.getLexiconReader());
            terms = line.split(" ");
            lexicon.addInformation(terms[0], Integer.parseInt(terms[1]), Integer.parseInt(terms[2]),
                    Integer.parseInt(terms[3]), Integer.parseInt(terms[4]), Integer.parseInt(terms[5]), Float.parseFloat(terms[6]));
        }
    }

    //function to load in main memory the document index from the disk
    public void obtainDocumentIndex(){
        int docId;
        int docNo;
        int size;
        for(int i = 0; i<collectionStatistics.getDocuments(); i++){
            //for the number of document it reads 3 integer from the disk file and add a new document information to the document index
            docId = fileManager.readFromFile(fileManager.getDocumentIndexReader());
            docNo = fileManager.readFromFile(fileManager.getDocumentIndexReader());
            size = fileManager.readFromFile(fileManager.getDocumentIndexReader());
            documentIndex.addDocument(docId, docNo, size);
        }
    }

    //function to load in main memory the collection statistics from the disk.
    public void obtainCollectionStatistics(){
        String[] terms;
        terms = fileManager.readLineFromFile((TextReader) fileManager.getCollectionStatisticsReader()).split(" ");
        collectionStatistics = new CollectionStatistics(Integer.parseInt(terms[0]), Double.parseDouble(terms[1]),
                lexicon.getLexicon().size(), Integer.parseInt(terms[2]));
    }

    public CollectionStatistics getCollectionStatistics() {
        return collectionStatistics;
    }

    public DocumentIndex getDocumentIndex() {
        return documentIndex;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public Lexicon getLexicon() {
        return lexicon;
    }

    public void setLexicon(Lexicon lexicon) {
        this.lexicon = lexicon;
    }
}
