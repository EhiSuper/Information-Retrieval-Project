package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;

//class that represents a posting.
//A posting is represented as a couple of docId, term frequency.
public class Posting {

    public int docId;
    public int freq;

    public Posting(int docId, int freq){
        this.docId = docId;
        this.freq = freq;
    }

    public void setDocId(int docId){
        this.docId = docId;
    }

    public void setFreq(int freq){
        this.freq = freq;
    }

    public int getDocId(){
        return this.docId;
    }

    public int getFreq(){
        return this.freq;
    }

    @Override
    public String toString() {
        return "[ docId: " + docId + " freq: " + freq + " ]";
    }
}
