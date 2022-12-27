import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessing.BoundedPriorityQueue;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessing.FinalScore;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessing.MaxScore;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring.ScoringFunction;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

public class MaxScoreTest {
    @Test
    public void testScoreDocuments() {
        // Set up test data
        String[] queryTerms = {"term1", "term2", "term3"};
        HashMap<String, ArrayList<Posting>> postingLists = new HashMap<>();
        postingLists.put("term1", new ArrayList<>(Arrays.asList(new Posting(1, 5), new Posting(2, 3), new Posting(3,3))));
        postingLists.put("term2", new ArrayList<>(Arrays.asList(new Posting(1, 2), new Posting(2, 4), new Posting(3, 1))));
        postingLists.put("term3", new ArrayList<>(Arrays.asList(new Posting(2, 1), new Posting(3, 2))));
        ScoringFunction scoringFunction = new DummyScoringFunction(postingLists, queryTerms, 3);
        int k = 2;

        // Call method under test
        MaxScore maxScore = new MaxScore();
        BoundedPriorityQueue scores = maxScore.scoreDocuments(queryTerms, postingLists, scoringFunction, k);

        PriorityQueue<FinalScore> sc = scores.getQueue();

        // Check that the returned queue has the correct order of documents
        assertEquals(2, scores.size());
        assertEquals(1, sc.poll().getKey());
        assertEquals(2, sc.poll().getKey());
    }

    @Test
    public void testCheckDocumentUpperBound() {
        // Set up test data
        String[] queryTerms = {"term1", "term2", "term3"};
        HashMap<String, ArrayList<Posting>> postingLists = new HashMap<>();
        postingLists.put("term1", new ArrayList<>(Arrays.asList(new Posting(1, 5), new Posting(2, 3), new Posting(3,1))));
        postingLists.put("term2", new ArrayList<>(Arrays.asList(new Posting(1, 2), new Posting(2, 3), new Posting(3, 1))));
        postingLists.put("term3", new ArrayList<>(Arrays.asList(new Posting(2, 1), new Posting(3, 2))));
        ScoringFunction scoringFunction = new DummyScoringFunction(postingLists, queryTerms, 3);
        int k = 2;

        // Call method under test
        MaxScore maxScore = new MaxScore();
        BoundedPriorityQueue scores = maxScore.scoreDocuments(queryTerms, postingLists, scoringFunction, k);

        PriorityQueue<FinalScore> sc = scores.getQueue();

        // Check that the returned queue has the correct order of documents
        assertEquals(2, scores.size());
        assertEquals(1, sc.poll().getKey());
        assertEquals(2, sc.poll().getKey());
    }


    @Test
    public void testGEQNull() {
        // Set up test data
        String[] queryTerms = {"term1", "term2", "term3"};
        HashMap<String, ArrayList<Posting>> postingLists = new HashMap<>();
        postingLists.put("term1", new ArrayList<>(Arrays.asList(new Posting(1, 5), new Posting(2, 3), new Posting(3,3))));
        postingLists.put("term2", new ArrayList<>(Arrays.asList(new Posting(1, 2), new Posting(2, 3), new Posting(3, 1))));
        postingLists.put("term3", new ArrayList<>(Arrays.asList(new Posting(2, 1))));
        ScoringFunction scoringFunction = new DummyScoringFunction(postingLists, queryTerms, 3);
        int k = 2;

        // Call method under test
        MaxScore maxScore = new MaxScore();
        BoundedPriorityQueue scores = maxScore.scoreDocuments(queryTerms, postingLists, scoringFunction, k);

        PriorityQueue<FinalScore> sc = scores.getQueue();

        // Check that the returned queue has the correct order of documents
        assertEquals(2, scores.size());
        assertEquals(1, sc.poll().getKey());
        assertEquals(2, sc.poll().getKey());
    }

    @Test
    public void testGEQSkipping() {
        // Set up test data
        String[] queryTerms = {"term1", "term2", "term3"};
        HashMap<String, ArrayList<Posting>> postingLists = new HashMap<>();
        postingLists.put("term1", new ArrayList<>(Arrays.asList(new Posting(1, 5), new Posting(2, 3), new Posting(4,3))));
        postingLists.put("term2", new ArrayList<>(Arrays.asList(new Posting(1, 2), new Posting(2, 3), new Posting(4, 1))));
        postingLists.put("term3", new ArrayList<>(Arrays.asList(new Posting(2, 1), new Posting(3,1), new Posting(5,1))));
        ScoringFunction scoringFunction = new DummyScoringFunction(postingLists, queryTerms, 3);
        int k = 2;

        // Call method under test
        MaxScore maxScore = new MaxScore();
        BoundedPriorityQueue scores = maxScore.scoreDocuments(queryTerms, postingLists, scoringFunction, k);

        PriorityQueue<FinalScore> sc = scores.getQueue();

        // Check that the returned queue has the correct order of documents
        assertEquals(2, scores.size());
        assertEquals(1, sc.poll().getKey());
        assertEquals(2, sc.poll().getKey());
    }


}
