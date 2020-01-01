package com.thealer.telehealer.apilayer.models.EducationalVideo;

import androidx.annotation.Nullable;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersCommonResultResponseModel;

import java.io.Serializable;

public class EducationalVideoOrder extends OrdersCommonResultResponseModel implements Serializable {

    @Nullable
    private EducationalVideo video;
    private boolean viewed;
    private int user_video_id;

    @Nullable
    public EducationalVideo getVideo() {
        return video;
    }

    public void setVideo(EducationalVideo video) {
        this.video = video;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public int getUser_video_id() {
        return user_video_id;
    }

    public void setUser_video_id(int user_video_id) {
        this.user_video_id = user_video_id;
    }
}
