package com.thealer.telehealer.common;

import androidx.annotation.Nullable;

public interface LocationTrackerInterface {
    void onLocationUpdated(@Nullable String city, @Nullable String state);
}
