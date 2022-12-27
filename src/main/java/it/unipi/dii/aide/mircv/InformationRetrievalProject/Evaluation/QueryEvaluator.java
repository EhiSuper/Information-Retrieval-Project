package it.unipi.dii.aide.mircv.InformationRetrievalProject.Evaluation;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessing.BoundedPriorityQueue;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessing.FinalScore;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessor;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.TextPreprocessing.TextPreprocessing;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;

public class QueryEvaluator implements Runnable{
    private final String line;
    private final BufferedWriter bw;
    private final QueryProcessor queryProcessor;

    public QueryEvaluator(String line, BufferedWriter bw, QueryProcessor queryProcessor) {
        this.line = line;
        this.bw = bw;
        this.queryProcessor = queryProcessor;
    }

    @Override
    public void run() {
        // Split the line into fields using a Scanner
        Scanner scanner = new Scanner(line);
        scanner.useDelimiter("\t");

        int qid = Integer.parseInt(scanner.next()); //Get docNo

        String query = TextPreprocessing.parse(scanner.next()); //Get document

        // Write the result to the output file
        try {
            processQuery(query, qid, queryProcessor);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void processQuery(String query, int qid, QueryProcessor queryProcessor) throws IOException {
        BoundedPriorityQueue results = queryProcessor.processQuery(query);

        PriorityQueue<FinalScore> queue = results.getQueue();
        Stack<FinalScore> stack = new Stack<>();

        // Iterate through the priority queue and add each element to the stack
        while (!queue.isEmpty()) {
            stack.push(queue.poll());
        }

        int position = 1;
        while (!stack.isEmpty()) {
            FinalScore fs = stack.pop();
            synchronized (bw) {
                bw.write(qid + " " + "Q0" + " " + fs.getKey() + " " + position + " " + fs.getValue() + " " + "STANDARD");
                bw.newLine();
            }
            position+=1;
        }
    }
}
