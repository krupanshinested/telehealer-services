package com.thealer.telehealer.common.OpenTok;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.models.OpenTok.CallRequest;
import com.thealer.telehealer.views.call.CallActivity;


public class CallNotificationService extends Service {

    @Override
    public void onCreate() {
        Log.d("CallNotificationService", "onCreate");

        OpenTok currentCall = CallManager.shared.getActiveCallToShow();
        CallRequest callRequest = null;
        if (currentCall != null)
            callRequest = currentCall.getCallRequest();
        boolean isWaitingRoom = false;
        if (callRequest != null) {
            Intent fullScreenIntent = CallActivity.getCallIntent(getApplication(), isWaitingRoom, null, callRequest);

            PendingIntent fullScreenPendingIntent;/* = PendingIntent.getActivity(getApplication(), 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);*/
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                fullScreenPendingIntent = PendingIntent.getActivity(getApplication(), 0, fullScreenIntent, PendingIntent.FLAG_IMMUTABLE);
            } else {
                fullScreenPendingIntent = PendingIntent.getActivity(getApplication(), 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            Intent acceptScreenIntent = CallActivity.getCallIntent(getApplication(), isWaitingRoom, true, callRequest);
            PendingIntent acceptScreenPendingIntent; /* = PendingIntent.getActivity(getApplication(), 1, acceptScreenIntent, PendingIntent.FLAG_ONE_SHOT);*/
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                acceptScreenPendingIntent = PendingIntent.getActivity(getApplication(), 1, acceptScreenIntent, PendingIntent.FLAG_IMMUTABLE);
            } else {
                acceptScreenPendingIntent = PendingIntent.getActivity(getApplication(), 1, acceptScreenIntent, PendingIntent.FLAG_ONE_SHOT);
            }

            Intent rejectScreenIntent = CallActivity.getCallIntent(getApplication(), isWaitingRoom, false, callRequest);
            PendingIntent rejectScreenPendingIntent; /*= PendingIntent.getActivity(getApplication(), 2,rejectScreenIntent, PendingIntent.FLAG_ONE_SHOT);*/
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                rejectScreenPendingIntent = PendingIntent.getActivity(getApplication(), 2, rejectScreenIntent, PendingIntent.FLAG_IMMUTABLE);
            } else {
                rejectScreenPendingIntent = PendingIntent.getActivity(getApplication(), 2, rejectScreenIntent, PendingIntent.FLAG_ONE_SHOT);
            }

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(getApplication(), TeleHealerApplication.appConfig.getVoipChannel())
                            .setSmallIcon(R.drawable.app_icon)
                            .setContentTitle(getApplication().getString(R.string.app_name) + " Incoming call")
                            .setContentText(callRequest.getDoctorName())
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_CALL)
                            .setFullScreenIntent(fullScreenPendingIntent, true)
                            .addAction(new NotificationCompat.Action.Builder(R.drawable.app_icon, "Reject", rejectScreenPendingIntent).build())
                            .addAction(new NotificationCompat.Action.Builder(R.drawable.app_icon, "Accept", acceptScreenPendingIntent).build());

            Notification incomingCallNotification = notificationBuilder.build();

            Log.d("CallNotificationService", "start pending indent");
            startForeground(CallActivity.VOIP_NOTIFICATION_ID, incomingCallNotification);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}