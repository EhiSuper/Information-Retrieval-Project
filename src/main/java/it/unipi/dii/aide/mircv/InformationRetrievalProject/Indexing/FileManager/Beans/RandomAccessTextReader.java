package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessTextReader implements Reader{

    public RandomAccessFile randomAccessFile;

    public RandomAccessTextReader(String file){
        try{
            randomAccessFile = new RandomAccessFile(file, "r");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public int read() {
        try{
            return randomAccessFile.readInt();
        }catch (IOException e){
            e.printStackTrace();
        }
        return -1;
    }
    @Override
    public void close(){
        try{
            randomAccessFile.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public Object getReader() {
        return randomAccessFile;
    }
}
