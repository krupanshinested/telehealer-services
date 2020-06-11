package com.thealer.telehealer.views.home.schedules;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.FutureTarget;
import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.OpenTok.CallRequest;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.CallPlacingActivity;
import com.thealer.telehealer.views.notification.NotificationCancelAppointmentReceiver;
import com.thealer.telehealer.views.notification.NotificationDetailActivity;

import java.util.UUID;

import static com.thealer.telehealer.TeleHealerApplication.notificationChannelId;

/**
 * Created by Aswin on 30,January,2019
 */
public class LocalNotificationReceiver extends BroadcastReceiver {
    Bitmap imageBitmap;

    public void onReceive(Context context, Intent intent) {


        if (intent != null) {

            SchedulesApiResponseModel.ResultBean resultBean = null;

            if (intent.getStringExtra(ArgumentKeys.SCHEDULE_DETAIL) != null) {
                resultBean = new Gson().fromJson(intent.getStringExtra(ArgumentKeys.SCHEDULE_DETAIL), SchedulesApiResponseModel.ResultBean.class);
            }


            if (resultBean != null) {

                String imageUrl = resultBean.getScheduledToUser().getUser_avatar();

                imageBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_placeholder);

                if (imageUrl != null) {

                    SchedulesApiResponseModel.ResultBean finalResultBean = resultBean;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                GlideUrl glideUrl = Utils.getGlideUrlWithAuth(context, imageUrl, true);

                                FutureTarget<Bitmap> futureTarget = Glide.with(context).asBitmap().load(glideUrl).submit();
                                imageBitmap = futureTarget.get();

                                createNotification(context, finalResultBean, imageBitmap);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();

                } else {
                    createNotification(context, resultBean, imageBitmap);
                }
            }
        }

    }

    private void createNotification(Context context, SchedulesApiResponseModel.ResultBean resultBean, Bitmap imageBitmap) {

        // Notification Click intent
        Intent contentIntent = new Intent(context, NotificationDetailActivity.class);
        contentIntent.putExtra(ArgumentKeys.SCHEDULE_DETAIL, new Gson().toJson(resultBean));
        contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(contentIntent);

        PendingIntent contentPendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Cancel click intent
        Intent cancelIntent = new Intent(context, NotificationCancelAppointmentReceiver.class);
        cancelIntent.putExtra(ArgumentKeys.SCHEDULE_DETAIL, new Gson().toJson(resultBean));
        cancelIntent.putExtra(ArgumentKeys.NOTIFICATION_ID, resultBean.getSchedule_id());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, resultBean.getSchedule_id(), cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Call click intent
        Intent callIntent = new Intent(context, CallPlacingActivity.class);
        CallRequest callRequest = new CallRequest(UUID.randomUUID().toString(),
                resultBean.getPatient().getUser_guid(), resultBean.getPatient(), resultBean.getDoctor().getUser_guid(), resultBean.getDoctor().getUserDisplay_name(), resultBean.getSchedule_id() + "", OpenTokConstants.video,true,resultBean.getSchedule_id() + "");
        callIntent.putExtra(ArgumentKeys.CALL_INITIATE_MODEL, callRequest);

        TaskStackBuilder builder = TaskStackBuilder.create(context);
        builder.addNextIntentWithParentStack(callIntent);

        PendingIntent callPendingIntent = builder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        String title = context.getString(R.string.local_notification_title), message = context.getString(R.string.local_notification_message);

        String titleName, messageName;

        if (UserType.isUserPatient()) {
            titleName = resultBean.getPatient().getDisplayName();
            messageName = resultBean.getDoctor().getDisplayName();
        } else {
            titleName = resultBean.getDoctor().getDisplayName();
            messageName = resultBean.getPatient().getDisplayName();
        }

        title = String.format(title, titleName);
        message = String.format(message, messageName, Utils.getPushNotificationTimeFormat(resultBean.getStart()));

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, notificationChannelId)
                .setSmallIcon(R.drawable.app_icon)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setContentTitle(title)
                .setContentText(message)
                .setLargeIcon(imageBitmap)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .addAction(R.drawable.ic_clear_black_24dp, context.getString(R.string.cancel_appointment), pendingIntent);

        if (!UserType.isUserPatient()) {
            notification.addAction(R.drawable.ic_call_black_24dp, context.getString(R.string.call), callPendingIntent);
        }

        notification.setContentIntent(contentPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(resultBean.getSchedule_id(), notification.build());

    }
}
