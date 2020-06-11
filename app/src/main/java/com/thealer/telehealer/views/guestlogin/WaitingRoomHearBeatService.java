package com.thealer.telehealer.views.guestlogin;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.models.OpenTok.CallRequest;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.OpenTok.CallManager;
import com.thealer.telehealer.common.OpenTok.OpenTok;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.views.call.CallActivity;
import com.thealer.telehealer.views.guestlogin.screens.GuestLoginScreensActivity;

import static com.thealer.telehealer.TeleHealerApplication.appConfig;


public class WaitingRoomHearBeatService extends Service {

    private boolean isStopped;
    private int NOTIFICATION_ID = 9898987;
    Handler handler = new Handler();
    private int INTERVAL = 1000 * 60 * 4;
    private Runnable periodicUpdate = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(periodicUpdate, INTERVAL);
            Log.d("WaitingRoomService","WAITING_ROOM_HEART_BEAT");
            Log.d("WaitingRoomService","isStopped"+isStopped);
            if (!isStopped) {
                Intent i = new Intent(ArgumentKeys.WAITING_ROOM_HEART_BEAT);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
            }

        }
    };


    @Override
    public void onCreate() {
        Log.d("WaitingRoomService","onCreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isStopped=false;
        Log.d("onStartCommand","isStopped"+isStopped);
        Log.d("WaitingRoomService","onStartCommand");
        handler.post(periodicUpdate);
        startForeground(NOTIFICATION_ID, createNotification(this));
        return START_REDELIVER_INTENT ;
    }

    @Override
    public void onDestroy() {
        isStopped=true;
        handler.removeCallbacks(periodicUpdate);
        Log.d("onDestroy","isStopped"+isStopped);
        Log.d("WaitingRoomService","onDestroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static Notification createNotification(Context context) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context,TeleHealerApplication.appConfig.getApnsChannel());
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.drawable.app_icon);
        builder.setContentTitle(context.getString(R.string.app_name)+" waiting Room");
        builder.setContentText("You are a in waiting room");
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_MIN);
        builder.setCategory(NotificationCompat.CATEGORY_SERVICE);
        return builder.build();
    }
}