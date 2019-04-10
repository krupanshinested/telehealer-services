package com.thealer.telehealer.apilayer.models.recents;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 26,December,2018
 */
public class DownloadTranscriptResponseModel extends BaseApiResponseModel {

    private String transcripts;
    private List<SpeakerLabelsBean> speakerLabels;

    public DownloadTranscriptResponseModel() {
    }

    public DownloadTranscriptResponseModel(String transcripts, List<SpeakerLabelsBean> speakerLabels) {
        this.transcripts = transcripts;
        this.speakerLabels = speakerLabels;
    }

    public String getTranscripts() {
        return transcripts;
    }

    public void setTranscripts(String transcripts) {
        this.transcripts = transcripts;
    }

    public List<SpeakerLabelsBean> getSpeakerLabels() {
        return speakerLabels;
    }

    public void setSpeakerLabels(List<SpeakerLabelsBean> speakerLabels) {
        this.speakerLabels = speakerLabels;
    }

    public static class SpeakerLabelsBean implements Serializable {

        private String start_time;
        private String speaker_label;
        private String end_time;
        private String transcript;
        private List<ItemsBean> items;
        private boolean isRemoved;

        public SpeakerLabelsBean() {
        }

        public SpeakerLabelsBean(String start_time, String speaker_label, String end_time, String transcript, boolean isRemoved) {
            this.start_time = start_time;
            this.speaker_label = speaker_label;
            this.end_time = end_time;
            this.transcript = transcript;
            this.isRemoved = isRemoved;
        }

        public boolean isRemoved() {
            return isRemoved;
        }

        public void setRemoved(boolean removed) {
            isRemoved = removed;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getSpeaker_label() {
            return speaker_label;
        }

        public void setSpeaker_label(String speaker_label) {
            this.speaker_label = speaker_label;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getTranscript() {
            return transcript;
        }

        public void setTranscript(String transcript) {
            this.transcript = transcript;
        }

        public List<ItemsBean> getItems() {
            return items;
        }

        public void setItems(List<ItemsBean> items) {
            this.items = items;
        }

        public String getSpeakerName(TranscriptionApiResponseModel transcriptionApiResponseModel) {
            String name = getSpeaker_label();

            if (getSpeaker_label().equals("spk_0") ||
                    getSpeaker_label().equals("spk_1")) {
                int speaker = Integer.parseInt(getSpeaker_label().replace("spk_", "")) + 1;
                name = "Speaker " + speaker + " : ";
            } else if (getSpeaker_label().equals("caller") &&
                    transcriptionApiResponseModel.getCaller() != null) {
                name = transcriptionApiResponseModel.getCaller().getDisplayName();
            } else if (getSpeaker_label().equals("callee") &&
                    transcriptionApiResponseModel.getCallee() != null) {
                name = transcriptionApiResponseModel.getCallee().getDisplayName();
            }
            return name;
        }

        public boolean isModelEqual(SpeakerLabelsBean speakerLabelsBean) {
            return speakerLabelsBean.getStart_time().equals(getStart_time()) && speakerLabelsBean.getEnd_time().equals(getEnd_time())
                    && speakerLabelsBean.getTranscript().equals(getTranscript());
        }

        public static class ItemsBean implements Serializable {

            private String start_time;
            private String speaker_label;
            private String end_time;
            private String transcript;

            public String getStart_time() {
                return start_time;
            }

            public void setStart_time(String start_time) {
                this.start_time = start_time;
            }

            public String getSpeaker_label() {
                return speaker_label;
            }

            public void setSpeaker_label(String speaker_label) {
                this.speaker_label = speaker_label;
            }

            public String getEnd_time() {
                return end_time;
            }

            public void setEnd_time(String end_time) {
                this.end_time = end_time;
            }

            public String getTranscript() {
                return transcript;
            }

            public void setTranscript(String transcript) {
                this.transcript = transcript;
            }
        }
    }

    public boolean isUpdated() {
        boolean isUpdated = false;
        for (int i = 0; i < getSpeakerLabels().size(); i++) {
            if (getSpeakerLabels().get(i).isRemoved()) {
                isUpdated = true;
                break;
            }
        }
        return isUpdated;
    }
}
