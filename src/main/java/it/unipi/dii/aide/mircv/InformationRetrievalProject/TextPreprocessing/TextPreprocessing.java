package it.unipi.dii.aide.mircv.InformationRetrievalProject.TextPreprocessing;


public class TextPreprocessing {
    static Boolean STEMMING_AND_STOPWORDS = false;
    public static String parse(String document){
        document = document.toLowerCase().replaceAll("\\p{Punct}", " ");
        if (STEMMING_AND_STOPWORDS){
            document = StopWordsRemover.removeStopWords(document);
            document = Stemmer.stemming(document);
        }
        return document;
    }
}
