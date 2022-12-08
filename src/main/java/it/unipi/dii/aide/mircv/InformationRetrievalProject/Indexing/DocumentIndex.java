package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;

import java.util.HashMap;

public class DocumentIndex {

    public HashMap<Integer, DocumentInformation> documentIndex;

    public DocumentIndex(){
        documentIndex = new HashMap<>();
    }

    public void setDocumentIndex(HashMap<Integer, DocumentInformation> documentIndex){ this.documentIndex = documentIndex;}
    public HashMap<Integer, DocumentInformation> getDocumentIndex(){ return this.documentIndex; }

    class DocumentInformation{
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
    }

    public void addDocument(int docId, int docNo, int size ){
        documentIndex.put(docId, new DocumentInformation(docNo, size));
    }

}
