package com.thealer.telehealer.apilayer.models.feedback.question;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class FeedbackQuestionModel {

    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class Datum {

        @SerializedName("feedbacks_questions_id")
        @Expose
        private Integer feedbacksQuestionsId;
        @SerializedName("feedback_type")
        @Expose
        private String feedbackType;
        @SerializedName("questions_type")
        @Expose
        private String questionsType;
        @SerializedName("options")
        @Expose
        private List<String> options = null;
        @SerializedName("question")
        @Expose
        private String question;
        @SerializedName("is_main_question")
        @Expose
        private Boolean isMainQuestion;
        @SerializedName("main_question_answer")
        @Expose
        private String mainQuestionAnswer;
        @SerializedName("is_physicians_question")
        @Expose
        private Boolean isPhysiciansQuestion;
        private String userSelect;

        public Integer getFeedbacksQuestionsId() {
            return feedbacksQuestionsId;
        }

        public void setFeedbacksQuestionsId(Integer feedbacksQuestionsId) {
            this.feedbacksQuestionsId = feedbacksQuestionsId;
        }

        public String getuserSelect() {
            return userSelect;
        }

        public void setuserSelect(String userSelect) {
            this.userSelect = userSelect;
        }

        public String getFeedbackType() {
            return feedbackType;
        }

        public void setFeedbackType(String feedbackType) {
            this.feedbackType = feedbackType;
        }

        public String getQuestionsType() {
            return questionsType;
        }

        public void setQuestionsType(String questionsType) {
            this.questionsType = questionsType;
        }

        public List<String> getOptions() {
            return options;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public Boolean getIsMainQuestion() {
            return isMainQuestion;
        }

        public void setIsMainQuestion(Boolean isMainQuestion) {
            this.isMainQuestion = isMainQuestion;
        }

        public String getMainQuestionAnswer() {
            return mainQuestionAnswer;
        }

        public void setMainQuestionAnswer(String mainQuestionAnswer) {
            this.mainQuestionAnswer = mainQuestionAnswer;
        }

        public Boolean getIsPhysiciansQuestion() {
            return isPhysiciansQuestion;
        }

        public void setIsPhysiciansQuestion(Boolean isPhysiciansQuestion) {
            this.isPhysiciansQuestion = isPhysiciansQuestion;
        }

    }

}
