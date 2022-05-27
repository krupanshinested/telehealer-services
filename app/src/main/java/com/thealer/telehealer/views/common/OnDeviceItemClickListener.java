package com.thealer.telehealer.views.common;

import android.os.Bundle;

/**
 * Created by Binjal Nakrani
 * Created Date: 03,JAN,2022
 **/
public interface OnDeviceItemClickListener {
    void  onItemClick(int position, Bundle bundle);
    void  onItemDeleteClick(int position);
}
