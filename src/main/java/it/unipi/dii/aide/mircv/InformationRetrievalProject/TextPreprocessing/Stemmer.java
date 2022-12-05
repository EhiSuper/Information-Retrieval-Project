package it.unipi.dii.aide.mircv.InformationRetrievalProject.TextPreprocessing;

import org.tartarus.snowball.ext.PorterStemmer;

public class Stemmer {
    public static String stemming(String document){
        PorterStemmer stem = new PorterStemmer();
        StringBuilder result = new StringBuilder();
        String[] tokens = document.split(" ");
        for(String token : tokens){
            stem.setCurrent(token);
            stem.stem();
            result.append(stem.getCurrent() + " ");
        }
        return result.toString();
    }
}
