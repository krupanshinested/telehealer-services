package com.thealer.telehealer.views.notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.UserType;

/**
 * Created by Aswin on 31,January,2019
 */
public class NotificationCancelAppointmentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int notificationId = intent.getIntExtra(ArgumentKeys.NOTIFICATION_ID, 0);

        SchedulesApiResponseModel.ResultBean resultBean = new Gson().fromJson(intent.getStringExtra(ArgumentKeys.SCHEDULE_DETAIL), SchedulesApiResponseModel.ResultBean.class);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);

        SchedulesApiViewModel schedulesApiViewModel = new SchedulesApiViewModel(TeleHealerApplication.application);

        String currentUserGuid=resultBean.getScheduled_by_user().getUser_guid();
        if(!UserType.isUserAssistant())
            currentUserGuid="";

        schedulesApiViewModel.deleteSchedule(notificationId, resultBean.getStart(),currentUserGuid, null, false);

    }
}
