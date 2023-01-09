package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

//class that writes to text files using a BufferedWriter.
public class TextWriter implements Writer{

    public BufferedWriter bufferedWriter;

    public TextWriter(String file){
        try{
            this.bufferedWriter = new BufferedWriter(new FileWriter(file));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public int write(int number) {
        try{
            bufferedWriter.write(number + " ");
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void write(String line){
        try{
            bufferedWriter.write(line);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void close(){
        try{
            bufferedWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
