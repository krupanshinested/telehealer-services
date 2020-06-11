package com.thealer.telehealer.common.OpenTok;

import androidx.annotation.Nullable;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

public class CallSettings extends BaseApiResponseModel {
    public String apiKey;
    public String sessionId;
    public String token;
    @Nullable
    public String expiredTokenWhileCallPlaced;

    public boolean recording_enabled = false;
    public boolean transcription_enabled = false;

    public boolean canStartPublishVideo  = true;
    public boolean canStartPublishAudio = true;

    boolean getIsExpiredTokenPresent() {
        return expiredTokenWhileCallPlaced != null;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getToken() {
        return token;
    }

    @Nullable
    public String getExpiredTokenWhileCallPlaced() {
        return expiredTokenWhileCallPlaced;
    }

    public boolean isRecording_enabled() {
        return recording_enabled;
    }

    public boolean isTranscription_enabled() {
        return transcription_enabled;
    }

    public boolean isCanStartPublishVideo() {
        return canStartPublishVideo;
    }

    public boolean isCanStartPublishAudio() {
        return canStartPublishAudio;
    }
}
