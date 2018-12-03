package com.example.shayanmirjafari.integratedvms.nlp;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

/**
 * Created by shayan on 8/11/15.
 * NLP module using OpenNLP
 */
public class LanguageProcessing {


    private String directory = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"VMS";



    private LanguageProcessing(){

    }

    public static LanguageProcessing getInstance(){
        return new LanguageProcessing();
    }

    public String[] findPersonName(String speech, InfoType type) throws IOException {
        InputStream is = null;
        switch (type){
            case PersonalName:
                is = new FileInputStream(directory + File.separator + "en-ner-person.bin");

                break;
            case Organization:
                is = new FileInputStream(directory + File.separator + "en-ner-organization.bin");

                break;
            case Location:
                is = new FileInputStream(directory + File.separator + "en-ner-location.bin");


                break;
            default:
                is = new FileInputStream(directory + File.separator + "en-ner-person.bin");
                break;


        }

        TokenNameFinderModel model = new TokenNameFinderModel(is);

        NameFinderME finder = new NameFinderME(model);

        String[] sentence = tokenize(speech);

        Span[] names = finder.find(sentence);

        String[] result = new String[names.length];

        for(int i = 0; i < result.length; i++){
            result[i] = "";
            for(int j = names[i].getStart(); j < names[i].getEnd(); j++){
                result[i] += sentence[j] + " ";
            }
            result[i] = result[i].trim();

        }
        is.close();

        return result;
    }

    public String[] sentenceDetect(String speech) throws InvalidFormatException,
            IOException {
//        String paragraph = "Hi. How are you? This is Mike.";

        InputStream is = new FileInputStream(directory + File.separator +"en-sent.bin");
        SentenceModel model = new SentenceModel(is);
        SentenceDetectorME sdetector = new SentenceDetectorME(model);

        String sentences[] = sdetector.sentDetect(speech);

//        System.out.println(sentences[0]);
//        System.out.println(sentences[1]);
        is.close();
        return sentences;
    }

    public String[] tokenize(String speech) throws IOException {
        InputStream is = new FileInputStream(directory + File.separator +"en-token.bin");

        TokenizerModel model = new TokenizerModel(is);

        Tokenizer tokenizer = new TokenizerME(model);

        String tokens[] = tokenizer.tokenize(speech);

//        for (String a : tokens)
//            System.out.println(a);

        is.close();

        return tokens;
    }


    private boolean startWithCapital(String word){
        if(word != null){
            if(word.substring(0,1).equals(word.substring(0,1).toUpperCase())){
                return true;
            }
        }

        return false;
    }

}
