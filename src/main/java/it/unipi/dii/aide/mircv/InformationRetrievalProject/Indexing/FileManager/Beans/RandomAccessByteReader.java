package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessByteReader implements Reader{

    public RandomAccessFile randomAccessFile;
    public Compressor compressor;

    public RandomAccessByteReader(String file, Compressor compressor){
        this.compressor = compressor;
        try{
            randomAccessFile = new RandomAccessFile(file, "r");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public int read() {
        return compressor.readBytes(randomAccessFile);
    }
    @Override
    public void close(){
        try{
            randomAccessFile.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public RandomAccessFile getReader(){
        return randomAccessFile;
    }
    public void goToOffset(int offset){
        try{
            randomAccessFile.seek(offset);
        }catch (IOException e){
            e.printStackTrace();
        }

    }

}

