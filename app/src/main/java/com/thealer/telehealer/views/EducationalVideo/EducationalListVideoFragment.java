package com.thealer.telehealer.views.EducationalVideo;

import android.content.Context;
import android.os.Bundle;
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

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideo;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideoResponse;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideoViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.EducationalVideo.Adapter.EducationListAdapter;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OverlayViewConstants;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.quickLogin.QuickLoginPinFragment;
import com.thealer.telehealer.views.settings.ProfileSettingsActivity;

import java.util.ArrayList;

import me.toptas.fancyshowcase.listener.DismissListener;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

public class EducationalListVideoFragment extends BaseFragment {

    private CustomRecyclerView recyclerView;
    private FloatingActionButton add;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;

    private EducationalVideoViewModel educationalVideoViewModel;
    private int page = 1;
    @Nullable
    private EducationListAdapter educationListAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        educationalVideoViewModel = new ViewModelProvider(this).get(EducationalVideoViewModel.class);
        addObservers();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_educational_video, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.custom_rv);
        add = view.findViewById(R.id.add_fab);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        view.findViewById(R.id.next_tv).setVisibility(View.GONE);
        toolbarTitle.setText(getString(R.string.educational_video));
        initListeners();

        if (getActivity() instanceof AttachObserverInterface) {
            ((AttachObserverInterface)getActivity()).attachObserver(educationalVideoViewModel);
        }

        assignRecyclerViewObservers();
    }

    private void assignRecyclerViewObservers() {
        recyclerView.setEmptyState(EmptyViewConstants.EMPTY_EDUCATIONAL_VIDEOS);
        recyclerView.showOrhideEmptyState(false);
        recyclerView.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                page = page + 1;
                recyclerView.setScrollable(false);
                recyclerView.showProgressBar();
                getEducationalVideos();
            }
        });
        recyclerView.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEducationalVideos();
            }
        });
        recyclerView.setErrorModel(this, educationalVideoViewModel.getErrorModelLiveData());
        recyclerView.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getEducationalVideos();
            }
        });

        educationListAdapter = new EducationListAdapter(getActivity(), new ArrayList<>(), new EducationListAdapter.EducationalListSelector() {
            @Override
            public void didSelectEducationalVideo(EducationalVideo video) {
                if (getActivity() instanceof ShowSubFragmentInterface) {
                    EducationalVideoDetailFragment fragment = new EducationalVideoDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ArgumentKeys.EDUCATIONAL_VIDEO,video);
                    fragment.setArguments(bundle);
                    ((ShowSubFragmentInterface) getActivity()).onShowFragment(fragment);
                }
            }
        });
        recyclerView.getRecyclerView().setAdapter(educationListAdapter);

        getEducationalVideos();
    }

    private void addObservers() {
        educationalVideoViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel instanceof EducationalVideoResponse) {
                    EducationalVideoResponse response = (EducationalVideoResponse) baseApiResponseModel;

                    if (educationListAdapter == null) {
                        return;
                    }

                    recyclerView.getSwipeLayout().setRefreshing(false);

                    if (page == 1) {
                        educationListAdapter.reset();
                    }

                    if (response.getResult().size() == 0) {
                        recyclerView.showOrhideEmptyState(true);

                        if (!appPreference.getBoolean(PreferenceConstants.IS_OVERLAY_EDUCATIONAL_VIDEO)) {

                            appPreference.setBoolean(PreferenceConstants.IS_OVERLAY_EDUCATIONAL_VIDEO, true);

                            DismissListener dismissListener = new DismissListener() {
                                @Override
                                public void onDismiss(@org.jetbrains.annotations.Nullable String s) {

                                }

                                @Override
                                public void onSkipped(@org.jetbrains.annotations.Nullable String s) {

                                }
                            };

                            Utils.showOverlay(getActivity(), add, OverlayViewConstants.OVERLAY_NO_EDUCATIONAL_VIDEO, dismissListener);
                        }
                    }

                    recyclerView.setNextPage(response.getNext());

                    if (response.getResult().size() > 0) {
                        recyclerView.showOrhideEmptyState(false);
                    }

                    educationListAdapter.appedItems(response.getResult());

                    recyclerView.setScrollable(true);
                }
            }
        });
    }

    private void getEducationalVideos() {
        educationalVideoViewModel.getEducationalVideo(null,page);
    }

    private void initListeners() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ShowSubFragmentInterface) {
                    EducationalCreateFragment fragment = new EducationalCreateFragment();
                    ((ShowSubFragmentInterface) getActivity()).onShowFragment(fragment);
                }
            }
        });

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }


}
