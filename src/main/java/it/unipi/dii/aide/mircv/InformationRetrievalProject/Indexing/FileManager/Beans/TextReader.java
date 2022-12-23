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
        if(scanner.hasNext()) return Integer.valueOf(scanner.next());
        else return -1;
    }

    public String readLine(){
        return scanner.nextLine();
    }

    @Override
    public void close(){
        scanner.close();
    }

    public boolean hasNextLine(){
        return scanner.hasNextLine();
    }
}
