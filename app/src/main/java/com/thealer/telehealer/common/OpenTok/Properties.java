package com.thealer.telehealer.common.OpenTok;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsekar on 1/30/19.
 */

public class Properties {
    List<String> supportedSpeechLanguages = new ArrayList<>();

    String currentSpeechLanguage;

    String listeningMsg;

    String oneStepVerifySpeechResult;

    long startListeningTime;

    long pauseAndSpeakTime;

    boolean offlineSpeechRecognition = false;

    boolean continuousSpeechRecognition = true;

    boolean showRecognitionProgressView = false;

    boolean oneStepResultVerify = false;

    boolean onReadyForSpeech = false;

    boolean speechResultFound = false;

    boolean closedByUser = false;
}
