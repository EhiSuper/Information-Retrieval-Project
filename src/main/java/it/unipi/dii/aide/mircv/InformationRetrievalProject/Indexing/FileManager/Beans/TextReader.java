package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans;

import java.io.*;
import java.util.Scanner;

public class TextReader implements Reader{
    public Scanner scanner;

    public TextReader(String file){
        try{
            this.scanner = new Scanner(new File(file));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public int read() {
        if(scanner.hasNext()){
            return Integer.parseInt(scanner.next());
        }
        else return -1;
    }

    public String readLine(){
        return scanner.nextLine();
    }

    @Override
    public void close(){
        scanner.close();
    }

    @Override
    public Object getReader() {
        return scanner;
    }

    public boolean hasNextLine(){
        return scanner.hasNextLine();
    }
}
