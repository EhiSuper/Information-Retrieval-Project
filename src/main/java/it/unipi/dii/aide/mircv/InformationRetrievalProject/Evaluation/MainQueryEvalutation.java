package it.unipi.dii.aide.mircv.InformationRetrievalProject.Evaluation;

import java.io.IOException;

public class MainQueryEvalutation {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Welcome to the Evaluation");
        String file = args[0];

        EvaluateSearchEngine eval = new EvaluateSearchEngine();
        long start = System.currentTimeMillis();
        eval.processCollection(file);
        long end = System.currentTimeMillis();
        System.out.println("Elapsed Time in milliseconds: " + (end-start));
    }
}


