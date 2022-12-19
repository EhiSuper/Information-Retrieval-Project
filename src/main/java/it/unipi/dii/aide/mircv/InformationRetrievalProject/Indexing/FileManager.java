package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;

import java.io.*;
import java.util.Scanner;

public class FileManager {

    public FileWriter myWriterDocIds;
    public FileWriter myWriterFreq;
    public FileWriter myWriterLexicon;
    public FileWriter myWriterDocumentIndex;
    public RandomAccessFile myWriterDocIdsEncoded;
    public RandomAccessFile myWriterFreqEncoded;
    public RandomAccessFile myWriterDocumentIndexEncoded;

    Scanner[] lexiconScanners;
    Scanner[] docIdsScanners;
    Scanner[] freqScanners;

    Scanner[] documentIndexScanners;
    RandomAccessFile[] docIdsEncodedScanners;
    RandomAccessFile[] freqEncodedScanners;

    RandomAccessFile[] documentIndexEncodedScanners;


    public FileWriter getMyWriterDocIds() {
        return myWriterDocIds;
    }

    public void setMyWriterDocIds(FileWriter myWriterDocIds) {
        this.myWriterDocIds = myWriterDocIds;
    }

    public FileWriter getMyWriterFreq() {
        return myWriterFreq;
    }

    public void setMyWriterFreq(FileWriter myWriterFreq) {
        this.myWriterFreq = myWriterFreq;
    }

    public FileWriter getMyWriterLexicon() {
        return myWriterLexicon;
    }

    public void setMyWriterLexicon(FileWriter myWriterLexicon) {
        this.myWriterLexicon = myWriterLexicon;
    }

    public RandomAccessFile getMyWriterDocIdsEncoded() {
        return myWriterDocIdsEncoded;
    }

    public void setMyWriterDocIdsEncoded(RandomAccessFile myWriterDocIdsEncoded) {
        this.myWriterDocIdsEncoded = myWriterDocIdsEncoded;
    }

    public RandomAccessFile getMyWriterFreqEncoded() {
        return myWriterFreqEncoded;
    }

    public void setMyWriterFreqEncoded(RandomAccessFile myWriterFreqEncoded) {
        this.myWriterFreqEncoded = myWriterFreqEncoded;
    }

    public Scanner[] getLexiconScanners() {
        return lexiconScanners;
    }

    public void setLexiconScanners(Scanner[] lexiconScanners) {
        this.lexiconScanners = lexiconScanners;
    }

    public Scanner[] getDocIdsScanners() {
        return docIdsScanners;
    }

    public void setDocIdsScanners(Scanner[] docIdsScanners) {
        this.docIdsScanners = docIdsScanners;
    }

    public Scanner[] getFreqScanners() {
        return freqScanners;
    }

    public void setFreqScanners(Scanner[] freqScanners) {
        this.freqScanners = freqScanners;
    }

    public RandomAccessFile[] getDocIdsEncodedScanners() {
        return docIdsEncodedScanners;
    }

    public void setDocIdsEncodedScanners(RandomAccessFile[] docIdsEncodedScanners) {
        this.docIdsEncodedScanners = docIdsEncodedScanners;
    }

    public RandomAccessFile[] getFreqEncodedScanners() {
        return freqEncodedScanners;
    }

    public void setFreqEncodedScanners(RandomAccessFile[] freqEncodedScanners) {
        this.freqEncodedScanners = freqEncodedScanners;
    }

    public FileWriter getMyWriterDocumentIndex() {
        return myWriterDocumentIndex;
    }

    public void setMyWriterDocumentIndex(FileWriter myWriterDocumentIndex) {
        this.myWriterDocumentIndex = myWriterDocumentIndex;
    }

    public RandomAccessFile getMyWriterDocumentIndexEncoded() {
        return myWriterDocumentIndexEncoded;
    }

    public void setMyWriterDocumentIndexEncoded(RandomAccessFile myWriterDocumentIndexEncoded) {
        this.myWriterDocumentIndexEncoded = myWriterDocumentIndexEncoded;
    }

