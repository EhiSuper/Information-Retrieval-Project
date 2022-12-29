import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessing.BoundedPriorityQueue;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessing.DAAT;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessing.FinalScore;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring.ScoringFunction;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring.TFIDF;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DAATest {
    @Test
    public void testScoreDocumentsDisjunctive() {
        // Create a test query with two terms: "cat" and "dog"
        String[] queryTerms = {"cat", "dog"};

        // Create a test posting list for the term "cat"
        ArrayList<Posting> catPostingList = new ArrayList<>();
        catPostingList.add(new Posting(1, 1));  // doc 1, freq 1
        catPostingList.add(new Posting(2, 2));  // doc 2, freq 2
        catPostingList.add(new Posting(3, 3));  // doc 3, freq 3
        catPostingList.add(new Posting(4, 4));  // doc 4, freq 4

        // Create a test posting list for the term "dog"
        ArrayList<Posting> dogPostingList = new ArrayList<>();
        dogPostingList.add(new Posting(2, 2));  // doc 2, freq 2
        dogPostingList.add(new Posting(3, 3));  // doc 3, freq 3
        dogPostingList.add(new Posting(4, 4));  // doc 4, freq 4
        dogPostingList.add(new Posting(5, 5));  // doc 5, freq 5

        // Create a test posting lists map
        HashMap<String, ArrayList<Posting>> postingLists = new HashMap<>();
        postingLists.put("cat", catPostingList);
        postingLists.put("dog", dogPostingList);

        // Create a test scoring function
        ScoringFunction scoringFunction = new DummyScoringFunction(postingLists, queryTerms, 3);

        // Create a test DAAT instance
        DAAT daat = new DAAT("disjunctive");

        // Score the documents for the test query
        BoundedPriorityQueue scores = daat.scoreDocuments(queryTerms, postingLists, scoringFunction, 3);

        // Check that the returned queue has the correct size
        assertEquals(3, scores.size());

        PriorityQueue<FinalScore> sc = scores.getQueue();

        // Check that the returned queue has the correct order of documents
        FinalScore first = sc.poll();
        assertEquals(5, first.getKey());
        assertEquals(2.5, first.getValue(), 0.01);

        FinalScore second = sc.poll();
        assertEquals(3, second.getKey());
        assertEquals(3.0, second.getValue(), 0.01);

        FinalScore third = sc.poll();
        assertEquals(4, third.getKey());
        assertEquals(4.0, third.getValue(), 0.01);
    }

    @Test
    public void testScoreDocumentsConjunctive() {
        // Create a test query with two terms: "cat" and "dog"
        String[] queryTerms = {"cat", "dog"};

        // Create a test posting list for the term "cat"
        ArrayList<Posting> catPostingList = new ArrayList<>();
        catPostingList.add(new Posting(1, 1));  // doc 1, freq 1
        catPostingList.add(new Posting(2, 2));  // doc 2, freq 2
        catPostingList.add(new Posting(3, 3));  // doc 3, freq 3
        catPostingList.add(new Posting(4, 4));  // doc 4, freq 4

        // Create a test posting list for the term "dog"
        ArrayList<Posting> dogPostingList = new ArrayList<>();
        dogPostingList.add(new Posting(2, 2));  // doc 2, freq 2
        dogPostingList.add(new Posting(3, 3));  // doc 3, freq 3
        dogPostingList.add(new Posting(4, 4));  // doc 4, freq 4
        dogPostingList.add(new Posting(5, 5));  // doc 5, freq 5

        // Create a test posting lists map
        HashMap<String, ArrayList<Posting>> postingLists = new HashMap<>();
        postingLists.put("cat", catPostingList);
        postingLists.put("dog", dogPostingList);

        // Create a test scoring function
        ScoringFunction scoringFunction = new DummyScoringFunction(postingLists, queryTerms, 3);

        // Create a test DAAT instance
        DAAT daat = new DAAT("conjunctive");

        // Score the documents for the test query
        BoundedPriorityQueue scores = daat.scoreDocuments(queryTerms, postingLists, scoringFunction, 3);

        // Check that the returned queue has the correct size
        assertEquals(3, scores.size());

        PriorityQueue<FinalScore> sc = scores.getQueue();

        // Check that the returned queue has the correct order of documents
        FinalScore first = sc.poll();
        assertEquals(2, first.getKey());
        assertEquals(2, first.getValue(), 0.01);

        FinalScore second = sc.poll();
        assertEquals(3, second.getKey());
        assertEquals(3.0, second.getValue(), 0.01);

        FinalScore third = sc.poll();
        assertEquals(4, third.getKey());
        assertEquals(4.0, third.getValue(), 0.01);
    }
}
