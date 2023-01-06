package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.*;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans.RandomAccessByteReader;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans.TextReader;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.FileManager;

import java.lang.reflect.Array;
import java.util.*;

public class HandleIndex {
    public FileManager fileManager;
    public Lexicon lexicon;
    public CollectionStatistics collectionStatistics;
    public DocumentIndex documentIndex;


    public HandleIndex(){
        this.fileManager = new FileManager();
        this.lexicon = new Lexicon();
        this.documentIndex = new DocumentIndex();

        fileManager.openLookupFiles();
        obtainLexicon(lexicon, fileManager);
        obtainCollectionStatistics();
        obtainDocumentIndex();
    }

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
                offsetDocId = lexicon.getLexicon().get(term).getPostingListOffsetDocId();
                offsetFreq = lexicon.getLexicon().get(term).getPostingListOffsetFreq();
                postingListLength = lexicon.getLexicon().get(term).getPostingListLength();
                fileManager.goToOffset((RandomAccessByteReader) fileManager.getDocIdsReader(), offsetDocId);
                fileManager.goToOffset((RandomAccessByteReader) fileManager.getFreqReader(), offsetFreq);
                for (int i = 0; i < postingListLength; i++) {
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

    public HashMap<String, ArrayList<Posting>> initialLookUp(String queryTerms){
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
                offsetDocId = lexicon.getLexicon().get(term).getPostingListOffsetDocId();
                offsetFreq = lexicon.getLexicon().get(term).getPostingListOffsetFreq();
                postingListLength = lexicon.getLexicon().get(term).getPostingListLength();
                fileManager.goToOffset((RandomAccessByteReader) fileManager.getDocIdsReader(), offsetDocId);
                fileManager.goToOffset((RandomAccessByteReader) fileManager.getFreqReader(), offsetFreq);
                if(postingListLength < 10){
                    postingToRead = postingListLength;
                }
                else{
                    postingToRead = 10;
                }
                for (int i = 0; i < postingToRead; i++) {
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

    public HashMap<String, ArrayList<Posting>> lookupDocId(String term, int docId){
        HashMap<String, ArrayList<Posting>> postingLists = new HashMap<>();
        int[] skipPointers = new int[3];
        int newDocId;
        int newFreq;
        int postingToRead;
        int postingListLength;
        int numberOfBlocks;
        searchBlock(skipPointers, term, docId);
        fileManager.goToOffset((RandomAccessByteReader) fileManager.getDocIdsReader(), skipPointers[0]);
        fileManager.goToOffset((RandomAccessByteReader) fileManager.getFreqReader(), skipPointers[1]);
        postingListLength = lexicon.getLexicon().get(term).getPostingListLength();
        if(skipPointers[2] == 0){
            if(postingListLength < 10){
                postingToRead = postingListLength;
            }
            else{
                postingToRead = 10;
            }
        }
        else {

            numberOfBlocks = (lexicon.getLexicon().get(term).getPostingListLength() / 10) + 1;
            postingToRead = postingListLength - (numberOfBlocks - 1) * 10;
        }
        for(int i = 0; i<postingToRead; i++){
            newDocId = fileManager.readFromFile(fileManager.getDocIdsReader());
            newFreq = fileManager.readFromFile(fileManager.getFreqReader());
            addPosting(postingLists, term, newDocId, newFreq);
        }
        return postingLists;
    }

    public void searchBlock(int[] skipPointers, String term, int docId){
        ArrayList<Integer> pointersDocIds = new ArrayList<>();
        ArrayList<Integer> pointerFreq = new ArrayList<>();
        ArrayList<Integer> docIds = new ArrayList<>();
        int offsetLastDocIds;
        int offsetSkipPointers;
        int blockNumber = (lexicon.getLexicon().get(term).getPostingListLength() / 10) + 1;
        offsetLastDocIds = lexicon.getLexicon().get(term).getPostingListOffsetLastDocIds();
        offsetSkipPointers = lexicon.getLexicon().get(term).getPostingListOffsetSkipPointers();
        fileManager.goToOffset((RandomAccessByteReader) fileManager.getLastDocIdsReader(), offsetLastDocIds);
        fileManager.goToOffset((RandomAccessByteReader) fileManager.getSkipPointersReader(), offsetSkipPointers);
        for(int i = 0; i<blockNumber; i++){
            docIds.add(fileManager.readFromFile(fileManager.getLastDocIdsReader()));
            pointersDocIds.add(fileManager.readFromFile(fileManager.getSkipPointersReader()));
            pointerFreq.add(fileManager.readFromFile(fileManager.getSkipPointersReader()));
        }
        for(int i = 0; i<docIds.size(); i++){
            if(i == 0){
                if(docId < docIds.get(i)){
                    skipPointers[0] = pointersDocIds.get(i);
                    skipPointers[1] = pointerFreq.get(i);
                    skipPointers[2] = 0;
                    return;
                }
            }
            else{
                if(docId <= docIds.get(i) && docId > docIds.get(i-1)){
                    skipPointers[0] = pointersDocIds.get(i);
                    skipPointers[1] = pointerFreq.get(i);
                    skipPointers[2] = 0;
                    if(i == (docIds.size() - 1)){
                        skipPointers[2] = 1;
                    }
                    return;
                }
            }
        }
    }

    public void addPosting(HashMap<String, ArrayList<Posting>> postingLists, String term, int docId, int freq){
        if (!postingLists.containsKey(term)){
            postingLists.put(term, new ArrayList<>());
        }
        postingLists.get(term).add(new Posting(docId, freq));
    }


    public static void obtainLexicon(Lexicon lexicon, FileManager fileManager) {
        String line;
        String[] terms;
        while (fileManager.hasNextLine((TextReader) fileManager.getLexiconReader())) {
            line = fileManager.readLineFromFile((TextReader) fileManager.getLexiconReader());
            terms = line.split(" ");
            lexicon.addInformation(terms[0], Integer.parseInt(terms[1]), Integer.parseInt(terms[2]), Integer.parseInt(terms[3]), Integer.parseInt(terms[4]), Integer.parseInt(terms[5]));
        }
    }

    public void obtainDocumentIndex(){
        int docId;
        int docNo;
        int size;
        for(int i = 0; i<collectionStatistics.getDocuments(); i++){
            docId = fileManager.readFromFile(fileManager.getDocumentIndexReader());
            docNo = fileManager.readFromFile(fileManager.getDocumentIndexReader());
            size = fileManager.readFromFile(fileManager.getDocumentIndexReader());
            documentIndex.addDocument(docId, docNo, size);
        }
    }

    public void obtainCollectionStatistics(){
        String[] terms;
        terms = fileManager.readLineFromFile((TextReader) fileManager.getCollectionStatisticsReader()).split(" ");
        collectionStatistics = new CollectionStatistics(Integer.parseInt(terms[0]), Double.parseDouble(terms[1]),
                lexicon.getLexicon().size(), Integer.valueOf(terms[2]));
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
