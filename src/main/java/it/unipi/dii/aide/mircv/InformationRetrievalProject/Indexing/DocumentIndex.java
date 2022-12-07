package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;

import java.util.HashMap;

public class DocumentIndex {

    public HashMap<Integer, DocumentInformation> index;

    class DocumentInformation{
        public int docNo;
        public int size;

        public DocumentInformation(int docNo, String URL, int size) {
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
}
