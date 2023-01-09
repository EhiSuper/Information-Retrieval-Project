package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;

//interface that a compression class has to implement
public interface Compressor {
    public ArrayList<Integer> encode(int number);
    public int decode(ArrayList<Integer> bytes);
    public int readBytes(BufferedInputStream file);
    public int readBytes(RandomAccessFile file);
    public int writeBytes(BufferedOutputStream file, int number);
}
