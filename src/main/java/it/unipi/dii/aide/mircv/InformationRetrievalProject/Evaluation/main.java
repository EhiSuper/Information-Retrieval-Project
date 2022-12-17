package it.unipi.dii.aide.mircv.InformationRetrievalProject.Evaluation;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.Index;

public class main {
    public static void main(String[] args) {
        System.out.println("Welcome to the Evaluation");
        String file = args[0];

        EvaluateSearchEngine eval = new EvaluateSearchEngine();
        eval.processCollection(file);
    }
}


