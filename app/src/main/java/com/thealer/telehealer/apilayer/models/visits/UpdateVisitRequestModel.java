package com.thealer.telehealer.apilayer.models.visits;

import com.thealer.telehealer.apilayer.models.recents.DownloadTranscriptResponseModel;

import java.util.List;

/**
 * Created by Aswin on 16,April,2019
 */
public class UpdateVisitRequestModel {

    private String association_type;
    private List<Integer> add_associations;
    private List<Integer> remove_associations;
    private String instructions;
    private DownloadTranscriptResponseModel updated_transcript;

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

    public DownloadTranscriptResponseModel getUpdated_transcript() {
        return updated_transcript;
    }

    public void setUpdated_transcript(DownloadTranscriptResponseModel updated_transcript) {
        this.updated_transcript = updated_transcript;
    }
}
