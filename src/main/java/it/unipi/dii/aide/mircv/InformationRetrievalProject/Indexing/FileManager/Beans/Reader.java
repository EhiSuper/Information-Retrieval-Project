package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans;

public interface Reader<T> {
    int read();
    void close();

    T getReader();
}
