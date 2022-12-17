package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Lexicon;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessing.DAAT;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring.BM25;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring.TFIDF;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.TextPreprocessing.TextPreprocessing;

import java.util.*;

public class QueryProcessor {
    private int k;
    private int nDocuments = 8841822; //Needed by BM25 and TFIDF -> Taken from collection statistics
    private double avdl = 2.6; //Needed by BM25 -> Taken from collection statistics
    public FileManager fileManager;
    public Lexicon lexicon;

    public QueryProcessor(int nResults){
        this.k = nResults;
        this.fileManager = new FileManager();
        this.lexicon = new Lexicon();
        fileManager.openLookupFiles();
        obtainLexicon();
    }

    public BoundedPriorityQueue processQuery(String query){
        String[] queryTerms = TextPreprocessing.parse(query).split(" "); //Parse the query
        /*Lookup example
        HashMap<String,ArrayList<Posting>> postingLists = new HashMap<String,ArrayList<Posting>>();
        postingLists.put("beijing", new ArrayList<Posting>(Arrays.asList(new Posting(1,1), new Posting(4,1))));
        postingLists.put("duck", new ArrayList<Posting>(Arrays.asList(new Posting(0,3), new Posting(1,2), new Posting(2,2), new Posting(4,1))));
        postingLists.put("recipe", new ArrayList<Posting>(Arrays.asList(new Posting(2,1), new Posting(3,1), new Posting(4,1))));

        HashMap<Integer,Integer> documentsSize = new HashMap<>(); //Needed by BM25 -> Taken from DocumentIndex foreach DocID in PostingLists
        documentsSize.put(0, 3);
        documentsSize.put(1,3);
        documentsSize.put(2,3);
        documentsSize.put(3,1);
        documentsSize.put(4,3);
         */

        HashMap<String, ArrayList<Posting>> postingLists = lookup(queryTerms); //Retrieve candidate postinglists
        for(String term : queryTerms){
            System.out.println(term + ": " + postingLists.get(term).size());
        }

        TFIDF tfidf = new TFIDF(postingLists, nDocuments);
        //BM25 bm25 = new BM25(postingLists, documentsSize, 1.2, 0.75, nDocuments, avdl);
        DAAT daat = new DAAT();
        BoundedPriorityQueue final_scores = daat.scoreDocuments(queryTerms, postingLists, tfidf, k);

        return final_scores; //Return scores
    }

    public void exitQueryProcessing(){
        fileManager.closeLookupFiles();
    }





    public void obtainLexicon(){
        String line;
        String[] terms;
        while(fileManager.getLexiconScanners()[0].hasNextLine()){
            line = fileManager.readLineFromFile(fileManager.getLexiconScanners()[0]);
            terms = line.split(" ");
            lexicon.addInformation(terms[0], Integer.parseInt(terms[1]), Integer.parseInt(terms[2]), Integer.parseInt(terms[3]));
        }
    }

    public HashMap<String, ArrayList<Posting>> lookup(String[] queryTerms){
        int offsetDocId = 0;
        int offsetFreq = 0;
        int postingListLength = 0;
        int docId = 0;
        int freq = 0;
        HashMap<String, ArrayList<Posting>> postingLists = new HashMap<>();
        for(String term : queryTerms){
            try {
                offsetDocId = lexicon.getLexicon().get(term).getPostingListOffsetDocId();
                offsetFreq = lexicon.getLexicon().get(term).getPostingListOffsetFreq();
                postingListLength = lexicon.getLexicon().get(term).getPostingListLength();
                fileManager.goToOffset(fileManager.getDocIdsEncodedScanners()[0], offsetDocId);
                fileManager.goToOffset(fileManager.getFreqEncodedScanners()[0], offsetFreq);
                for (int i = 0; i < postingListLength; i++) {
                    docId = fileManager.readFromFile(fileManager.getDocIdsEncodedScanners()[0]);
                    freq = fileManager.readFromFile(fileManager.getFreqEncodedScanners()[0]);
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
}
