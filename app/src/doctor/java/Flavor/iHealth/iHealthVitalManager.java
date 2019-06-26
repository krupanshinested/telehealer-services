package Flavor.iHealth;

import android.app.Application;
import androidx.annotation.NonNull;

import com.thealer.telehealer.common.VitalCommon.VitalsManager;

/**
 * Created by rsekar on 1/24/19.
 */

public class iHealthVitalManager extends VitalsManager {
    public iHealthVitalManager(@NonNull Application application) {
        super(application);
    }
}
