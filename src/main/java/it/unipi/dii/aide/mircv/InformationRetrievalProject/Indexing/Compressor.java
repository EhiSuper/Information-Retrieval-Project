package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Compressor {

    public ArrayList<Integer> vbEncode(int number){
        ArrayList<Integer> numbers = new ArrayList<>();
        ArrayList<Integer> reversed = new ArrayList<>();
        while(true){
            numbers.add(number % 128);
            if (number < 128){
                break;
            }
            number /= 128;
        }
        numbers.set(0, numbers.get(0) + 128);
        for(int i = 0; i<numbers.size(); i++){
            reversed.add(numbers.get(numbers.size() - i - 1));
        }
        return reversed;
    }

    public int vbDecode(ArrayList<Integer> bytes){
        int n = 0;
        for(Integer number : bytes){
            if (number < 128){
                n = 128 * n + number;
            }
            else{
                n = 128 * n + number - 128;
            }
        }
        return n;
    }

    public int readBytes(RandomAccessFile file){
        ArrayList<Integer> bytes = new ArrayList<>();
        int byteRead = 0;
        int n = 0;
        try{
            while(true){
                byteRead = file.read();
                bytes.add(byteRead);
                if(byteRead >= 128){
                    break;
                }
            }
            n = vbDecode(bytes);
        }catch (IOException e){
            e.printStackTrace();
        }
        return n;
    }

    public int writeBytes(RandomAccessFile file, int number){
        ArrayList<Integer> bytes;
        bytes = vbEncode(number);
        int numberOfBytesWritten = 0;
        try{
            for(Integer value : bytes){
                file.write(value);
                numberOfBytesWritten ++;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return  numberOfBytesWritten;
    }
}
