package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessing;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Lexicon;
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

    public BoundedPriorityQueue scoreDocuments(String[] queryTerms, HashMap<String, ArrayList<Posting>> postingLists, ScoringFunction scoringFunction, int k){
        BoundedPriorityQueue scores = new BoundedPriorityQueue(k);
        HashMap<String, Double> termUpperBounds = new HashMap<>();
        double threshold = 0;

        //Compute termUpperBounds
        for(String term : queryTerms){
            termUpperBounds.put(term,(double) handleIndex.getLexicon().getLexicon().get(term).getTermUpperBound());
        }

        postingLists = postingLists.entrySet().stream()
                .sorted(Comparator.comparingDouble(e -> termUpperBounds.get(e.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        String[] termsOrder = postingLists.keySet().toArray(new String[0]);

        boolean[] essentialPostingList = new boolean[termsOrder.length];
        double counter = 0;
        for(int i = 0; i < termsOrder.length; i++){
            counter += termUpperBounds.get(termsOrder[i]);
            essentialPostingList[i] = counter >= threshold;
        }

        ArrayList<PostingListIterator> postingIterators = new ArrayList<>(); //List of iterators
        //Create an iterator foreach posting list related to each query term (like a pointer)
        for(String term : termsOrder){
            postingIterators.add(new PostingListIterator(term, postingLists.get(term), scoringFunction, handleIndex, "maxscore"));
        }

        if(relationType.equals("conjunctive")){
            processConjunctive(scores,postingIterators);
            return scores;
        }

        while(!notFinished(postingIterators, essentialPostingList)){
            int minDocid = minDocId(postingIterators, essentialPostingList); //Get minimum docID over all posting lists


            double score = 0.0;
            boolean checkDocUpperBound = false;
            for(int i = termsOrder.length-1; i >= 0; i--){ //Foreach posting list check if the current posting correspond to the minimum docID
                PostingListIterator term_iterator = postingIterators.get(i);
                if (!essentialPostingList[i]) {
                    if(!checkDocUpperBound) {
                        if(!checkDocumentUpperBound(score, termUpperBounds, termsOrder,  i, threshold)){
                            break;
                        }else{
                            checkDocUpperBound = true;
                        }
                    }
                    term_iterator.nextGEQ(minDocid);
                }
                if (!term_iterator.isFinished()) {
                    if (term_iterator.docid() == minDocid) { //If the current posting has the docID equal to the minimum docID
                        score += term_iterator.score(termsOrder[i]); //Compute the score using the posting score function
                        term_iterator.next(); //Go to the next element of the posting list
                    }
                }
            }

            scores.add(new FinalScore(minDocid,score)); //Add the final score to the priorityQueue
            if(scores.isFull()){
                threshold = scores.peek().getValue();
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
        while(!minPostingListIterator.isFinished()){
            boolean toAdd = true;
            int docId = minPostingListIterator.docid();
            double score = minPostingListIterator.score(minPostingListIterator.getTerm());
            minPostingListIterator.next();
            for(int i=0;i<postingListIterators.size();i++){
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
                scores.add(new FinalScore(docId,score));
            }
        }

    }


    public boolean checkDocumentUpperBound(double score, HashMap<String, Double> termUpperBounds, String[] termsOrder, int i, double threshold){
        for(int j=i; j>=0; j--){
            score += termUpperBounds.get(termsOrder[j]);
        }
        return score >= threshold;
    }

    //Get the minimum docID over all the posting lists
    public int minDocId(ArrayList<PostingListIterator> postingIterators, boolean[] essentialPostingList){
        int minDocId = Integer.MAX_VALUE; //N° docs in the collection (maximum docID)

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