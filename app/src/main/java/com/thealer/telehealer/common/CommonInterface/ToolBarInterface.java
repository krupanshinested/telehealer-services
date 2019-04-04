package com.thealer.telehealer.common.CommonInterface;

import android.widget.ImageView;

/**
 * Created by rsekar on 12/3/18.
 */

public interface ToolBarInterface {
    void updateTitle(String title);

    ImageView getExtraOption();

    void updateSubTitle(String subTitle, int visibility);
}
