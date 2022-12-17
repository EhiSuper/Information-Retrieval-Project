package it.unipi.dii.aide.mircv.InformationRetrievalProject.Evaluation;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.BoundedPriorityQueue;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.FinalScore;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessor;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.TextPreprocessing.TextPreprocessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;

public class EvaluateSearchEngine {

    private File resultFile;
    private PrintWriter writer;

    public EvaluateSearchEngine(){
        try {
            resultFile = new File("Data/Output/queryResults.txt");
            if (resultFile.createNewFile()) {
                System.out.println("File created: " + resultFile.getName());
            } else {
                System.out.println("File already exists.");
            }
        }catch (IOException e) {
            System.out.println("An error occurred during the creation of the output file.");
            e.printStackTrace();
        }

        try{
            writer = new PrintWriter(resultFile);
        }catch (FileNotFoundException e){
            System.out.println("Query result file not founded");
            e.printStackTrace();
        }

    }


    public void processCollection(String file) {
        try {
            File myFile = new File(file);
            Scanner myReader = new Scanner(myFile, StandardCharsets.UTF_8);
            QueryProcessor queryProcessor= new QueryProcessor(10);
            int counter = 0;
            while (myReader.hasNextLine()) {
                System.out.println("Processing Query number: " + counter );
                String[] line = myReader.nextLine().split("\t", 2); //Read the line and split it (cause the line is composed by (docNo \t document))

                int qid;
                try {
                    qid = Integer.parseInt(line[0]); //Get docNo
                } catch (NumberFormatException e) {
                    continue;
                }
                String query = TextPreprocessing.parse(line[1]); //Get document
                processQuery(query, qid, queryProcessor);
                counter += 1;
            }
            myReader.close();
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("The specified file is not found. Please try again.");
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void processQuery(String query, int qid, QueryProcessor queryProcessor){
        BoundedPriorityQueue results = queryProcessor.processQuery(query);
        results.printResults();

        PriorityQueue<FinalScore> queue = results.getQueue();
        Stack<FinalScore> stack = new Stack<>();

        // Iterate through the priority queue and add each element to the stack
        while (!queue.isEmpty()) {
            stack.push(queue.poll());
        }

        int position = 1;
        while (!stack.isEmpty()) {
            FinalScore fs = stack.pop();
            writer.println(qid + " " + "Q0" + " " + fs.getKey() + " " + position + " " +  fs.getValue() + " " + "STANDARD");
            position+=1;
        }
    }
}

