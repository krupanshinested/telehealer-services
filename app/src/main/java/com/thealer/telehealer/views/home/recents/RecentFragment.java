package com.thealer.telehealer.views.home.recents;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnOrientationChangeInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 14,November,2018
 */
public class RecentFragment extends BaseFragment {

    private static final String TAG = "aswin recents ";
    private RecentsApiViewModel recentsApiViewModel;
    private int page = 1;
    private RecentsApiResponseModel recentsApiResponseModel;
    private List<String> listHeader = new ArrayList<>();
    private HashMap<String, List<RecentsApiResponseModel.ResultBean>> listChild = new HashMap<>();
    private List<String> chatListHeader = new ArrayList<>();
    private List<String> videoListHeader = new ArrayList<>();
    private HashMap<String, List<RecentsApiResponseModel.ResultBean>> chatList = new HashMap<>();
    private HashMap<String, List<RecentsApiResponseModel.ResultBean>> videoList = new HashMap<>();
    private NewRecentListAdapter recentListAdapter;
    private OnOrientationChangeInterface onOrientationChangeInterface;
    private AttachObserverInterface attachObserverInterface;
    private boolean isApiRequested = false, isResumed;
    private ImageView throbberIv;
    private CustomRecyclerView recentsCrv;
    private boolean isCalls = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();

        onOrientationChangeInterface = (OnOrientationChangeInterface) getActivity();

        recentsApiViewModel = ViewModelProviders.of(this).get(RecentsApiViewModel.class);

        attachObserverInterface.attachObserver(recentsApiViewModel);

        recentsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                recentsCrv.getSwipeLayout().setRefreshing(false);
                throbberIv.setVisibility(View.GONE);

                if (baseApiResponseModel != null) {

                    recentsApiResponseModel = (RecentsApiResponseModel) baseApiResponseModel;

                    recentsCrv.setNextPage(recentsApiResponseModel.getNext());

//                    updateList();

                    if (!recentsApiResponseModel.getResult().isEmpty()) {

                        recentListAdapter.setData(recentsApiResponseModel.getResult(), page, isCalls);

                        Log.e(TAG, "onChanged: " + recentListAdapter.getItemCount());

                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.USER_DETAIL, recentsApiResponseModel.getResult().get(0));
                        onOrientationChangeInterface.onDataReceived(bundle);
                        recentsCrv.showOrhideEmptyState(false);
                    } else {
                        recentsCrv.showOrhideEmptyState(true);
                    }
                }
                isApiRequested = false;
                recentsCrv.setScrollable(true);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        recentsCrv = (CustomRecyclerView) view.findViewById(R.id.recents_crv);
        throbberIv = (ImageView) view.findViewById(R.id.throbber_iv);

        Glide.with(getActivity().getApplicationContext()).load(R.raw.throbber).into(throbberIv);

        recentsCrv.setEmptyState(EmptyViewConstants.EMPTY_CALLS);

        recentsCrv.hideEmptyState();

        if (getUserVisibleHint()) {
            makeApiCall(true);
        }
        recentListAdapter = new NewRecentListAdapter(getActivity());
        recentsCrv.getRecyclerView().setAdapter(recentListAdapter);

        recentsCrv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                Log.e(TAG, "onPaginate: ");
                throbberIv.setVisibility(View.VISIBLE);
                ++page;
                makeApiCall(false);
                isApiRequested = true;
                recentsCrv.setScrollable(false);
            }
        });

        recentsCrv.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                makeApiCall(false);
            }
        });

        recentsCrv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeApiCall(true);
            }
        });

        recentsCrv.setErrorModel(this, recentsApiViewModel.getErrorModelLiveData());
    }

    private void makeApiCall(boolean isShowProgress) {
        if (!isApiRequested) {
            if (recentsApiViewModel != null) {
                if (getArguments() != null) {
                    if (getArguments().getBoolean(Constants.IS_FROM_HOME)) {
                        isApiRequested = true;
                        String doctorGuid = null;
                        if (UserType.isUserAssistant()) {
                            doctorGuid = appPreference.getString(PreferenceConstants.ASSOCIATION_GUID_LIST);
                            doctorGuid = doctorGuid.concat("," + UserDetailPreferenceManager.getWhoAmIResponse().getUser_guid());
                        }
                        recentsApiViewModel.getMyCorrespondentList(page, doctorGuid, isShowProgress);
                    } else {

                        CommonUserApiResponseModel userDetail = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
                        String userGuid = null;
                        if (userDetail != null) {
                            userGuid = userDetail.getUser_guid();
//                            recentsApiViewModel.getUserCorrespondentList(userDetail.getUser_guid(), null, null, page, false, isShowProgress);
                        }
                        CommonUserApiResponseModel doctorDetail = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);
                        String doctorGuid = null;
                        if (doctorDetail != null) {
                            doctorGuid = doctorDetail.getUser_guid();
                            isCalls = true;

                            if (!UserType.isUserAssistant()) {
                                userGuid = doctorGuid;
                            }
                        }
                        Log.e(TAG, "makeApiCall: " + new Gson().toJson(userDetail));
                        Log.e(TAG, "makeApiCall: " + new Gson().toJson(doctorDetail));
                        recentsApiViewModel.getUserCorrespondentList(userGuid, doctorGuid, null, page, isCalls, isShowProgress);
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        isApiRequested = false;

        if (isVisibleToUser && isResumed)
            makeApiCall(true);
    }

}
