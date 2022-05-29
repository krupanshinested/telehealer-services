package com.thealer.telehealer.apilayer.models.feedback;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedbackResponseModel {
    @SerializedName("answer")
    @Expose
    private String answer;
    @SerializedName("feedbacks_questions_id")
    @Expose
    private Integer feedbacksQuestionsId;
    @SerializedName("question")
    @Expose
    private String question;

    public FeedbackResponseModel(Integer questionid, String question, String value) {
        this.answer = value;
        this.feedbacksQuestionsId=questionid;
        this.question = question;

    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getFeedbacksQuestionsId() {
        return feedbacksQuestionsId;
    }

    public void setFeedbacksQuestionsId(Integer feedbacksQuestionsId) {
        this.feedbacksQuestionsId = feedbacksQuestionsId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
