package com.thealer.telehealer.views.home.schedules;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.pubNub.PubNubNotificationPayload;
import com.thealer.telehealer.common.pubNub.PubNubResult;
import com.thealer.telehealer.common.pubNub.PubnubUtil;
import com.thealer.telehealer.common.pubNub.models.PushPayLoad;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.home.chat.ChatActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 18,June,2019
 */
public class WaitingRoomActivity extends BaseActivity {

    public static boolean isActive = false;

    private SchedulesApiResponseModel.ResultBean schedule;
    private ImageView closeIv;
    private ShimmerTextView title_tv;
    private Shimmer shimmer = new Shimmer();
    private PlayerView playerView;
    private SimpleExoPlayer simpleExoPlayer;
    private ShimmerTextView titleTv;
    private CustomButton viewMsgBtn;
    private LinearLayout messageLl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);
        initView();
        schedule = (SchedulesApiResponseModel.ResultBean) getIntent().getSerializableExtra(ArgumentKeys.SCHEDULE_DETAIL);

        ArrayList<String> preSentSchedules = getListOfSchedulesAlreadySent();
        if (!preSentSchedules.contains(schedule.getSchedule_id() + "")) {
            addScheduleIdToList(schedule.getSchedule_id() + "");

            PushPayLoad payLoad = PubNubNotificationPayload.getWaitingRoomPayload(schedule.getDoctor().getUser_guid(), schedule.getSchedule_id() + "");
            PubnubUtil.shared.publishPushMessage(payLoad, new PubNubResult() {
                @Override
                public void didSend(Boolean isSuccess) {
                    //do nothing
                }
            });
        }

        shimmer.setDuration(2000)
                .setStartDelay(0);

        createVideoPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(WaitingRoomActivity.this).registerReceiver(callStartReceiver, new IntentFilter(Constants.CALL_STARTED_BROADCAST));
        LocalBroadcastManager.getInstance(WaitingRoomActivity.this).registerReceiver(messageReceiver, new IntentFilter(Constants.message_broadcast));

        WaitingRoomActivity.isActive = true;

        shimmer.start(title_tv);
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(WaitingRoomActivity.this).unregisterReceiver(callStartReceiver);
        LocalBroadcastManager.getInstance(WaitingRoomActivity.this).unregisterReceiver(messageReceiver);

        WaitingRoomActivity.isActive = false;
        shimmer.cancel();

        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
        }
    }

    public void addScheduleIdToList(String scheduleId) {
        Gson gson = new Gson();
        ArrayList<String> schedules = getListOfSchedulesAlreadySent();
        schedules.add(scheduleId);
        String scheduleString = gson.toJson(schedules);
        appPreference.setString(PreferenceConstants.SCHEDULE_WAITING_ROOM, scheduleString);
    }

    @NonNull
    public ArrayList<String> getListOfSchedulesAlreadySent() {
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> schedules = new Gson().fromJson(appPreference.getString(PreferenceConstants.SCHEDULE_WAITING_ROOM), listType);
        if (schedules == null) {
            return new ArrayList<>();
        } else {
            return schedules;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initView() {
        playerView = findViewById(R.id.player_view);
        closeIv = (ImageView) findViewById(R.id.close_iv);
        title_tv = findViewById(R.id.title_tv);

        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        titleTv = (ShimmerTextView) findViewById(R.id.title_tv);
        viewMsgBtn = (CustomButton) findViewById(R.id.view_msg_btn);
        messageLl = (LinearLayout) findViewById(R.id.message_ll);

        viewMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageLl.setVisibility(View.GONE);
                title_tv.setVisibility(View.VISIBLE);

                Bundle bundle = new Bundle();
                bundle.putString(ArgumentKeys.USER_GUID, schedule.getDoctor().getUser_guid());
                startActivity(new Intent(WaitingRoomActivity.this, ChatActivity.class).putExtras(bundle));
            }
        });
    }

    private BroadcastReceiver callStartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getExtras() != null) {

                Bundle bundle = intent.getExtras();
                String userguid = bundle.getString(ArgumentKeys.USER_GUID);

                if (userguid != null && userguid.equals(schedule.getDoctor().getUser_guid())) {
                    messageLl.setVisibility(View.VISIBLE);
                    title_tv.setVisibility(View.GONE);
                }
            }
        }
    };

    private void createVideoPlayer() {
        String path;
        double random = Math.random();
        Log.v("WaitingRoom", "random " + random);
        if (((int) (random * 1000)) % 2 == 0) {
            Log.v("WaitingRoom", "playing 1");
            path = "https://s3.amazonaws.com/telehealer.com/Beach.mp4";
        } else {
            Log.v("WaitingRoom", "playing 2");
            path = "https://s3.amazonaws.com/telehealer.com/Creek.mp4";
        }

        Uri uri = Uri.parse(path);
        TrackSelector trackSelector = new DefaultTrackSelector();
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(WaitingRoomActivity.this, trackSelector);
        playerView.setPlayer(simpleExoPlayer);
        playerView.setShowShuffleButton(false);
        DefaultHttpDataSourceFactory dataSource = new DefaultHttpDataSourceFactory(
                Util.getUserAgent(this, getString(R.string.app_name)));
        ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(dataSource)
                .createMediaSource(uri);
        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.addListener(new Player.EventListener() {

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                simpleExoPlayer.stop();
            }

            @Override
            public void onSeekProcessed() {
            }
        });

        /*CustomTextureView customTextureView = new CustomTextureView(WaitingRoomActivity.this);
        Matrix matrix = new Matrix();
        matrix.preRotate(90);
        customTextureView.setTransform(matrix);
        simpleExoPlayer.setVideoTextureView(customTextureView);*/
        simpleExoPlayer.setPlayWhenReady(true);
        simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
        playerView.setUseController(false);
    }
}
