package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;

import java.io.*;
import java.util.Scanner;

public class FileManager {

    public BufferedWriter myWriterDocIds;
    public BufferedWriter myWriterFreq;
    public BufferedWriter myWriterLexicon;
    public BufferedWriter myWriterDocumentIndex;
    public BufferedOutputStream myWriterDocIdsCompressed;
    public BufferedOutputStream myWriterFreqCompressed;
    public BufferedOutputStream myWriterDocumentIndexCompressed;

    Scanner[] lexiconScanners;
    Scanner[] docIdsScanners;
    Scanner[] freqScanners;
    Scanner[] documentIndexScanners;

    BufferedInputStream[] docIdsCompressedScanners;
    BufferedInputStream[] freqCompressedScanners;
    BufferedInputStream[] documentIndexCompressedScanners;

    RandomAccessFile docIdsReader;
    RandomAccessFile freqReader;
    RandomAccessFile documentIndexReader;

    public BufferedWriter getMyWriterDocIds() {
        return myWriterDocIds;
    }

    public void setMyWriterDocIds(BufferedWriter myWriterDocIds) {
        this.myWriterDocIds = myWriterDocIds;
    }

    public BufferedWriter getMyWriterFreq() {
        return myWriterFreq;
    }

    public void setMyWriterFreq(BufferedWriter myWriterFreq) {
        this.myWriterFreq = myWriterFreq;
    }

    public BufferedWriter getMyWriterLexicon() {
        return myWriterLexicon;
    }

    public void setMyWriterLexicon(BufferedWriter myWriterLexicon) {
        this.myWriterLexicon = myWriterLexicon;
    }

    public BufferedWriter getMyWriterDocumentIndex() {
        return myWriterDocumentIndex;
    }

    public void setMyWriterDocumentIndex(BufferedWriter myWriterDocumentIndex) {
        this.myWriterDocumentIndex = myWriterDocumentIndex;
    }

    public BufferedOutputStream getMyWriterDocIdsCompressed() {
        return myWriterDocIdsCompressed;
    }

    public void setMyWriterDocIdsCompressed(BufferedOutputStream myWriterDocIdsCompressed) {
        this.myWriterDocIdsCompressed = myWriterDocIdsCompressed;
    }

    public BufferedOutputStream getMyWriterFreqCompressed() {
        return myWriterFreqCompressed;
    }

    public void setMyWriterFreqCompressed(BufferedOutputStream myWriterFreqCompressed) {
        this.myWriterFreqCompressed = myWriterFreqCompressed;
    }

    public BufferedOutputStream getMyWriterDocumentIndexCompressed() {
        return myWriterDocumentIndexCompressed;
    }

