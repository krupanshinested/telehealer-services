package com.thealer.telehealer.common.CommonInterface;

import android.opengl.Visibility;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by rsekar on 12/3/18.
 */

public interface ToolBarInterface {
    public void updateTitle(String title);
    public ImageView getExtraOption();
    public void updateSubTitle(String subTitle,int visibility);
}
