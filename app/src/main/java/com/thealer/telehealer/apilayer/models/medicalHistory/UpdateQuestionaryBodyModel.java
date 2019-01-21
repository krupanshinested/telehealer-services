package com.thealer.telehealer.apilayer.models.medicalHistory;

import com.thealer.telehealer.apilayer.models.commonResponseModel.QuestionnaireBean;

/**
 * Created by Aswin on 22,January,2019
 */
public class UpdateQuestionaryBodyModel {

    private QuestionnaireBean questionnaire;

    public QuestionnaireBean getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(QuestionnaireBean questionnaire) {
        this.questionnaire = questionnaire;
    }
}
