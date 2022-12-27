import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring.TFIDF;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TFIDFTest {
    @Test
    public void testDocumentWeight() {
        // Create a test document with two terms: "cat" and "dog"
        Posting posting = new Posting(1, 2);  // docID 1, frequency 2
        String[] queryTerms = {"cat", "dog"};
        long nDocuments = 1000;  // Number of documents in the collection

        // Create a test posting list for the term "cat"
        ArrayList<Posting> postingList = new ArrayList<>();
        postingList.add(posting);
        HashMap<String, ArrayList<Posting>> postingLists = new HashMap<>();
        postingLists.put("cat", postingList);

        // Create a TFIDF instance
        TFIDF tfidf = new TFIDF(postingLists, queryTerms, nDocuments);

        // Calculate the expected document weight for the term "cat"
        double tf = 1 + Math.log(posting.getFreq());  // Term frequency
        double idf = Math.log(nDocuments / (double) postingLists.get("cat").size());  // Inverse document frequency
        double expectedWeight = tf * idf;

        // Test the document weight for the term "cat"
        double actualWeight = tfidf.documentWeight("cat", posting);
        assertEquals(expectedWeight, actualWeight, 0.001);
    }

    @Test
    public void testTermWeight() {
        // Create a test query with two terms: "cat" and "dog"
        String[] queryTerms = {"cat", "dog"};

        // Create a TFIDF instance
        TFIDF tfidf = new TFIDF(new HashMap<>(), queryTerms, 1000);  // Number of documents in the collection is not relevant here

        // Calculate the expected term weights
        double expectedWeight1 = 1.0 / 2.0;  // Term "cat" occurs once in the query
        double expectedWeight2 = 1.0 / 2.0;  // Term "dog" occurs once in the query

        // Test the term weights
        double actualWeight1 = tfidf.termWeight("cat");
        double actualWeight2 = tfidf.termWeight("dog");
        assertEquals(expectedWeight1, actualWeight1, 0.001);
        assertEquals(expectedWeight2, actualWeight2, 0.001);
    }

    @Test
    public void testScore() {
        // Create a test document with two terms: "cat" and "dog"
        Posting posting = new Posting(1, 2);  // docID 1, frequency 2

        // Create a test query with two terms: "cat" and "dog"
        String[] queryTerms = {"cat", "dog"};

        // Create a test posting list for the term "cat"
        ArrayList<Posting> postingList = new ArrayList<>();
        postingList.add(posting);
        HashMap<String, ArrayList<Posting>> postingLists = new HashMap<>();
        postingLists.put("cat", postingList);

        // Create a TFIDF instance
        TFIDF tfidf = new TFIDF(postingLists, queryTerms, 1000);  // Number of documents in the collection is not relevant here

        // Calculate the expected score for the term "cat" in the document
        double termWeight = 1.0 / 2.0;  // term "cat" occurs once in the query
        double documentWeight = 1 + Math.log(posting.getFreq());  // term frequency
        double idf = Math.log(1000 / (double) postingLists.get("cat").size());  // Inverse document frequency
        documentWeight *= idf;
        double expectedScore = termWeight * documentWeight;

        // Test the score for the term "cat" in the document
        double actualScore = tfidf.score("cat", posting);
        assertEquals(expectedScore, actualScore, 0.001);

    }
}
