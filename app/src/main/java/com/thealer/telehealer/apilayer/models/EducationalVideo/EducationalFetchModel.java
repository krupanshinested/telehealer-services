package com.thealer.telehealer.apilayer.models.EducationalVideo;

import com.thealer.telehealer.apilayer.models.OpenTok.TokenFetchModel;

import java.io.Serializable;

public class EducationalFetchModel extends TokenFetchModel implements Serializable {
    private int videoId;

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }
}
