package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessing;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.HandleIndex;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring.ScoringFunction;

import java.util.*;
import java.util.stream.Collectors;

public class MaxScore {

    String relationType;
    HandleIndex handleIndex;


    public MaxScore(String relationType, HandleIndex handleIndex){
        this.relationType = relationType;
        this.handleIndex = handleIndex;
    }

    //Main function for scoring documents
    public BoundedPriorityQueue scoreDocuments(String[] queryTerms, HashMap<String, ArrayList<Posting>> postingLists, ScoringFunction scoringFunction, int k){
        BoundedPriorityQueue scores = new BoundedPriorityQueue(k); //Initialize a new BoundedPriorityQueue with a capacity of k
        HashMap<String, Double> termUpperBounds = new HashMap<>(); //Create a HashMap of term upper bounds
        double threshold = 0;

        //Find term upper bounds for each query term
        for(String term : queryTerms){
            termUpperBounds.put(term,(double) handleIndex.getLexicon().getLexicon().get(term).getTermUpperBound());
        }

        //Sort the posting lists based on the term upper bounds
        postingLists = postingLists.entrySet().stream()
                .sorted(Comparator.comparingDouble(e -> termUpperBounds.get(e.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        //Create an array of terms in the order they appear in the posting lists
        String[] termsOrder = postingLists.keySet().toArray(new String[0]);

        //Create an array of booleans to keep track of which posting lists are "essential" and mark which posting lists are "essential"
        boolean[] essentialPostingList = new boolean[termsOrder.length];
        double counter = 0;
        for(int i = 0; i < termsOrder.length; i++){
            counter += termUpperBounds.get(termsOrder[i]);
            essentialPostingList[i] = counter >= threshold;
        }

        //Create an array list of PostingListIterators, one for each query term
        ArrayList<PostingListIterator> postingIterators = new ArrayList<>();
        for(String term : termsOrder){
            postingIterators.add(new PostingListIterator(term, postingLists.get(term), scoringFunction, handleIndex, "maxscore"));
        }

        //Check if the query is conjunctive and execute it conjunctive in case
        if(relationType.equals("conjunctive")){
            processConjunctive(scores,postingIterators);
            return scores;
        }

        //Iterate through the posting lists until all are finished
        while(!notFinished(postingIterators, essentialPostingList)){
            int minDocid = minDocId(postingIterators, essentialPostingList); //Get minimum docID over all posting lists
            double score = 0.0;
            boolean checkDocUpperBound = false;
            // Loop through the posting lists in reverse order
            for(int i = termsOrder.length-1; i >= 0; i--){ //Foreach posting list check if the current posting corresponds to the minimum docID
                PostingListIterator term_iterator = postingIterators.get(i);
                // If the current posting list is not essential
                if (!essentialPostingList[i]) {
                    if(!checkDocUpperBound) { // Check if the document upper bound has been reached
                        if(!checkDocumentUpperBound(score, termUpperBounds, termsOrder,  i, threshold)){
                            break; // If it has been reached, break out of the loop
                        }else{
                            checkDocUpperBound = true;
                        }
                    }
                    term_iterator.nextGEQ(minDocid); // Move the iterator to the next element with a docID greater or equal to the minimum docID
                }
                // If the iterator has not reached the end of the posting list
                if (!term_iterator.isFinished()) {
                    if (term_iterator.docid() == minDocid) {  // If the current posting has the same docID as the minimum docID
                        score += term_iterator.score(termsOrder[i]); // Add the score for the posting to the total score for the document
                        term_iterator.next(); // Move the iterator to the next element
                    }
                }
            }

            scores.add(new FinalScore(minDocid,score)); // Add the final score for the document to the priority queue
            if(scores.isFull()){ // If the priority queue is full
                threshold = scores.peek().getValue();  // Set the threshold to the minimum score in the priority queue
            }

            counter = 0;
            for(int i = 0; i < termsOrder.length; i++){
                counter += termUpperBounds.get(termsOrder[i]);
                essentialPostingList[i] = counter >= threshold;
            }
        }

        return scores; //Return the top K scores


    }


    public void processConjunctive(BoundedPriorityQueue scores, ArrayList<PostingListIterator> postingListIterators){
        //Find the smallest postingList
        int minPostingListIndex = 0;
        int minPostingListLength = handleIndex.getLexicon().getLexicon().get(postingListIterators.get(0).getTerm()).getPostingListLength();
        for(int i=1; i<postingListIterators.size(); i++){
            if(minPostingListLength>handleIndex.getLexicon().getLexicon().get(postingListIterators.get(i).getTerm()).getPostingListLength()){
                minPostingListIndex = i;
                minPostingListLength = handleIndex.getLexicon().getLexicon().get(postingListIterators.get(i).getTerm()).getPostingListLength();
            }
        }

        PostingListIterator minPostingListIterator = postingListIterators.get(minPostingListIndex);
        while(!minPostingListIterator.isFinished()){//While there are posting to be processed
            boolean toAdd = true;
            int docId = minPostingListIterator.docid();
            double score = minPostingListIterator.score(minPostingListIterator.getTerm());
            minPostingListIterator.next();
            for(int i=0;i<postingListIterators.size();i++){ //foreach other posting list call the nextGEQ on the docID of the smallest postingList
                if(i!=minPostingListIndex){
                    postingListIterators.get(i).nextGEQ(docId);
                    if(docId == postingListIterators.get(i).docid()){
                        score += postingListIterators.get(i).score(postingListIterators.get(i).getTerm());
                    }else{
                        toAdd = false;
                        break;
                    }
                }
            }
            if(toAdd){
                scores.add(new FinalScore(handleIndex.getDocumentIndex().getDocumentIndex().get(docId).getDocNo(),score));
            }
        }

    }

    //Check if the remaining upper bound score of the terms is greater than the current threshold
    public boolean checkDocumentUpperBound(double score, HashMap<String, Double> termUpperBounds, String[] termsOrder, int i, double threshold){
        for(int j=i; j>=0; j--){
            score += termUpperBounds.get(termsOrder[j]);
        }
        return score >= threshold;
    }

    //Get the minimum docID over all the posting lists
    public int minDocId(ArrayList<PostingListIterator> postingIterators, boolean[] essentialPostingList){
        int minDocId = Integer.MAX_VALUE;

        for(int i=essentialPostingList.length-1; i>=0; i--){
            if(essentialPostingList[i]){
                PostingListIterator postingIterator = postingIterators.get(i);
                if(!postingIterator.isFinished()) {
                    if(postingIterator.docid() < minDocId){
                        minDocId = postingIterator.docid();
                    }
                }
            }
        }

        return minDocId;
    }

    //Check if the query processing is finished, i.e. all the posting lists has been fully processed
    public boolean notFinished(ArrayList<PostingListIterator> postingIterators, boolean[] essentialPostingList){
        boolean finished = true;
        for(int i=essentialPostingList.length-1; i>=0; i--){
            if(essentialPostingList[i]){
                if(postingIterators.get(i).hasNext()) {
                    finished = false;
                    break;
                }
            }
        }
        return finished;
    }

}