package it.unipi.dii.aide.mircv.InformationRetrievalProject.QueryProcessing.QueryProcessing;


//Class used to save the pair (docID, final_score)
public class FinalScore implements Comparable<FinalScore>{
    private int key;
    private double value;

    public FinalScore(int key, double value){
        this.key = key;
        this.value = value;
    }

    public void setKey(int key) {this.key = key;}

    public int getKey() {return key;}

    public void setValue(double value) {this.value = value;}

    public double getValue() {return value;}

    //Used to compare the value of the priorityQueue
    @Override
    public int compareTo(FinalScore other) {
        return Double.compare(this.value, other.getValue());
    }

    @Override
    public String toString() {
        return key + " " + value;
    }
}
