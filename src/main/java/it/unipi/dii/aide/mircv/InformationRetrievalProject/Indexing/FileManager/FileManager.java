package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager;

import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans.*;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans.Reader;
import it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing.FileManager.Beans.Writer;

import java.io.*;

//class that manages the interaction with files.
public class FileManager {

    //writers used to write to blocks during indexing and then for merging.
    public Writer myWriterLexicon;
    public Writer myWriterDocIds;
    public Writer myWriterFreq;
    public Writer myWriterDocumentIndex;
    public Writer myWriterLastDocIds;
    public Writer myWriterSkipPointers;

    //readers used during the merging phase
    Reader[] lexiconScanners;
    Reader[] docIdsScanners;
    Reader[] freqScanners;
    Reader[] documentIndexScanners;

    //readers used during the query processing lookup phase
    Reader lexiconReader;
    Reader docIdsReader;
    Reader freqReader;
    Reader documentIndexReader;
    Reader collectionStatisticsReader;
    Reader lastDocIdsReader;
    Reader skipPointersReader;

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

    public Reader getDocIdsReader() { return docIdsReader; }

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

    public Writer getMyWriterLastDocIds() {
        return myWriterLastDocIds;
    }

    public void setMyWriterLastDocIds(Writer myWriterLastDocIds) {
        this.myWriterLastDocIds = myWriterLastDocIds;
    }

    public Writer getMyWriterSkipPointers() {
        return myWriterSkipPointers;
    }

    public void setMyWriterSkipPointers(Writer myWriterSkipPointers) {
        this.myWriterSkipPointers = myWriterSkipPointers;
    }

    public Reader getLastDocIdsReader() {
        return lastDocIdsReader;
    }

    public void setLastDocIdsReader(Reader lastDocIdsReader) {
        this.lastDocIdsReader = lastDocIdsReader;
    }

    public Reader getSkipPointersReader() {
        return skipPointersReader;
    }

    public void setSkipPointersReader(Reader skipPointersReader) {
        this.skipPointersReader = skipPointersReader;
    }

    //function that opens the right block files during indexing
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

    //function that closes block files during indexing
    public void closeBlockFiles(){
        myWriterDocIds.close();
        myWriterFreq.close();
        myWriterLexicon.close();
        myWriterDocumentIndex.close();
    }

    //function used to write to a file an int. The function accept a writer so it is independent of the encoding type
    public int writeOnFile(Writer writer, int number){
        return writer.write(number);
    }

    //function used to specifically write a string to a text file.
    public void writeLineOnFile(TextWriter writer, String line){
        writer.write(line);
    }

    //function used to read an int from a provided reader.
    public int readFromFile(Reader reader){
        return reader.read();
    }

    //function used to specifically read a text line from a text file.
    public String readLineFromFile(TextReader reader){
        return reader.readLine();
    }

    //function used to skip to a specific offset passed as int of the specified file.
    public void goToOffset(RandomByteReader file, int offset){
        file.goToOffset(offset);
    }

    //function that opens the block scanners during the merging phase.
    //depending on the encoding used it opens the right files.
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

    //function that closes the scanners used during merging.
    public void closeScanners() {
        for (int i = 0; i<lexiconScanners.length; i++){
            lexiconScanners[i].close();
            docIdsScanners[i].close();
            freqScanners[i].close();
            documentIndexScanners[i].close();
        }
    }

    //function that opens the final files for the merge phase.
    //Depending on the encoding it opens the right files.
    public void openMergeFiles(String encodingType){
        myWriterLexicon = new TextWriter("Data/Output/Lexicon/lexicon.txt");
        if(encodingType.equals("text")){
            myWriterDocIds = new TextWriter("Data/Output/DocIds/docIds.txt");
            myWriterFreq = new TextWriter("Data/Output/Frequencies/freq.txt");
            myWriterDocumentIndex = new TextWriter("Data/Output/DocumentIndex/documentIndex.txt");
            myWriterLastDocIds = new TextWriter("Data/Output/Skipping/lastDocIds.txt");
            myWriterSkipPointers = new TextWriter("Data/Output/Skipping/skipPointers.txt");
        }
        else{
            Compressor compressor = new VariableByteCode();
            myWriterDocIds = new ByteWriter("Data/Output/DocIds/docIds.dat", compressor);
            myWriterFreq = new ByteWriter("Data/Output/Frequencies/freq.dat", compressor);
            myWriterDocumentIndex = new ByteWriter("Data/Output/DocumentIndex/documentIndex.dat", compressor);
            myWriterLastDocIds = new ByteWriter("Data/Output/Skipping/lastDocIds.dat", compressor);
            myWriterSkipPointers = new ByteWriter("Data/Output/Skipping/skipPointers.dat", compressor);
        }
    }

    //function that closes the merge files.
    public void closeMergeFiles(){
        myWriterDocIds.close();
        myWriterFreq.close();
        myWriterLexicon.close();
        myWriterDocumentIndex.close();
        myWriterLastDocIds.close();
        myWriterSkipPointers.close();
    }

    //function that opens the lookup files for the lookup phase.
    public void openLookupFiles() {
        Compressor compressor = new VariableByteCode();
        docIdsReader = new RandomByteReader("Data/Output/DocIds/docIds.dat", compressor);
        freqReader = new RandomByteReader("Data/Output/Frequencies/freq.dat", compressor);
        lastDocIdsReader = new RandomByteReader("Data/Output/Skipping/lastDocIds.dat", compressor);
        skipPointersReader = new RandomByteReader("Data/Output/Skipping/skipPointers.dat", compressor);
    }

    //function that closes the lookup files.
    public void closeLookupFiles(){
        docIdsReader.close();
        freqReader.close();
        lastDocIdsReader.close();
        skipPointersReader.close();
    }

    public void openObtainFiles(){
        Compressor compressor = new VariableByteCode();
        lexiconReader = new TextReader("Data/Output/Lexicon/lexicon.txt");
        collectionStatisticsReader = new TextReader("Data/Output/CollectionStatistics/collectionStatistics.txt");
        documentIndexReader = new ByteReader("Data/Output/DocumentIndex/documentIndex.dat", compressor);
    }

    public void closeObtainFiles(){
        lexiconReader.close();
        collectionStatisticsReader.close();
        documentIndexReader.close();
    }

    //function that checks if a text file has a next line.
    public boolean hasNextLine(TextReader reader) {
        return reader.hasNextLine();
    }
}
