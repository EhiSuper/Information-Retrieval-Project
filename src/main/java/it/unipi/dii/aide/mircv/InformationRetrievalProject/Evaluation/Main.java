package it.unipi.dii.aide.mircv.InformationRetrievalProject.Evaluation;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Welcome to the Evaluation");
        String file = args[0];

        EvaluateSearchEngine eval = new EvaluateSearchEngine();
        eval.processCollection(file);
    }
}


