package com.thealer.telehealer.apilayer.models.EducationalVideo;

import com.thealer.telehealer.apilayer.models.PaginationCommonResponseModel;

import java.io.Serializable;
import java.util.ArrayList;

public class EducationalVideoResponse extends PaginationCommonResponseModel implements Serializable {
    private ArrayList<EducationalVideo> result;

    public ArrayList<EducationalVideo> getResult() {
        return result;
    }
}