    public void setMyWriterDocumentIndexCompressed(BufferedOutputStream myWriterDocumentIndexCompressed) {
        this.myWriterDocumentIndexCompressed = myWriterDocumentIndexCompressed;
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

    public Scanner[] getDocumentIndexScanners() {
        return documentIndexScanners;
    }

    public void setDocumentIndexScanners(Scanner[] documentIndexScanners) {
        this.documentIndexScanners = documentIndexScanners;
    }

    public BufferedInputStream[] getDocIdsCompressedScanners() {
        return docIdsCompressedScanners;
    }

    public void setDocIdsCompressedScanners(BufferedInputStream[] docIdsCompressedScanners) {
        this.docIdsCompressedScanners = docIdsCompressedScanners;
    }

    public BufferedInputStream[] getFreqCompressedScanners() {
        return freqCompressedScanners;
    }

    public void setFreqCompressedScanners(BufferedInputStream[] freqCompressedScanners) {
        this.freqCompressedScanners = freqCompressedScanners;
    }

    public BufferedInputStream[] getDocumentIndexCompressedScanners() {
        return documentIndexCompressedScanners;
    }

    public void setDocumentIndexCompressedScanners(BufferedInputStream[] documentIndexCompressedScanners) {
        this.documentIndexCompressedScanners = documentIndexCompressedScanners;
    }

    public RandomAccessFile getDocIdsReader() {
        return docIdsReader;
    }

    public void setDocIdsReader(RandomAccessFile docIdsReader) {
        this.docIdsReader = docIdsReader;
    }

    public RandomAccessFile getFreqReader() {
        return freqReader;
    }

    public void setFreqReader(RandomAccessFile freqReader) {
        this.freqReader = freqReader;
    }

    public RandomAccessFile getDocumentIndexReader() {
        return documentIndexReader;
    }

    public void setDocumentIndexReader(RandomAccessFile documentIndexReader) {
        this.documentIndexReader = documentIndexReader;
    }

    public void openBlockFiles(int blockCounter){
        try{
            myWriterDocIds = new BufferedWriter(new FileWriter("Data/Output/DocIds/docIds" + blockCounter + ".txt"));
            myWriterFreq = new BufferedWriter(new FileWriter("Data/Output/Frequencies/freq" + blockCounter + ".txt"));
            myWriterLexicon = new BufferedWriter(new FileWriter("Data/Output/Lexicon/lexicon" + blockCounter + ".txt"));
            myWriterDocumentIndex = new BufferedWriter(new FileWriter("Data/Output/DocumentIndex/documentIndex" + blockCounter + ".txt"));
            myWriterDocIdsCompressed = new BufferedOutputStream(new FileOutputStream("Data/Output/DocIds/docIds" + blockCounter + "Compressed.txt"));
            myWriterFreqCompressed = new BufferedOutputStream(new FileOutputStream("Data/Output/Frequencies/freq" + blockCounter + "Compressed.txt"));
            myWriterDocumentIndexCompressed = new BufferedOutputStream(new FileOutputStream("Data/Output/DocumentIndex/documentIndex" + blockCounter + "Compressed.txt"));
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
            myWriterDocIdsCompressed.close();
            myWriterFreqCompressed.close();
            myWriterDocumentIndexCompressed.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void writeOnFile(BufferedWriter file, String string){
        try{
            file.write(string);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void writeLineOnFile(BufferedWriter file, String string){
        try{
            file.write(string + "\n");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public String readFromFile(Scanner scanner){
        return scanner.next();
    }

    public String readLineFromFile(Scanner scanner){
        return scanner.nextLine();
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
            myObj = new File("Data/Output/DocumentIndex/documentIndex.txt");
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
            myObj = new File("Data/Output/DocIds/docIdsCompressed.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myObj = new File("Data/Output/Frequencies/freqCompressed.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myObj = new File("Data/Output/DocumentIndex/documentIndexCompressed.txt");
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
            myObj = new File("Data/Output/DocumentIndex/documentIndex" + blockCounter + ".txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myObj = new File("Data/Output/DocIds/docIds" + blockCounter + "Compressed.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myObj = new File("Data/Output/Frequencies/freq" + blockCounter + "Compressed.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myObj = new File("Data/Output/DocumentIndex/documentIndex" + blockCounter + "Compressed.txt");
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
        docIdsCompressedScanners = new BufferedInputStream[blockCounter];
        freqCompressedScanners = new BufferedInputStream[blockCounter];
        documentIndexCompressedScanners = new BufferedInputStream[blockCounter];
        try{
            for(int i = 0; i<blockCounter; i++){
                lexiconScanners[i] = new Scanner(new File("Data/Output/Lexicon/lexicon" + i + ".txt"));
                docIdsScanners[i] = new Scanner(new File("Data/Output/DocIds/docIds" + i + ".txt"));
                freqScanners[i] = new Scanner(new File("Data/Output/Frequencies/freq" + i + ".txt"));
                documentIndexScanners[i] = new Scanner(new File("Data/Output/DocumentIndex/documentIndex" + i + ".txt"));
                docIdsCompressedScanners[i] = new BufferedInputStream(new FileInputStream("Data/Output/DocIds/docIds" + i + "Compressed.txt"));
                freqCompressedScanners[i] = new BufferedInputStream(new FileInputStream("Data/Output/Frequencies/freq" + i + "Compressed.txt"));
                documentIndexCompressedScanners[i] = new BufferedInputStream(new FileInputStream("Data/Output/DocumentIndex/documentIndex" + i + "Compressed.txt"));
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
                docIdsCompressedScanners[i].close();
                freqCompressedScanners[i].close();
                docIdsCompressedScanners[i].close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void openMergeFiles(){
        try{
            myWriterDocIds = new BufferedWriter(new FileWriter("Data/Output/DocIds/docIds.txt"));
            myWriterFreq = new BufferedWriter(new FileWriter("Data/Output/Frequencies/freq.txt"));
            myWriterLexicon = new BufferedWriter(new FileWriter("Data/Output/Lexicon/lexicon.txt"));
            myWriterDocumentIndex = new BufferedWriter(new FileWriter("Data/Output/DocumentIndex/documentIndex.txt"));
            myWriterDocIdsCompressed = new BufferedOutputStream(new FileOutputStream("Data/Output/DocIds/docIdsCompressed.txt"));
            myWriterFreqCompressed = new BufferedOutputStream(new FileOutputStream("Data/Output/Frequencies/freqCompressed.txt"));
            myWriterDocumentIndexCompressed = new BufferedOutputStream(new FileOutputStream("Data/Output/DocumentIndex/documentIndexCompressed.txt"));
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
            myWriterDocIdsCompressed.close();
            myWriterFreqCompressed.close();
            myWriterDocumentIndexCompressed.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void openLookupFiles(){
        lexiconScanners = new Scanner[1];
        try{
            lexiconScanners[0] = new Scanner(new File("Data/Output/Lexicon/lexicon.txt"));
            docIdsReader = new RandomAccessFile("Data/Output/DocIds/docIdsCompressed.txt", "r");
            freqReader = new RandomAccessFile("Data/Output/Frequencies/freqCompressed.txt", "r");
            documentIndexReader = new RandomAccessFile("Data/Output/DocumentIndex/documentIndexCompressed.txt", "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void closeLookupFiles(){
        try{
            lexiconScanners[0].close();
            docIdsReader.close();
            freqReader.close();
            documentIndexReader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
