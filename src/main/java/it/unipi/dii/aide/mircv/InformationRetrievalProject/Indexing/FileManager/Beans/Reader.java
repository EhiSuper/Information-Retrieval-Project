package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans;

//Interface that a reader has to implement.
public interface Reader<T> {
    int read();
    void close();

    T getReader();
}
