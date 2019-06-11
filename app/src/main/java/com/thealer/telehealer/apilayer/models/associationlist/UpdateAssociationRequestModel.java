package com.thealer.telehealer.apilayer.models.associationlist;

import java.io.Serializable;

/**
 * Created by Aswin on 11,June,2019
 */
public class UpdateAssociationRequestModel implements Serializable {

    private boolean favorite;

    public UpdateAssociationRequestModel(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
