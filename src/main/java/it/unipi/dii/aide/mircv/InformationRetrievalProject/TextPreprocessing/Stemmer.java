package it.unipi.dii.aide.mircv.InformationRetrievalProject.TextPreprocessing;

import org.tartarus.snowball.ext.PorterStemmer;

public class Stemmer {
    public static String stemming(String document){
        PorterStemmer stem = new PorterStemmer(); //Initialize the stemmer
        StringBuilder result = new StringBuilder(); //StringBuilder to rebuild the document after split and stemming

        String[] tokens = document.split(" "); //Split the string using " " as splitter

        //Foreach token stem it and append it to the StringBuilder
        for(String token : tokens){
            stem.setCurrent(token);
            stem.stem();
            result.append(stem.getCurrent()).append(" ");
        }

        return result.toString(); //Return the "stemmed" document
    }
}
