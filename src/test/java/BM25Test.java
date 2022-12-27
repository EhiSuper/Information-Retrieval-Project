import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.DocumentIndex;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring.BM25;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class BM25Test {

    @Test
    public void testDocumentWeight() {
        // Create a test document with a single term: "cat"
        Posting posting = new Posting(1, 2);  // docID 1, frequency 2
        String[] queryTerms = {"cat"};
        long nDocuments = 1000;  // Number of documents in the collection

        // Create a test posting list for the term "cat"
        ArrayList<Posting> postingList = new ArrayList<>();
        postingList.add(posting);
        HashMap<String, ArrayList<Posting>> postingLists = new HashMap<>();
        postingLists.put("cat", postingList);

        // Create a test document information object for the test document
        DocumentIndex.DocumentInformation docInfo = new DocumentIndex.DocumentInformation(1, 10);  // Document size is 100

        // Create a map of document information objects
        HashMap<Integer, DocumentIndex.DocumentInformation> documentInformations = new HashMap<>();
        documentInformations.put(1, docInfo);

        // Create a BM25 instance
        BM25 bm25 = new BM25(postingLists, queryTerms, documentInformations, 1.2, 0.75, nDocuments, 150.0);  // Average document length is 150

        // Test the document weight for the term "cat"
        double actualWeight = bm25.documentWeight("cat", posting);
        assertEquals(5.854029, actualWeight, 0.001);
    }

    @Test
    public void testTermWeight() {
        // Create a test query with a single term: "cat"
        String[] queryTerms = {"cat", "dog", "milk"};

        // Create a BM25 instance
        BM25 bm25 = new BM25(new HashMap<>(), queryTerms, new HashMap<>(), 0, 0, 0, 0);

        // Calculate the expected term weights
        double expectedWeight1 = 1.0 / 3.0;  // Term "cat" occurs once in the query
        double expectedWeight2 = 1.0 / 3.0;  // Term "dog" occurs once in the query

        // Test the term weights
        double actualWeight1 = bm25.termWeight("cat");
        double actualWeight2 = bm25.termWeight("dog");
        assertEquals(expectedWeight1, actualWeight1, 0.001);
        assertEquals(expectedWeight2, actualWeight2, 0.001);
    }
}