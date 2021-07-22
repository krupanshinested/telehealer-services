package com.thealer.telehealer.views.EducationalVideo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.EducationalVideo.DeleteEducationalVideoResponse;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideo;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideoOrder;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideoViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

import java.util.ArrayList;

public class EducationalVideoDetailFragment extends BaseFragment {

    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv, delete_iv, full_view;
    private TextView toolbarTitle, edit_tv;
    private PlayerView playerView;
    private TextView description_tv;

    private EducationalVideo educationalVideo;
    @Nullable
    private EducationalVideoOrder educationalVideoOrder;
    @Nullable
    private SimpleExoPlayer simpleExoPlayer;

    private EducationalVideoViewModel educationalVideoViewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        educationalVideoViewModel = new ViewModelProvider(this).get(EducationalVideoViewModel.class);
        addObserver();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_educational_detail, container, false);

        if (getActivity() instanceof AttachObserverInterface) {
            ((AttachObserverInterface) getActivity()).attachObserver(educationalVideoViewModel);
        }

        initView(view);

        if (getArguments() != null) {
            Object videoObject = getArguments().getSerializable(ArgumentKeys.EDUCATIONAL_VIDEO);

            if (videoObject == null) {
                String doctorGuid = getArguments().getString(ArgumentKeys.DOCTOR_GUID);
                String id = getArguments().getString(ArgumentKeys.EDUCATIONAL_VIDEO_ID);
                String userGuid = getArguments().getString(ArgumentKeys.USER_GUID);
                educationalVideoViewModel.getEducationalVideos(doctorGuid, id, userGuid);
            } else if (videoObject instanceof EducationalVideo) {
                educationalVideo = (EducationalVideo) videoObject;
                updateUI();
            } else if (videoObject instanceof EducationalVideoOrder) {
                educationalVideoOrder = (EducationalVideoOrder) videoObject;
                educationalVideo = educationalVideoOrder.getVideo();
                updateUI();
            }
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
        }
    }

    private void initView(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        edit_tv = view.findViewById(R.id.next_tv);
        description_tv = view.findViewById(R.id.description_tv);
        playerView = view.findViewById(R.id.player_view);
        delete_iv = view.findViewById(R.id.close_iv);
        full_view = view.findViewById(R.id.full_view);

        edit_tv.setText(getString(R.string.edit));
        delete_iv.setImageResource(R.drawable.bt_ic_delete_forever);

        edit_tv.setVisibility(View.GONE);
        delete_iv.setVisibility(View.GONE);

        initListener();
    }

    private void updateUI() {
        if (educationalVideo != null) {
            toolbarTitle.setText(educationalVideo.getTitle());
            description_tv.setText(educationalVideo.getDescription());
        }

        if (educationalVideo != null && !TextUtils.isEmpty(educationalVideo.getUrl())) {
            if (educationalVideoOrder != null && UserType.isUserPatient()) {
                educationalVideoViewModel.patchEducationalVideo(educationalVideoOrder.getUser_video_id() + "");
            }
            setUpPlayer(educationalVideo.getUrl());
        } else {
            Utils.showAlertDialog(getActivity(), getString(R.string.pending), getString(R.string.educational_video_created), getString(R.string.ok), null, null, null);
        }

        if (educationalVideoOrder != null && !UserType.isUserPatient()) {
            edit_tv.setVisibility(View.GONE);
            delete_iv.setVisibility(View.VISIBLE);
        } else if (educationalVideo != null && educationalVideo.getCreated_by() == UserDetailPreferenceManager.getWhoAmIResponse().getUser_id()) {
            edit_tv.setVisibility(View.VISIBLE);
            delete_iv.setVisibility(View.VISIBLE);
        } else {
            edit_tv.setVisibility(View.GONE);
            delete_iv.setVisibility(View.GONE);
        }

        if (educationalVideo != null && !TextUtils.isEmpty(educationalVideo.getUrl())) {
            full_view.setVisibility(View.VISIBLE);
        } else {
            full_view.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        edit_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ShowSubFragmentInterface) {
                    EducationalCreateFragment fragment = new EducationalCreateFragment();
                    fragment.setArguments(getArguments());
                    ((ShowSubFragmentInterface) getActivity()).onShowFragment(fragment);
                }
            }
        });

        delete_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (educationalVideoOrder != null && !UserType.isUserPatient()) {
                    Utils.showAlertDialog(getActivity(), getString(R.string.remove), getString(R.string.unassociate_educational_video), getString(R.string.remove), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ArrayList<Integer> ids = new ArrayList<>();
                            ids.add(educationalVideoOrder.getVideo().getVideo_id());

                            String doctor_guid = null;
                            if (UserType.isUserAssistant()) {
                                doctor_guid = educationalVideoOrder.getDoctor().getUser_guid();
                            }

                            educationalVideoViewModel.unAssociateEducationalVideoOrder(doctor_guid, educationalVideoOrder.getPatient().getUser_guid(), ids);
                        }
                    }, null);
                } else {
                    Utils.showAlertDialog(getActivity(), getString(R.string.delete), getString(R.string.delete_educational_video), getString(R.string.delete), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String currentUserGuid = UserDetailPreferenceManager.getUser_guid();
                            if (!UserType.isUserAssistant())
                                currentUserGuid = "";
                            educationalVideoViewModel.deleteEducationalVideo(currentUserGuid, educationalVideo.getVideo_id());
                        }
                    }, null);
                }
            }
        });

        full_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(educationalVideo.getUrl())) {
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(educationalVideo.getUrl()));
                intent.setDataAndType(Uri.parse(educationalVideo.getUrl()), "video/mp4");
                startActivity(intent);
            }
        });
    }

    private void addObserver() {
        educationalVideoViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {

                if (baseApiResponseModel instanceof DeleteEducationalVideoResponse) {
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                }

            }
        });

        educationalVideoViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(ArrayList<BaseApiResponseModel> baseApiResponseModel) {
                if (baseApiResponseModel.size() > 0) {
                    if (baseApiResponseModel.get(0) instanceof EducationalVideoOrder) {
                        EducationalVideoOrder order = (EducationalVideoOrder) baseApiResponseModel.get(0);
                        educationalVideoOrder = order;
                        educationalVideo = educationalVideoOrder.getVideo();

                        updateUI();
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                }
            }
        });

        educationalVideoViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(ErrorModel errorModel) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });
    }

    private void setUpPlayer(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }

        Uri uri = Uri.parse(path);
        TrackSelector trackSelector = new DefaultTrackSelector();
        SimpleExoPlayer simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);
        playerView.setPlayer(simpleExoPlayer);
        playerView.setShowShuffleButton(false);
        DefaultHttpDataSourceFactory dataSource = new DefaultHttpDataSourceFactory(
                Util.getUserAgent(getActivity(), getString(R.string.organization_name)));
        ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(dataSource)
                .createMediaSource(uri);
        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.setPlayWhenReady(true);
        this.simpleExoPlayer = simpleExoPlayer;
    }
}
