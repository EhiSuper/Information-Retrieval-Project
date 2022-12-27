import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring.ScoringFunction;

import java.util.ArrayList;
import java.util.HashMap;

public class DummyScoringFunction extends ScoringFunction {
    public DummyScoringFunction(HashMap<String, ArrayList<Posting>> postingLists, String[] queryTerms, long nDocuments) {
        super(postingLists, queryTerms, nDocuments);
    }

    @Override
    public double documentWeight(String term, Posting posting) {
        return posting.getFreq();
    }
}
