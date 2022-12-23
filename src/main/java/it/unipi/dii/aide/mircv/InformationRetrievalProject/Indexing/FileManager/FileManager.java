package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans.*;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans.Reader;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans.Writer;

import java.io.*;

public class FileManager {

    public Writer myWriterLexicon;
    public Writer myWriterDocIds;
    public Writer myWriterFreq;
    public Writer myWriterDocumentIndex;

    Reader[] lexiconScanners;
    Reader[] docIdsScanners;
    Reader[] freqScanners;
    Reader[] documentIndexScanners;

    Reader lexiconReader;
    Reader docIdsReader;
    Reader freqReader;
    Reader documentIndexReader;
    Reader collectionStatisticsReader;

    public Writer getMyWriterLexicon() {
        return myWriterLexicon;
    }

    public void setMyWriterLexicon(Writer myWriterLexicon) {
        this.myWriterLexicon = myWriterLexicon;
    }

    public Writer getMyWriterDocIds() {
        return myWriterDocIds;
    }

    public void setMyWriterDocIds(Writer myWriterDocIds) {
        this.myWriterDocIds = myWriterDocIds;
    }

    public Writer getMyWriterFreq() {
        return myWriterFreq;
    }

    public void setMyWriterFreq(Writer myWriterFreq) {
        this.myWriterFreq = myWriterFreq;
    }

    public Writer getMyWriterDocumentIndex() {
        return myWriterDocumentIndex;
    }

    public void setMyWriterDocumentIndex(Writer myWriterDocumentIndex) {
        this.myWriterDocumentIndex = myWriterDocumentIndex;
    }

    public Reader[] getLexiconScanners() {
        return lexiconScanners;
    }

    public void setLexiconScanners(Reader[] lexiconScanners) {
        this.lexiconScanners = lexiconScanners;
    }

    public Reader[] getDocIdsScanners() {
        return docIdsScanners;
    }

    public void setDocIdsScanners(Reader[] docIdsScanners) {
        this.docIdsScanners = docIdsScanners;
    }

    public Reader[] getFreqScanners() {
        return freqScanners;
    }

    public void setFreqScanners(Reader[] freqScanners) {
        this.freqScanners = freqScanners;
    }

    public Reader[] getDocumentIndexScanners() {
        return documentIndexScanners;
    }

    public void setDocumentIndexScanners(Reader[] documentIndexScanners) {
        this.documentIndexScanners = documentIndexScanners;
    }

    public Reader getLexiconReader() {
        return lexiconReader;
    }

    public void setLexiconReader(Reader lexiconReader) {
        this.lexiconReader = lexiconReader;
    }

    public Reader getDocIdsReader() {
        return docIdsReader;
    }

    public void setDocIdsReader(Reader docIdsReader) {
        this.docIdsReader = docIdsReader;
    }

    public Reader getFreqReader() {
        return freqReader;
    }

    public void setFreqReader(Reader freqReader) {
        this.freqReader = freqReader;
    }

    public Reader getDocumentIndexReader() {
        return documentIndexReader;
    }

    public void setDocumentIndexReader(Reader documentIndexReader) {
        this.documentIndexReader = documentIndexReader;
    }

    public Reader getCollectionStatisticsReader() {
        return collectionStatisticsReader;
    }

    public void setCollectionStatisticsReader(Reader collectionStatisticsReader) {
        this.collectionStatisticsReader = collectionStatisticsReader;
    }

    public void openBlockFiles(int blockCounter, String encodingType){
        myWriterLexicon = new TextWriter("Data/Output/Lexicon/lexicon" + blockCounter + ".txt");
        if(encodingType.equals("text")){
            myWriterDocIds = new TextWriter("Data/Output/DocIds/docIds" + blockCounter + ".txt");
            myWriterFreq = new TextWriter("Data/Output/Frequencies/freq" + blockCounter + ".txt");
            myWriterDocumentIndex = new TextWriter("Data/Output/DocumentIndex/documentIndex" + blockCounter + ".txt");
        }
        else{
            Compressor compressor = new VariableByteCode();
            myWriterDocIds = new ByteWriter("Data/Output/DocIds/docIds" + blockCounter + ".dat", compressor);
            myWriterFreq = new ByteWriter("Data/Output/Frequencies/freq" + blockCounter + ".dat", compressor);
            myWriterDocumentIndex = new ByteWriter("Data/Output/DocumentIndex/documentIndex" + blockCounter + ".dat", compressor);
        }
    }

    public void closeBlockFiles(){
        myWriterDocIds.close();
        myWriterFreq.close();
        myWriterLexicon.close();
        myWriterDocumentIndex.close();
    }

