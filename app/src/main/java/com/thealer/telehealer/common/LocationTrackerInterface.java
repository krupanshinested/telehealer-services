package com.thealer.telehealer.common;

import android.support.annotation.Nullable;

public interface LocationTrackerInterface{
    void onLocationUpdated(@Nullable String city,@Nullable String state);
}
