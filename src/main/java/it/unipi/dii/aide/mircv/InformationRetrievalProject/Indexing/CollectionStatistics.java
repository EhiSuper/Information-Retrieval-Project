package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;

public class CollectionStatistics {

    public int documents;
    public double avgDocumentLength;
    public int terms;
    public int postings;

    public CollectionStatistics(int documents, double avgDocumentLength, int terms, int postings) {
        this.documents = documents;
        this.avgDocumentLength = avgDocumentLength;
        this.terms = terms;
        this.postings = postings;
    }

    public int getDocuments() {
        return documents;
    }

    public void setDocuments(int documents) {
        this.documents = documents;
    }

    public double getAvgDocumentLength() {
        return avgDocumentLength;
    }

    public void setAvgDocumentLength(double avgDocumentLength) {
        this.avgDocumentLength = avgDocumentLength;
    }

    public int getTerms() {
        return terms;
    }

    public void setTerms(int terms) {
        this.terms = terms;
    }

    public int getPostings() {
        return postings;
    }

    public void setPostings(int postings) {
        this.postings = postings;
    }
}
