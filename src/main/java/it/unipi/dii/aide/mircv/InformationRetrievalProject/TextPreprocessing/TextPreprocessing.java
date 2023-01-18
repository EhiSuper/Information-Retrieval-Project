package it.unipi.dii.aide.mircv.InformationRetrievalProject.TextPreprocessing;


public class TextPreprocessing {
    static Boolean stopwordsRemoval= true;
    static Boolean wordsStemming= false;

    public TextPreprocessing(Boolean stopwordsRemoval, Boolean wordsStemming){
        this.stopwordsRemoval = stopwordsRemoval;
        this.wordsStemming = wordsStemming;
    }

    public static String parse(String document){
        document = document.toLowerCase(); //Lowercase text
        document = document.replaceAll("[^a-zA-Z0-9]", " "); //Remove punctualization and non-ascii chars
        document = document.trim().replaceAll(" +"," "); //Remove useless whitespaces (starting-ending and double+)

        if(stopwordsRemoval){
            document = StopWordsRemover.removeStopWords(document); //Remove stopwords
        }
        if(wordsStemming){
            document = Stemmer.stemming(document); //Apply stemming
        }

        return document;
    }
}
