package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class VariableByteCode implements Compressor{

    public ArrayList<Integer> encode(int number){
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

    public int decode(ArrayList<Integer> bytes){
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

    public int readBytes(BufferedInputStream file){
        ArrayList<Integer> bytes = new ArrayList<>();
        int byteRead;
        int n = 0;
        try{
            while(true){
                byteRead = file.read();
                if(byteRead == -1) break;
                bytes.add(byteRead);
                if(byteRead >= 128){
                    break;
                }
            }
            if (byteRead != -1) n = decode(bytes);
            else n = -1;
        }catch (IOException e){
            e.printStackTrace();
        }
        return n;
    }

    public int readBytes(RandomAccessFile file){
        ArrayList<Integer> bytes = new ArrayList<>();
        int byteRead;
        int n = 0;
        try{
            while(true){
                byteRead = file.read();
                bytes.add(byteRead);
                if(byteRead >= 128){
                    break;
                }
            }
            n = decode(bytes);
        }catch (IOException e){
            e.printStackTrace();
        }
        return n;
    }

    public int writeBytes(BufferedOutputStream file, int number){
        ArrayList<Integer> bytes;
        bytes = encode(number);
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

