package com.thealer.telehealer.views.home.recents;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoFrameMetadataListener;
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

    private SimpleExoPlayer simpleExoPlayer;

    private RecentsApiViewModel recentsApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private OnCloseActionInterface onCloseActionInterface;
    private TranscriptionApiResponseModel transcriptionApiResponseModel;
    private DownloadTranscriptResponseModel downloadTranscriptResponseModel;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private int position = -1;
    private TranscriptionListAdapter transcriptionListAdapter;
    private ConstraintLayout parent;
    private NestedScrollView transcriptNsv;
    private TextView infoTv;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        recentsApiViewModel = new ViewModelProvider(this).get(RecentsApiViewModel.class);
        attachObserverInterface.attachObserver(recentsApiViewModel);

        recentsApiViewModel.baseApiResponseModelMutableLiveData.observe(this,
                new Observer<BaseApiResponseModel>() {
                    @Override
                    public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                        if (baseApiResponseModel != null) {
                            if (baseApiResponseModel instanceof TranscriptionApiResponseModel) {
                                transcriptionApiResponseModel = (TranscriptionApiResponseModel) baseApiResponseModel;

                                if (transcriptionApiResponseModel.getStatus() != null && transcriptionApiResponseModel.getStatus().equals(transcriptionApiResponseModel.STATUS_READY)) {
                                    String username = null;
                                    if (UserType.isUserPatient()) {
                                        username = transcriptionApiResponseModel.getDoctor().getDisplayName();
                                    } else {
                                        username = transcriptionApiResponseModel.getPatient().getDisplayName();
                                    }
                                    toolbarTitle.setTextColor(Color.WHITE);
                                    toolbarTitle.setText(username + "\n" + Utils.getDayMonthYear(transcriptionApiResponseModel.getOrder_start_time()));

                                    if (transcriptionApiResponseModel.getDoctor() != null) {
                                        if (transcriptionApiResponseModel.getDoctor().getUser_id() == transcriptionApiResponseModel.getCaller_id()) {
                                            transcriptionApiResponseModel.setCaller(transcriptionApiResponseModel.getDoctor());
                                        } else if (transcriptionApiResponseModel.getDoctor().getUser_id() == transcriptionApiResponseModel.getCallee_id()) {
                                            transcriptionApiResponseModel.setCallee(transcriptionApiResponseModel.getDoctor());
                                        }
                                    }
                                    if (transcriptionApiResponseModel.getPatient() != null) {
                                        if (transcriptionApiResponseModel.getPatient().getUser_id() == transcriptionApiResponseModel.getCaller_id()) {
                                            transcriptionApiResponseModel.setCaller(transcriptionApiResponseModel.getPatient());
                                        } else if (transcriptionApiResponseModel.getPatient().getUser_id() == transcriptionApiResponseModel.getCallee_id()) {
                                            transcriptionApiResponseModel.setCallee(transcriptionApiResponseModel.getPatient());
                                        }
                                    }
                                    if (transcriptionApiResponseModel.getMedical_assistant() != null) {
                                        if (transcriptionApiResponseModel.getMedical_assistant().getUser_id() == transcriptionApiResponseModel.getCaller_id()) {
                                            transcriptionApiResponseModel.setCaller(transcriptionApiResponseModel.getMedical_assistant());
                                        } else if (transcriptionApiResponseModel.getMedical_assistant().getUser_id() == transcriptionApiResponseModel.getCallee_id()) {
                                            transcriptionApiResponseModel.setCallee(transcriptionApiResponseModel.getMedical_assistant());
                                        }
                                    }

                                    if (!TextUtils.isEmpty(transcriptionApiResponseModel.getTranscript())) {
                                        recentsApiViewModel.downloadTranscriptDetail(transcriptionApiResponseModel.getTranscript(), true);
                                    }

                                    createVideoPlayer();
                                } else {
                                    Utils.showAlertDialog(getActivity(),
                                            getString(R.string.alert),
                                            getString(R.string.transcription_not_ready),
                                            getString(R.string.ok),
                                            null,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    onCloseActionInterface.onClose(false);
                                                }
                                            },
                                            null);
                                }

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
        parent = (ConstraintLayout) view.findViewById(R.id.parent);
        transcriptNsv = (NestedScrollView) view.findViewById(R.id.transcript_nsv);
        infoTv = (TextView) view.findViewById(R.id.info_tv);

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
    }

    private void updateTranscript() {

        if (downloadTranscriptResponseModel != null && downloadTranscriptResponseModel.getSpeakerLabels().size() > 0) {
            enableOrDisableView(printIv, true);
            enableOrDisableView(featuresIv, true);
            transcriptionListAdapter = new TranscriptionListAdapter(getActivity(), downloadTranscriptResponseModel, transcriptionApiResponseModel);
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
        String path = transcriptionApiResponseModel.getAudio_stream();

        /*if (transcriptionApiResponseModel.getAudio_stream().contains("http:") || transcriptionApiResponseModel.getAudio_stream().contains("https:")) {
            path = transcriptionApiResponseModel.getAudio_stream();
        } else {
            path = getString(R.string.api_base_url) + getString(R.string.get_image_url) + transcriptionApiResponseModel.getAudio_stream() + "&decrypt=false";
        }*/


        Uri uri = Uri.parse(path);

        TrackSelector trackSelector = new DefaultTrackSelector();
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);

        playerView.setPlayer(simpleExoPlayer);

        playerView.setShowShuffleButton(false);
        DefaultHttpDataSourceFactory dataSource = new DefaultHttpDataSourceFactory(
                Util.getUserAgent(getActivity(), getString(R.string.app_name)));
        ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(dataSource)
                .createMediaSource(uri);

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
                if (transcriptNsv.getVisibility() == View.VISIBLE) {
                    infoTv.setVisibility(View.INVISIBLE);
                    transcriptNsv.setVisibility(View.GONE);
                    printIv.setVisibility(View.GONE);
                    featuresIv.setVisibility(View.GONE);
                } else {
                    transcriptNsv.setVisibility(View.VISIBLE);
                    infoTv.setVisibility(View.VISIBLE);
                    printIv.setVisibility(View.VISIBLE);
                    featuresIv.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.print_iv:
                if (transcriptionApiResponseModel != null && downloadTranscriptResponseModel != null) {
                    String transcriptPdf = new TranscriptionPdfGenerator(getActivity()).getTranscriptPdf(transcriptionApiResponseModel, downloadTranscriptResponseModel, getString(R.string.fine_transcript_message));

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
    public void onPause() {
        super.onPause();
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
        }
    }
}
