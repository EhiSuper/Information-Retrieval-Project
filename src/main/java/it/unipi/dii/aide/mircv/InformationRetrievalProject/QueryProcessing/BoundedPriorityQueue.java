package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing;

import java.util.PriorityQueue;
import java.util.Stack;

public class BoundedPriorityQueue {
    PriorityQueue<FinalScore> queue;
    int dimension;
    public BoundedPriorityQueue(int k){
        this.dimension = k;
        this.queue = new PriorityQueue(k);
    }

    public PriorityQueue<FinalScore> getQueue(){
        return this.queue;
    }

    public void add(FinalScore score){
        if(queue.size() == dimension){
            if(queue.peek().getValue() < score.getValue()){
                queue.poll();
                queue.add(score);
            }
        }
        else{
            queue.add(score);
        }
    }

    public void printResults() {
        Stack<FinalScore> stack = new Stack<>();
        PriorityQueue<FinalScore> copy = new PriorityQueue<>(queue);

        // Iterate through the priority queue and add each element to the stack
        while (!copy.isEmpty()) {
            stack.push(copy.poll());
        }

        System.out.print("[ ");
        // Print the elements in the stack (in reverse order)
        while (!stack.isEmpty()) {
            System.out.print("(" + stack.pop() + "), ");
        }
        System.out.println(" ]");
    }
}
