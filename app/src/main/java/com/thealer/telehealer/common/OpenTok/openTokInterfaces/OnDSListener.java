package com.thealer.telehealer.common.OpenTok.openTokInterfaces;

import java.util.List;

/**
 * Created by rsekar on 2/4/19.
 */

public interface OnDSListener {
    void onDroidSpeechSupportedLanguages(String currentSpeechLanguage, List<String> supportedSpeechLanguages);

    void onDroidSpeechRmsChanged(float rmsChangedValue);

    void onDroidSpeechLiveResult(String liveSpeechResult);

    void onDroidSpeechFinalResult(String finalSpeechResult);

    void onDroidSpeechClosedByUser();

    void onDroidSpeechError(String errorMsg);
}
