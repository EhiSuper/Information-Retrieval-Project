package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

//class that reads encoded integers using a buffered input stream and a specific
//compressor taken as argument.
public class ByteReader implements Reader{

    public BufferedInputStream bufferedInputStream;
    public Compressor compressor;

    public ByteReader(String file, Compressor compressor){
        this.compressor = compressor;
        try{
            this.bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public int read() {
        return compressor.readBytes(bufferedInputStream);
    }
    @Override
    public void close() {
        try {
            bufferedInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getReader() {
        return bufferedInputStream;
    }

    public void goToOffset(int offset) {
        try{
            bufferedInputStream.skip(offset);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
