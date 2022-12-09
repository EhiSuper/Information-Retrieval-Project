package it.unipi.dii.aide.mircv.InformationRetrievalProject.TextPreprocessing;


public class TextPreprocessing {
    static Boolean REMOVE_STOPWORDS= true;
    static Boolean STEMMING= true;

    public static String parse(String document){
        document = document.toLowerCase(); //Lowercase text
        document = document.replaceAll("\\p{Punct}", " "); //Remove punctualization
        document = document.replaceAll("[^\\x00-\\x7F]", ""); //Remove non-ascii chars
        document = document.trim().replaceAll(" +"," "); //Remove useless whitespaces (starting-ending and double+)

        if(REMOVE_STOPWORDS){
            document = StopWordsRemover.removeStopWords(document); //Remove stopwords
        }
        if(STEMMING){
            document = Stemmer.stemming(document); //Apply stemming
        }

        return document;
    }
}
