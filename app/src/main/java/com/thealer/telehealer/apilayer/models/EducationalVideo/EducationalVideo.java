package com.thealer.telehealer.apilayer.models.EducationalVideo;

import androidx.annotation.Nullable;

import com.thealer.telehealer.common.BaseAdapterObjectModel;
import com.thealer.telehealer.common.Utils;

import java.io.Serializable;
import java.util.Date;

public class EducationalVideo extends BaseAdapterObjectModel implements Serializable {
    private String url;
    private int video_id;
    private String title;
    private String description;
    private String audio_stream_screenshot;
    private int created_by;
    private Date created_at;

    @Override
    public String getAdapterTitle() {
        return Utils.getStringFromDate(created_at,Utils.defaultDateFormat);
    }

    @Override
    public Object getComparableObject() {
        return created_at;
    }

    public String getUrl() {
        return url;
    }

    public int getVideo_id() {
        return video_id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAudio_stream_screenshot() {
        return audio_stream_screenshot;
    }

    public int getCreated_by() {
        return created_by;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
