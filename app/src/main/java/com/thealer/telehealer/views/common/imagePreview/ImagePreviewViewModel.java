package com.thealer.telehealer.views.common.imagePreview;

import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 06,December,2018
 */
public class ImagePreviewViewModel extends ViewModel {
    private List<String> imageList = new ArrayList<>();

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }
}
