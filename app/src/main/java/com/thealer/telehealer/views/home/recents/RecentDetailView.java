package com.thealer.telehealer.views.home.recents;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoFrameMetadataListener;
import com.google.android.exoplayer2.video.spherical.CameraMotionListener;
import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.recents.DownloadTranscriptResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiViewModel;
import com.thealer.telehealer.apilayer.models.recents.TranscriptionApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.PdfViewerFragment;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 16,November,2018
 */
public class RecentDetailView extends BaseFragment implements View.OnClickListener {

    private PlayerView playerView;
    private CustomRecyclerView transcriptRv;
    private ConstraintLayout transcriptControllerCl;
    private ImageView featuresIv;
    private ImageView printIv;
    private TextView transcriptTv;
    private ImageView backIv;
    private TextView toolbarTitle;
    private TextView timerTv;

    private SimpleExoPlayer simpleExoPlayer;

    private RecentsApiViewModel recentsApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private OnCloseActionInterface onCloseActionInterface;
    private TranscriptionApiResponseModel transcriptionApiResponseModel;
    private DownloadTranscriptResponseModel downloadTranscriptResponseModel;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private int position = -1;
    private TranscriptionListAdapter transcriptionListAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        recentsApiViewModel = ViewModelProviders.of(this).get(RecentsApiViewModel.class);
        attachObserverInterface.attachObserver(recentsApiViewModel);

