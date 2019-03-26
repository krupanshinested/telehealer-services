package com.thealer.telehealer.apilayer.models.vitals;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 25,March,2019
 */
public class StethBean implements Serializable {
    private String event_created;
    private String event_uuid;
    private List<SegmentsBean> segments;

    public String getEvent_created() {
        return event_created;
    }

    public void setEvent_created(String event_created) {
        this.event_created = event_created;
    }

    public String getEvent_uuid() {
        return event_uuid;
    }

    public void setEvent_uuid(String event_uuid) {
        this.event_uuid = event_uuid;
    }

    public List<SegmentsBean> getSegments() {
        return segments;
    }

    public void setSegments(List<SegmentsBean> segments) {
        this.segments = segments;
    }

    public static class SegmentsBean implements Serializable {

        private String ai_response;
        private String phonogram_file;
        private int segment_id;
        private double segment_duration;
        private String filter_type;
        private String audio_file;

        public String getAi_response() {
            return ai_response;
        }

        public void setAi_response(String ai_response) {
            this.ai_response = ai_response;
        }

        public String getPhonogram_file() {
            return phonogram_file;
        }

        public void setPhonogram_file(String phonogram_file) {
            this.phonogram_file = phonogram_file;
        }

        public int getSegment_id() {
            return segment_id;
        }

        public void setSegment_id(int segment_id) {
            this.segment_id = segment_id;
        }

        public double getSegment_duration() {
            return segment_duration;
        }

        public void setSegment_duration(double segment_duration) {
            this.segment_duration = segment_duration;
        }

        public String getFilter_type() {
            return filter_type;
        }

        public void setFilter_type(String filter_type) {
            this.filter_type = filter_type;
        }

        public String getAudio_file() {
            return audio_file;
        }

        public void setAudio_file(String audio_file) {
            this.audio_file = audio_file;
        }
    }
}

