package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessing;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Posting;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.HandleIndex;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.Scoring.ScoringFunction;
import java.util.ArrayList;
import java.util.HashMap;

public class DAAT {
    String relationType;
    HandleIndex handleIndex;

    public DAAT(String relationType, HandleIndex handleIndex){
        this.relationType = relationType;
        this.handleIndex = handleIndex;
    }

    public BoundedPriorityQueue scoreDocuments(String[] queryTerms, HashMap<String, ArrayList<Posting>> postingLists, ScoringFunction scoringFunction, int k){
        BoundedPriorityQueue scores = new BoundedPriorityQueue(k);

        ArrayList<PostingListIterator> postingIterators = new ArrayList<>(); //List of iterators
        //Create an iterator foreach posting list related to each query term (like a pointer)
        for(String term : queryTerms){
            postingIterators.add(new PostingListIterator(term, postingLists.get(term), scoringFunction, handleIndex, "daat"));
        }

        if(relationType.equals("conjunctive")){
            processConjunctive(scores,postingIterators);
            return scores;
        }

        while(!notFinished(postingIterators)){
            int minDocid = minDocId(postingIterators); //Get minimum docID over all posting lists

            double score = 0.0;
            for(int i = 0; i < queryTerms.length; i++){ //Foreach posting list check if the current posting correspond to the minimum docID
                PostingListIterator term_iterator = postingIterators.get(i);
                if (!term_iterator.isFinished()) {
                    if (term_iterator.docid() == minDocid) { //If the current posting has the docID equal to the minimum docID
                        score += term_iterator.score(queryTerms[i]); //Compute the score using the posting score function
                        term_iterator.next(); //Go to the next element of the posting list
                    }
                }
            }

            scores.add(new FinalScore(minDocid,score)); //Add the final score to the priorityQueue
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
                scores.add(new FinalScore(handleIndex.getDocumentIndex().getDocumentIndex().get(docId).getDocNo(),score));
            }
        }

    }


    //Get the minimum docID over all the posting lists
    public int minDocId(ArrayList<PostingListIterator> postingIterators){
        int minDocId = Integer.MAX_VALUE; //N° docs in the collection (maximum docID)
        for(PostingListIterator postingIterator : postingIterators){
            if(!postingIterator.isFinished()){
                if(postingIterator.docid() < minDocId){
                    minDocId = postingIterator.docid();
                }
            }
        }
        return minDocId;
    }

    //Check if the query processing is finished, i.e. all the posting lists has been fully processed
    public boolean notFinished(ArrayList<PostingListIterator> postingIterators){
        boolean finished = true;
        for(PostingListIterator postingIterator : postingIterators){
            if (postingIterator.hasNext()) {
                finished = false;
                break;
            }
        }
        return finished;
    }
}
