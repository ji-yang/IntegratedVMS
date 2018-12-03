package com.example.shayanmirjafari.integratedvms.listener;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;

/**
 * Created by shayan on 8/11/15.
 */
public class CustomizedVoiceListener implements RecognitionListener {

    private static OnCallBackListener listener;
    private SpeechRecognizer speech;
    private Intent intent;

    private CustomizedVoiceListener(final SpeechRecognizer speech, final Intent intent, final OnCallBackListener listener){
        this.speech = speech;
        this.listener = listener;
        this.intent = intent;
    }

    public static CustomizedVoiceListener create(final SpeechRecognizer speech, final Intent intent, final OnCallBackListener listener){
        return new CustomizedVoiceListener(speech, intent, listener);
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
        speech.startListening(intent);
    }

    @Override
    public void onError(int i) {

    }

    @Override
    public void onResults(Bundle bundle) {
//        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = bundle
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        listener.onRecognitionResult(matches.get(0));
        speech.stopListening();

    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }


}