    public int writeOnFile(Writer writer, int number){
        return writer.write(number);
    }

    public void writeLineOnFile(TextWriter writer, String line){
        writer.write(line);
    }

    public int readFromFile(Reader reader){
        return reader.read();
    }

    public String readLineFromFile(TextReader reader){
        return reader.readLine();
    }

    public void goToOffset(RandomAccessFile file, int offset){
        try{
            file.seek(offset);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void openScanners(int blockCounter, String encodingType){
        lexiconScanners = new TextReader[blockCounter];
        for(int i = 0; i<blockCounter; i++){
            lexiconScanners[i] = new TextReader("Data/Output/Lexicon/lexicon" + i + ".txt");
        }
        if(encodingType.equals("text")){
            docIdsScanners = new TextReader[blockCounter];
            freqScanners = new TextReader[blockCounter];
            documentIndexScanners = new TextReader[blockCounter];
            for(int i = 0; i<blockCounter; i++){
                docIdsScanners[i] = new TextReader("Data/Output/DocIds/docIds" + i + ".txt");
                freqScanners[i] = new TextReader("Data/Output/Frequencies/freq" + i + ".txt");
                documentIndexScanners[i] = new TextReader("Data/Output/DocumentIndex/documentIndex" + i + ".txt");
            }
        }
        else{
            Compressor compressor = new VariableByteCode();
            docIdsScanners = new ByteReader[blockCounter];
            freqScanners = new ByteReader[blockCounter];
            documentIndexScanners = new ByteReader[blockCounter];
            for(int i = 0; i<blockCounter; i++){
                docIdsScanners[i] = new ByteReader("Data/Output/DocIds/docIds" + i + ".dat", compressor);
                freqScanners[i] = new ByteReader("Data/Output/Frequencies/freq" + i + ".dat", compressor);
                documentIndexScanners[i] = new ByteReader("Data/Output/DocumentIndex/documentIndex" + i + ".dat", compressor);
            }
        }
    }

    public void closeScanners() {
        for (int i = 0; i<lexiconScanners.length; i++){
            lexiconScanners[i].close();
            docIdsScanners[i].close();
            freqScanners[i].close();
            documentIndexScanners[i].close();
        }
    }

    public void openMergeFiles(String encodingType){
        myWriterLexicon = new TextWriter("Data/Output/Lexicon/lexicon.txt");
        if(encodingType.equals("text")){
            myWriterDocIds = new TextWriter("Data/Output/DocIds/docIds.txt");
            myWriterFreq = new TextWriter("Data/Output/Frequencies/freq.txt");
            myWriterDocumentIndex = new TextWriter("Data/Output/DocumentIndex/documentIndex.txt");
        }
        else{
            Compressor compressor = new VariableByteCode();
            myWriterDocIds = new ByteWriter("Data/Output/DocIds/docIds.dat", compressor);
            myWriterFreq = new ByteWriter("Data/Output/Frequencies/freq.dat", compressor);
            myWriterDocumentIndex = new ByteWriter("Data/Output/DocumentIndex/documentIndex.dat", compressor);
        }
    }

    public void closeMergeFiles(){
        myWriterDocIds.close();
        myWriterFreq.close();
        myWriterLexicon.close();
        myWriterDocumentIndex.close();
    }

    public void openLookupFiles(String encodingType){
        lexiconReader = new TextReader("Data/Output/Lexicon/lexicon.txt");
        collectionStatisticsReader = new TextReader("Data/Output/CollectionStatistics/collectionStatistics.txt");
        if(encodingType.equals("text")){
            docIdsReader = new RandomAccessTextReader("Data/Output/DocIds/docIds.txt");
            freqReader = new RandomAccessTextReader("Data/Output/Frequencies/freq.txt");
            documentIndexReader = new RandomAccessTextReader("Data/Output/DocumentIndex/documentIndex.txt");
        }
        else{
            Compressor compressor = new VariableByteCode();
            docIdsReader = new RandomAccessByteReader("Data/Output/DocIds/docIds.dat", compressor);
            freqReader = new RandomAccessByteReader("Data/Output/Frequencies/freq.dat", compressor);
            documentIndexReader = new RandomAccessByteReader("Data/Output/DocumentIndex/documentIndex.dat", compressor);
        }
    }

    public void closeLookupFiles(){
        lexiconReader.close();
        docIdsReader.close();
        freqReader.close();
        documentIndexReader.close();
        collectionStatisticsReader.close();
    }

    public boolean hasNextLine(TextReader reader) {
        return reader.hasNextLine();
    }
}