        recentsApiViewModel.baseApiResponseModelMutableLiveData.observe(this,
                new Observer<BaseApiResponseModel>() {
                    @Override
                    public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                        if (baseApiResponseModel != null) {
                            if (baseApiResponseModel instanceof TranscriptionApiResponseModel) {
                                transcriptionApiResponseModel = (TranscriptionApiResponseModel) baseApiResponseModel;

                                String username = null;
                                if (UserType.isUserPatient()) {
                                    username = "Dr. " + transcriptionApiResponseModel.getDoctor().getFirst_name() + " " + transcriptionApiResponseModel.getDoctor().getLast_name();
                                } else {
                                    username = transcriptionApiResponseModel.getPatient().getFirst_name() + " " + transcriptionApiResponseModel.getPatient().getLast_name();
                                }
                                toolbarTitle.setTextColor(Color.WHITE);
                                toolbarTitle.setText(username + "\n" + Utils.getDayMonthYear(transcriptionApiResponseModel.getOrder_start_time()));

                                recentsApiViewModel.downloadTranscriptDetail(transcriptionApiResponseModel.getTranscript(), true);
                                createVideoPlayer();

                            } else if (baseApiResponseModel instanceof DownloadTranscriptResponseModel) {
                                downloadTranscriptResponseModel = (DownloadTranscriptResponseModel) baseApiResponseModel;
                                updateTranscript();
                            }
                        }
                    }
                });

        recentsApiViewModel.getErrorModelLiveData().observe(this,
                new Observer<ErrorModel>() {
                    @Override
                    public void onChanged(@Nullable ErrorModel errorModel) {
                        if (errorModel != null) {

                        }
                    }
                });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_detail_view, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        playerView = (PlayerView) view.findViewById(R.id.player_view);
        transcriptRv = (CustomRecyclerView) view.findViewById(R.id.transcript_rv);
        transcriptControllerCl = (ConstraintLayout) view.findViewById(R.id.transcript_controller_cl);
        featuresIv = (ImageView) view.findViewById(R.id.features_iv);
        printIv = (ImageView) view.findViewById(R.id.print_iv);
        transcriptTv = (TextView) view.findViewById(R.id.transcript_tv);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);

        backIv.setOnClickListener(this);
        transcriptTv.setOnClickListener(this);
        printIv.setOnClickListener(this);
        featuresIv.setOnClickListener(this);

        transcriptRv.setEmptyState(EmptyViewConstants.EMPTY_TRANSCRIPTS);

        if (getArguments() != null) {
            RecentsApiResponseModel.ResultBean resultBean = (RecentsApiResponseModel.ResultBean) getArguments().getSerializable(ArgumentKeys.SELECTED_RECENT_DETAIL);
            if (resultBean != null) {
                recentsApiViewModel.getTranscriptionDetail(resultBean.getTranscription_id(), true);
            }
        }
        timerTv = (TextView) view.findViewById(R.id.timer_tv);
    }

    private void updateTranscript() {

        if (downloadTranscriptResponseModel != null && downloadTranscriptResponseModel.getSpeakerLabels().size() > 0) {
            enableOrDisableView(printIv, true);
            enableOrDisableView(featuresIv, true);
            transcriptionListAdapter = new TranscriptionListAdapter(getActivity(), downloadTranscriptResponseModel);
            transcriptRv.getRecyclerView().setAdapter(transcriptionListAdapter);
        } else {
            enableOrDisableView(printIv, false);
            enableOrDisableView(featuresIv, false);
        }
    }

    private void enableOrDisableView(ImageView imageView, boolean enable) {
        imageView.setClickable(enable);

        if (enable) {
            imageView.setAlpha(1f);
        } else {
            imageView.setAlpha(0.5f);
        }
    }

    private void createVideoPlayer() {
        String path = getString(R.string.api_base_url) + getString(R.string.get_image_url) + transcriptionApiResponseModel.getAudio_stream() + "&decrypt=false";

        Uri uri = Uri.parse(path);

        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put(Constants.HEADER_AUTH_TOKEN, appPreference.getString(PreferenceConstants.USER_AUTH_TOKEN));
        headerMap.put("Content-Type", "application/x-www-form-urlencoded");
        headerMap.put("X-REQUEST-TYPE", "mobile");
        headerMap.put("X-DEVICE-TYPE", "android");


        TrackSelector trackSelector = new DefaultTrackSelector();

        HttpDataSource.Factory httpFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(getActivity(), getActivity().getPackageName()));

        httpFactory.getDefaultRequestProperties().set(headerMap);

        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);

        playerView.setPlayer(simpleExoPlayer);

        playerView.setShowShuffleButton(false);

        MediaSource mediaSource = new HlsMediaSource.Factory(httpFactory).createMediaSource(uri);

        LoopingMediaSource loopingMediaSource = new LoopingMediaSource(mediaSource);

        simpleExoPlayer.prepare(mediaSource);

        simpleExoPlayer.setVideoFrameMetadataListener(new VideoFrameMetadataListener() {
            @Override
            public void onVideoFrameAboutToBeRendered(long presentationTimeUs, long releaseTimeNs, Format format) {

                if (downloadTranscriptResponseModel != null) {
                    for (int i = 0; i < downloadTranscriptResponseModel.getSpeakerLabels().size(); i++) {
                        float currentSecond = TimeUnit.MICROSECONDS.toSeconds(presentationTimeUs);

                        if (currentSecond > Float.parseFloat(downloadTranscriptResponseModel.getSpeakerLabels().get(i).getStart_time()) &&
                                currentSecond < Float.parseFloat(downloadTranscriptResponseModel.getSpeakerLabels().get(i).getEnd_time())) {
                            if (position < i) {
                                position = i;

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        transcriptionListAdapter.setCurrentTranscription(position);
                                        transcriptRv.getRecyclerView().scrollToPosition(position);
                                    }
                                });
                            }
                            break;
                        }
                    }
                }
            }
        });

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
                position = -1;
            }
        });

        simpleExoPlayer.setPlayWhenReady(true);


    }

    @Override
    public void onResume() {
        super.onResume();
        backIv.setImageTintList(ColorStateList.valueOf(getActivity().getColor(R.color.colorWhite)));
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
            case R.id.transcript_tv:
                if (transcriptRv.getVisibility() == View.VISIBLE) {
                    transcriptRv.setVisibility(View.GONE);
                    printIv.setVisibility(View.GONE);
                    featuresIv.setVisibility(View.GONE);
                } else {
                    transcriptRv.setVisibility(View.VISIBLE);
                    printIv.setVisibility(View.VISIBLE);
                    featuresIv.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.print_iv:
                if (transcriptionApiResponseModel != null && downloadTranscriptResponseModel != null) {
                    String transcriptPdf = new TranscriptionPdfGenerator().getTranscriptPdf(transcriptionApiResponseModel, downloadTranscriptResponseModel);

                    PdfViewerFragment pdfViewerFragment = new PdfViewerFragment();
                    bundle.putString(ArgumentKeys.HTML_FILE, transcriptPdf);
                    bundle.putString(ArgumentKeys.PDF_TITLE, getString(R.string.transcript_file));
                    pdfViewerFragment.setArguments(bundle);
                    showSubFragmentInterface.onShowFragment(pdfViewerFragment);
                }
                break;
            case R.id.features_iv:
                if (transcriptionApiResponseModel != null && downloadTranscriptResponseModel != null) {

                    bundle = new Bundle();
                    bundle.putString(ArgumentKeys.ORDER_ID, transcriptionApiResponseModel.getOrder_id());
                    bundle.putSerializable(ArgumentKeys.TRANSCRIPTION_DETAIL, downloadTranscriptResponseModel);

                    ExperimentalFeatureFragment experimentalFeatureFragment = new ExperimentalFeatureFragment();
                    experimentalFeatureFragment.setArguments(bundle);
                    showSubFragmentInterface.onShowFragment(experimentalFeatureFragment);

                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        simpleExoPlayer.release();
    }
}
