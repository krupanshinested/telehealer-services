package com.thealer.telehealer.apilayer.models.EducationalVideo;


import com.thealer.telehealer.apilayer.models.OpenTok.CallRequest;
import com.thealer.telehealer.common.OpenTok.CallSettings;

import java.io.Serializable;

public class EducationalFetchModel extends CallSettings implements Serializable {
    private int videoId;

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }
}
