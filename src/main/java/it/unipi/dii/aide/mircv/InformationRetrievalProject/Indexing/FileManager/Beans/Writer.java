package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans;

//interface that a class that writes to a file has to implement
public interface Writer {
    int write(int number);
    void close();
}
