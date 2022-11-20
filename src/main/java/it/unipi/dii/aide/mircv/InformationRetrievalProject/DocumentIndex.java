package it.unipi.dii.aide.mircv.InformationRetrievalProject;

import java.util.HashMap;

public class DocumentIndex {

    public HashMap<Integer, DocumentInformation> index;

    class DocumentInformation{
        public int docNo;
        public String URL;
        public int size;

        public DocumentInformation(int docNo, String URL, int size) {
            this.docNo = docNo;
            this.URL = URL;
            this.size = size;
        }

        public int getDocNo() {
            return docNo;
        }

        public void setDocNo(int docNo) {
            this.docNo = docNo;
        }

        public String getURL() {
            return URL;
        }

        public void setURL(String URL) {
            this.URL = URL;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }
}