    public Scanner[] getDocumentIndexScanners() {
        return documentIndexScanners;
    }

    public void setDocumentIndexScanners(Scanner[] documentIndexScanners) {
        this.documentIndexScanners = documentIndexScanners;
    }

    public RandomAccessFile[] getDocumentIndexEncodedScanners() {
        return documentIndexEncodedScanners;
    }

    public void setDocumentIndexEncodedScanners(RandomAccessFile[] documentIndexEncodedScanners) {
        this.documentIndexEncodedScanners = documentIndexEncodedScanners;
    }

    public void openBlockFiles(int blockCounter){
        try{
            myWriterDocIds = new FileWriter("Data/Output/DocIds/docIds" + blockCounter + ".txt");
            myWriterFreq = new FileWriter("Data/Output/Frequencies/freq" + blockCounter + ".txt");
            myWriterLexicon = new FileWriter("Data/Output/Lexicon/lexicon" + blockCounter + ".txt");
            myWriterDocumentIndex = new FileWriter("Data/Output/DocumentIndex/documentIndex" + blockCounter + ".txt");
            myWriterDocIdsEncoded = new RandomAccessFile("Data/Output/DocIds/docIds" + blockCounter + "Encoded.txt", "rw");
            myWriterFreqEncoded = new RandomAccessFile("Data/Output/Frequencies/freq" + blockCounter + "Encoded.txt", "rw");
            myWriterDocumentIndexEncoded = new RandomAccessFile("Data/Output/DocumentIndex/documentIndex" + blockCounter + "Encoded.txt", "rw");

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void closeBlockFiles(){
        try{
            myWriterDocIds.close();
            myWriterFreq.close();
            myWriterLexicon.close();
            myWriterDocumentIndex.close();
            myWriterDocIdsEncoded.close();
            myWriterFreqEncoded.close();
            myWriterDocumentIndexEncoded.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void writeOnFile(FileWriter file, String string){
        try{
            file.write(string);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void writeLineOnFile(FileWriter file, String string){
        try{
            file.write(string + "\n");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void writeOnFile(RandomAccessFile file, int number){
        try{
            file.writeInt(number);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public String readFromFile(Scanner scanner){
        return scanner.next();
    }

    public String readLineFromFile(Scanner scanner){
        return scanner.nextLine();
    }

    public int readFromFile(RandomAccessFile file){
        try{
            return file.readInt();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return 0;
    }

    public int readFromFile(RandomAccessFile file, int offset){
        try{
            file.seek(offset);
            return file.readInt();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return 0;
    }

    public void goToOffset(RandomAccessFile file, int offset){
        try{
            file.seek(offset);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void createFinalFiles(){
        try {
            File myObj = new File("Data/Output/DocIds/docIds.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myObj = new File("Data/Output/Frequencies/freq.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myObj = new File("Data/Output/Lexicon/lexicon.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myObj = new File("Data/Output/DocIds/docIdsEncoded.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myObj = new File("Data/Output/Frequencies/freqEncoded.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myObj = new File("Data/Output/DocumentIndex/documentIndex.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myObj = new File("Data/Output/DocumentIndex/documentIndexEncoded.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myObj = new File("Data/Output/CollectionStatistics/collectionStatistics.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred during the creation of the output file.");
            e.printStackTrace();
        }
    }

    public void createBlockFiles (int blockCounter){
        try {
            File myObj = new File("Data/Output/DocIds/docIds" + blockCounter + ".txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myObj = new File("Data/Output/Frequencies/freq" + blockCounter + ".txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myObj = new File("Data/Output/Lexicon/lexicon" + blockCounter + ".txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myObj = new File("Data/Output/DocIds/docIds" + blockCounter + "Encoded.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myObj = new File("Data/Output/Frequencies/freq" + blockCounter + "Encoded.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myObj = new File("Data/Output/DocumentIndex/documentIndex" + blockCounter + ".txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myObj = new File("Data/Output/DocumentIndex/documentIndex" + blockCounter + "Encoded.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred during the creation of the output file.");
            e.printStackTrace();
        }
    }

    public void openScanners(int blockCounter){
        lexiconScanners = new Scanner[blockCounter];
        docIdsScanners = new Scanner[blockCounter];
        freqScanners = new Scanner[blockCounter];
        documentIndexScanners = new Scanner[blockCounter];
        docIdsEncodedScanners = new RandomAccessFile[blockCounter];
        freqEncodedScanners = new RandomAccessFile[blockCounter];
        documentIndexEncodedScanners = new RandomAccessFile[blockCounter];
        try{
            for(int i = 0; i<blockCounter; i++){
                lexiconScanners[i] = new Scanner(new File("Data/Output/Lexicon/lexicon" + i + ".txt"));
                docIdsScanners[i] = new Scanner(new File("Data/Output/DocIds/docIds" + i + ".txt"));
                freqScanners[i] = new Scanner(new File("Data/Output/Frequencies/freq" + i + ".txt"));
                documentIndexScanners[i] = new Scanner(new File("Data/Output/DocumentIndex/documentIndex" + i + ".txt"));
                docIdsEncodedScanners[i] = new RandomAccessFile("Data/Output/DocIds/docIds" + i + "Encoded.txt", "r");
                freqEncodedScanners[i] = new RandomAccessFile("Data/Output/Frequencies/freq" + i + "Encoded.txt", "r");
                documentIndexEncodedScanners[i] = new RandomAccessFile("Data/Output/DocumentIndex/documentIndex" + i + "Encoded.txt", "r");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void closeScanners() {
        for (int i = 0; i<lexiconScanners.length; i++){
            try{
                lexiconScanners[i].close();
                docIdsScanners[i].close();
                freqScanners[i].close();
                documentIndexScanners[i].close();
                docIdsEncodedScanners[i].close();
                freqEncodedScanners[i].close();
                documentIndexEncodedScanners[i].close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void openMergeFiles(){
        try{
            myWriterDocIds = new FileWriter("Data/Output/DocIds/docIds.txt");
            myWriterFreq = new FileWriter("Data/Output/Frequencies/freq.txt");
            myWriterLexicon = new FileWriter("Data/Output/Lexicon/lexicon.txt");
            myWriterDocumentIndex = new FileWriter("Data/Output/DocumentIndex/documentIndex.txt");
            myWriterDocIdsEncoded = new RandomAccessFile("Data/Output/DocIds/docIdsEncoded.txt", "rw");
            myWriterFreqEncoded = new RandomAccessFile("Data/Output/Frequencies/freqEncoded.txt", "rw");
            myWriterDocumentIndexEncoded = new RandomAccessFile("Data/Output/DocumentIndex/documentIndexEncoded.txt", "rw");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void closeMergeFiles(){
        try{
            myWriterDocIds.close();
            myWriterFreq.close();
            myWriterLexicon.close();
            myWriterDocumentIndex.close();
            myWriterDocIdsEncoded.close();
            myWriterFreqEncoded.close();
            myWriterDocumentIndexEncoded.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void openLookupFiles(){
        lexiconScanners = new Scanner[1];
        docIdsEncodedScanners = new RandomAccessFile[1];
        freqEncodedScanners = new RandomAccessFile[1];
        try{
            lexiconScanners[0] = new Scanner(new File("Data/Output/Lexicon/lexicon.txt"));
            docIdsEncodedScanners[0] = new RandomAccessFile("Data/Output/DocIds/docIdsEncoded.txt", "r");
            freqEncodedScanners[0] = new RandomAccessFile("Data/Output/Frequencies/freqEncoded.txt", "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void closeLookupFiles(){
        try{
            lexiconScanners[0].close();
            docIdsEncodedScanners[0].close();
            freqEncodedScanners[0].close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
