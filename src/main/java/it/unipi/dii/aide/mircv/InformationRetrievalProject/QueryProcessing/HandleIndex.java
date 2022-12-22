package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class HandleIndex {
    public FileManager fileManager;
    public Lexicon lexicon;
    public CollectionStatistics collectionStatistics;
    public Compressor compressor;
    public DocumentIndex documentIndex;


    public HandleIndex(){
        this.fileManager = new FileManager();
        this.lexicon = new Lexicon();
        this.compressor = new Compressor();
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
                fileManager.goToOffset(fileManager.getDocIdsCompressedScanners()[0], offsetDocId);
                fileManager.goToOffset(fileManager.getFreqCompressedScanners()[0], offsetFreq);
                for (int i = 0; i < postingListLength; i++) {
                    docId = compressor.readBytes(fileManager.getDocIdsCompressedScanners()[0]);
                    freq = compressor.readBytes(fileManager.getFreqCompressedScanners()[0]);
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
        while (fileManager.getLexiconScanners()[0].hasNextLine()) {
            line = fileManager.readLineFromFile(fileManager.getLexiconScanners()[0]);
            terms = line.split(" ");
            lexicon.addInformation(terms[0], Integer.parseInt(terms[1]), Integer.parseInt(terms[2]), Integer.parseInt(terms[3]));
        }
    }

    public void obtainDocumentIndex(){
        int docId;
        int docNo;
        int size;
        for(int i = 0; i<collectionStatistics.getDocuments(); i++){
            docId = compressor.readBytes(fileManager.getDocumentIndexCompressedScanners()[0]);
            docNo = compressor.readBytes(fileManager.getDocumentIndexCompressedScanners()[0]);
            size = compressor.readBytes(fileManager.getDocumentIndexCompressedScanners()[0]);
            documentIndex.addDocument(docId, docNo, size);
        }
    }

    public void obtainCollectionStatistics(){
        String line;
        String[] terms;
        try{
            Scanner scanner = new Scanner(new File("Data/Output/CollectionStatistics/collectionStatistics.txt"));
            line = fileManager.readLineFromFile(scanner);
            terms = line.split(" ");
            collectionStatistics = new CollectionStatistics(Integer.parseInt(terms[0]), Double.parseDouble(terms[1]),
                    2, lexicon.getLexicon().size());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public CollectionStatistics getCollectionStatistics() {
        return collectionStatistics;
    }

    public DocumentIndex getDocumentIndex() {
        return documentIndex;
    }

    public Compressor getCompressor() {
        return compressor;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public Lexicon getLexicon() {
        return lexicon;
    }


    public void setCompressor(Compressor compressor) {
        this.compressor = compressor;
    }

    public void setLexicon(Lexicon lexicon) {
        this.lexicon = lexicon;
    }
}
