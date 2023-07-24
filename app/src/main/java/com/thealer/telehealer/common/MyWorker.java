package com.thealer.telehealer.common;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.models.OpenTok.CallRequest;
import com.thealer.telehealer.common.OpenTok.CallManager;
import com.thealer.telehealer.common.OpenTok.CallNotificationService;
import com.thealer.telehealer.common.OpenTok.OpenTok;
import com.thealer.telehealer.views.call.CallActivity;

public class MyWorker extends Worker {
    private final Context context;
    private String TAG = "MyWorker";

    public MyWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork called for: " + this.getId());

        createNotification(context);

        return Result.success();
    }

    private void createNotification(Context application) {
        OpenTok currentCall = CallManager.shared.getActiveCallToShow();
        CallRequest callRequest = null;
        if (currentCall != null)
            callRequest = currentCall.getCallRequest();
        boolean isWaitingRoom = false;
        if (callRequest != null) {
            Intent fullScreenIntent = CallActivity.getCallIntent((Application) application, isWaitingRoom, null, callRequest);

            PendingIntent fullScreenPendingIntent;/* = PendingIntent.getActivity(getApplication(), 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);*/
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                fullScreenPendingIntent = PendingIntent.getActivity(application, 0, fullScreenIntent, PendingIntent.FLAG_MUTABLE);
            } else {
                fullScreenPendingIntent = PendingIntent.getActivity(application, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            Intent acceptScreenIntent = CallActivity.getCallIntent((Application) application, isWaitingRoom, true, callRequest);
            PendingIntent acceptScreenPendingIntent; /* = PendingIntent.getActivity(application, 1, acceptScreenIntent, PendingIntent.FLAG_ONE_SHOT);*/
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                acceptScreenPendingIntent = PendingIntent.getActivity(application, 1, acceptScreenIntent, PendingIntent.FLAG_MUTABLE);
            } else {
                acceptScreenPendingIntent = PendingIntent.getActivity(application, 1, acceptScreenIntent, PendingIntent.FLAG_ONE_SHOT);
            }

            Intent rejectScreenIntent = CallActivity.getCallIntent((Application) application, isWaitingRoom, false, callRequest);
            PendingIntent rejectScreenPendingIntent; /*= PendingIntent.getActivity(application, 2,rejectScreenIntent, PendingIntent.FLAG_ONE_SHOT);*/
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                rejectScreenPendingIntent = PendingIntent.getActivity(application, 2, rejectScreenIntent, PendingIntent.FLAG_MUTABLE);
            } else {
                rejectScreenPendingIntent = PendingIntent.getActivity(application, 2, rejectScreenIntent, PendingIntent.FLAG_ONE_SHOT);
            }

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(application, TeleHealerApplication.appConfig.getVoipChannel())
                            .setSmallIcon(R.drawable.app_icon)
                            .setContentTitle(application.getString(R.string.app_name) + " Incoming call")
                            .setContentText(callRequest.getDoctorName())
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setCategory(NotificationCompat.CATEGORY_CALL)
                            .setFullScreenIntent(fullScreenPendingIntent, true)
                            .addAction(new NotificationCompat.Action.Builder(R.drawable.app_icon, "Reject", rejectScreenPendingIntent).build())
                            .addAction(new NotificationCompat.Action.Builder(R.drawable.app_icon, "Accept", acceptScreenPendingIntent).build());

            Notification incomingCallNotification = notificationBuilder.build();

            Log.d("CallNotificationService", "start pending indent");
            NotificationManager mNotificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(CallActivity.VOIP_NOTIFICATION_ID, incomingCallNotification);

        }
    }


    @Override
    public void onStopped() {
        Log.d(TAG, "onStopped called for: " + this.getId());
        super.onStopped();
    }
}