package com.thealer.telehealer.apilayer.models.medicalHistory;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 23,January,2019
 */
public class FamilyHistoryModel extends MedicalHistoryCommonModel implements Serializable {

    private List<String> selectedRelations;

    public List<String> getSelectedRelations() {
        return selectedRelations;
    }

    public void setSelectedRelations(List<String> selectedRelations) {
        this.selectedRelations = selectedRelations;
    }

    public String getSelectedRelationsString() {
        return selectedRelations.toString().replace("[", "").replace("]", "");
    }
}
