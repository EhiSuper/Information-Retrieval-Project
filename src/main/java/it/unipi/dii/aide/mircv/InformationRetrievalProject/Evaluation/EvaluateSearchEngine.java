package it.unipi.dii.aide.mircv.InformationRetrievalProject.Evaluation;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EvaluateSearchEngine {

    private File resultFile;

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
    }


    public void processCollection(String file) throws IOException, InterruptedException {

        QueryProcessor queryProcessor= new QueryProcessor(10, "tfidf", "daat", "bytes");


        // Open the input and output files
        BufferedReader br = Files.newBufferedReader(Paths.get(file), StandardCharsets.UTF_8);
        BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile));


        ExecutorService executorService = Executors.newFixedThreadPool(10);

        int counter = 0;
        String line;
        while ((line = br.readLine()) != null){
            System.out.println("Processing Query number: " + counter );
            try{
                executorService.execute(new QueryEvaluator(line, bw, queryProcessor));
            }catch (NumberFormatException e) {
                System.out.println("Not a valid qid");
            }
            counter += 1;
        }

        // Shut down the thread pool
        executorService.shutdown();

        // Wait for all tasks to complete
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        // Close the input and output files
        br.close();
        bw.close();
    }
}

