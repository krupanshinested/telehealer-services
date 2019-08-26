package com.thealer.telehealer.views.home.vitals;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.vitals.StethBean;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AudioOnlyRenderersFactory;
import com.thealer.telehealer.views.common.LabelValueCustomView;

import java.util.HashMap;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 25,March,2019
 */
public class StethoscopeSegmentDetailFragment extends BaseFragment {
    private ImageView phonogramIv;
    private LabelValueCustomView dateLvcv;
    private LabelValueCustomView segmentLvcv;
    private LabelValueCustomView filterLvcv;
    private LabelValueCustomView durationLvcv;
    private LabelValueCustomView resultLvcv;

    private int minute = 60;
    private int hour = minute * 60;

    private SimpleExoPlayer simpleExoPlayer;
    private PlayerControlView playerControlView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stethoscope_segment_detail, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        phonogramIv = (ImageView) view.findViewById(R.id.phonogram_iv);
        dateLvcv = (LabelValueCustomView) view.findViewById(R.id.date_lvcv);
        segmentLvcv = (LabelValueCustomView) view.findViewById(R.id.segment_lvcv);
        filterLvcv = (LabelValueCustomView) view.findViewById(R.id.filter_lvcv);
        durationLvcv = (LabelValueCustomView) view.findViewById(R.id.duration_lvcv);
        resultLvcv = (LabelValueCustomView) view.findViewById(R.id.result_lvcv);
        playerControlView = (PlayerControlView) view.findViewById(R.id.player_control_view);

        if (getArguments() != null) {
            StethBean.SegmentsBean segmentsBean = (StethBean.SegmentsBean) getArguments().getSerializable(ArgumentKeys.SEGMENT_DETAIL);
            String date = getArguments().getString(ArgumentKeys.SELECTED_DATE);
            int segment = getArguments().getInt(ArgumentKeys.SEGMENT);

            Utils.setImageWithGlide(getActivity().getApplicationContext(), phonogramIv, segmentsBean.getPhonogram_file(), getActivity().getDrawable(R.drawable.placeholder_steth_io_graph), true, true);

            phonogramIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), LandscapeImageView.class).putExtra(ArgumentKeys.SHARED_IMAGE, segmentsBean.getPhonogram_file()));
                }
            });

            dateLvcv.setValueText(Utils.getFormatedDateTime(date));

            segmentLvcv.setValueText("S" + segment);

            filterLvcv.setValueText(segmentsBean.getFilter_type());

            int duration = (int) segmentsBean.getSegment_duration();
            String durationUnit = getString(R.string.seconds);
            durationLvcv.setValueText(duration + " " + durationUnit);

            if (duration > 0) {
                initializeAudioPlayer(segmentsBean.getAudio_file());
            }

            if (duration > hour) {
                int hr = duration / hour;
                int sec = duration - (hr * hour);
                int min = sec / minute;
                durationLvcv.setValueText(hr + " " + getString(R.string.hour) + " " + min + " " + getString(R.string.minute));
            } else if (duration > minute) {
                int min = duration / minute;
                int sec = duration - (min * minute);
                durationLvcv.setValueText(min + " " + getString(R.string.minute) + " " + sec + " " + durationUnit);
            }
            resultLvcv.setValueText(segmentsBean.getAi_response());
        }
    }

    private void initializeAudioPlayer(String audio_file) {
        String path = audio_file;
        /*if (audio_file.contains("http:") || audio_file.contains("https:")) {
            path = audio_file;
        } else {
            path = getString(R.string.api_base_url) + getString(R.string.get_image_url) + audio_file;
        }*/

        Uri uri = Uri.parse(path);

        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put(Constants.HEADER_AUTH_TOKEN, appPreference.getString(PreferenceConstants.USER_AUTH_TOKEN));
        headerMap.put("Content-Type", "application/x-www-form-urlencoded");
        headerMap.put("X-REQUEST-TYPE", "mobile");
        headerMap.put("X-DEVICE-TYPE", "android");


        TrackSelector trackSelector = new DefaultTrackSelector();

        HttpDataSource.Factory httpFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(getActivity(), getActivity().getPackageName()));

        httpFactory.getDefaultRequestProperties().set(headerMap);

        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), new AudioOnlyRenderersFactory(getActivity()), trackSelector);

        playerControlView.setPlayer(simpleExoPlayer);

        MediaSource mediaSource;
        mediaSource = new ExtractorMediaSource(uri, httpFactory, new DefaultExtractorsFactory(), null, null);

        simpleExoPlayer.prepare(mediaSource);

        simpleExoPlayer.addListener(new Player.EventListener() {

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.e("aswin", "onPlayerStateChanged: " + playWhenReady);
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                simpleExoPlayer.stop();
            }

            @Override
            public void onSeekProcessed() {
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setPlayWhenReady(true);
            }
        } else {
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setPlayWhenReady(false);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            setUserVisibleHint(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
        }
    }
}
