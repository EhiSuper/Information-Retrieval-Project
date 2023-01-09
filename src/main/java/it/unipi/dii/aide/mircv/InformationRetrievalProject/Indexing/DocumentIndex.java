package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

//class that represents the document index of the document collection.
public class DocumentIndex {

    //the document index is an hashmap between docId and document informations.
    public HashMap<Integer, DocumentInformation> documentIndex;

    public DocumentIndex(){
        documentIndex = new HashMap<>();
    }

    public void setDocumentIndex(HashMap<Integer, DocumentInformation> documentIndex){ this.documentIndex = documentIndex;}
    public HashMap<Integer, DocumentInformation> getDocumentIndex(){ return this.documentIndex; }

    public static class DocumentInformation{
        public int docNo;
        public int size;

        public DocumentInformation(int docNo, int size) {
            this.docNo = docNo;
            this.size = size;
        }

        public int getDocNo() {
            return docNo;
        }

        public void setDocNo(int docNo) {
            this.docNo = docNo;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        @Override
        public String toString() {
            return docNo + " " + size;
        }
    }

    //function that adds a document to the document index.
    public void addDocument(int docId, int docNo, int size ){
        documentIndex.put(docId, new DocumentInformation(docNo, size));
    }

    //function that returns a list of sorted integers representing the docIds of the document index
    public ArrayList<Integer> sortDocumentIndex(){
        ArrayList<Integer> sortedDocIds = new ArrayList<>(documentIndex.keySet());
        Collections.sort(sortedDocIds);
        return sortedDocIds;
    }

}
