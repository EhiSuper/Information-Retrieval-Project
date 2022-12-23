package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ByteWriter implements Writer{
    public BufferedOutputStream bufferedOutputStream;
    public Compressor compressor;

    public ByteWriter(String file, Compressor compressor){
        this.compressor = compressor;
        try{
            this.bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public int write(int number) {
        return compressor.writeBytes(bufferedOutputStream, number);
    }

    @Override
    public void close(){
        try{
            bufferedOutputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
