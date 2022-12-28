package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.*;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans.Compressor;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans.TextReader;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans.VariableByteCode;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.FileManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.*;

public class HandleIndex {
    public FileManager fileManager;
    public Lexicon lexicon;
    public CollectionStatistics collectionStatistics;
    public DocumentIndex documentIndex;


    public HandleIndex(String encodingType){
        this.fileManager = new FileManager();
        this.lexicon = new Lexicon();
        this.documentIndex = new DocumentIndex();

        fileManager.openLookupFiles(encodingType);
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
                fileManager.goToOffset((RandomAccessFile) fileManager.getDocIdsReader().getReader(), offsetDocId);
                fileManager.goToOffset((RandomAccessFile) fileManager.getFreqReader().getReader(), offsetFreq);
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
            lexicon.addInformation(terms[0], Integer.parseInt(terms[1]), Integer.parseInt(terms[2]), Integer.parseInt(terms[3]));
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
