package com.thealer.telehealer.apilayer.models.visits;

import com.thealer.telehealer.apilayer.models.commonResponseModel.QuestionnaireBean;
import com.thealer.telehealer.apilayer.models.procedure.ProcedureModel;
import com.thealer.telehealer.apilayer.models.recents.DownloadTranscriptResponseModel;
import com.thealer.telehealer.apilayer.models.recents.VisitDiagnosisModel;
import com.thealer.telehealer.views.home.recents.VisitDetailViewModel;

import java.util.List;

/**
 * Created by Aswin on 16,April,2019
 */
public class UpdateVisitRequestModel {

    private String association_type;
    private List<Integer> add_associations;
    private List<Integer> remove_associations;
    private String instructions;
    private VisitDiagnosisModel diagnosis;
    private DownloadTranscriptResponseModel updated_transcript;
    private QuestionnaireBean questionnaire;
    private List<VisitDetailViewModel.UpdatedHistoryBean> patient_history;
    private ProcedureModel procedure;

    public String getAssociation_type() {
        return association_type;
    }

    public void setAssociation_type(String association_type) {
        this.association_type = association_type;
    }

    public List<Integer> getAdd_associations() {
        return add_associations;
    }

    public void setAdd_associations(List<Integer> add_associations) {
        this.add_associations = add_associations;
    }

    public List<Integer> getRemove_associations() {
        return remove_associations;
    }

    public void setRemove_associations(List<Integer> remove_associations) {
        this.remove_associations = remove_associations;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public VisitDiagnosisModel getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(VisitDiagnosisModel diagnosis) {
        this.diagnosis = diagnosis;
    }

    public DownloadTranscriptResponseModel getUpdated_transcript() {
        return updated_transcript;
    }

    public void setUpdated_transcript(DownloadTranscriptResponseModel updated_transcript) {
        this.updated_transcript = updated_transcript;
    }

    public QuestionnaireBean getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(QuestionnaireBean questionnaire) {
        this.questionnaire = questionnaire;
    }

    public List<VisitDetailViewModel.UpdatedHistoryBean> getPatient_history() {
        return patient_history;
    }

    public void setPatient_history(List<VisitDetailViewModel.UpdatedHistoryBean> patient_history) {
        this.patient_history = patient_history;
    }

    public ProcedureModel getProcedure() {
        return procedure;
    }

    public void setProcedure(ProcedureModel procedure) {
        this.procedure = procedure;
    }
}
