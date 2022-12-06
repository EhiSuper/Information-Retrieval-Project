package it.unipi.dii.aide.mircv.InformationRetrievalProject.TextPreprocessing;


public class TextPreprocessing {
    static Boolean REMOVE_STOPWORDS= true;
    static Boolean STEMMING= false;
    public static String parse(String document){
        document = document.toLowerCase(); //Lowercase text
        document = document.replaceAll("\\p{Punct}", " "); //Remove punctualization
        document = document.replaceAll("[^\\x00-\\x7F]", ""); //Remove non-ascii chars

        if(REMOVE_STOPWORDS){
            document = StopWordsRemover.removeStopWords(document); //Remove stopwords
        }
        if(STEMMING){
            document = Stemmer.stemming(document); //Apply stemming
        }

        return document;
    }
}
