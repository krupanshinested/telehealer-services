package com.thealer.telehealer.views.home.recents;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomExpandableListView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
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
    private CustomExpandableListView recentsCelv;
    private ExpandableListView recentsElv;
    private RecentsApiViewModel recentsApiViewModel;
    private int page = 1;
    private RecentsApiResponseModel recentsApiResponseModel;
    private List<String> listHeader = new ArrayList<>();
    private HashMap<String, List<RecentsApiResponseModel.ResultBean>> listChild = new HashMap<>();
    private List<String> chatListHeader = new ArrayList<>();
    private List<String> videoListHeader = new ArrayList<>();
    private HashMap<String, List<RecentsApiResponseModel.ResultBean>> chatList = new HashMap<>();
    private HashMap<String, List<RecentsApiResponseModel.ResultBean>> videoList = new HashMap<>();
    private RecentListAdapter recentListAdapter;
    private OnOrientationChangeInterface onOrientationChangeInterface;
    private AttachObserverInterface attachObserverInterface;
    private boolean isApiRequested = false, isResumed;
    private int totalCount = 0;
    private ImageView throbberIv;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(TAG, "onAttach: " + (recentsApiViewModel == null));
        attachObserverInterface = (AttachObserverInterface) getActivity();

        onOrientationChangeInterface = (OnOrientationChangeInterface) getActivity();

        recentsApiViewModel = ViewModelProviders.of(this).get(RecentsApiViewModel.class);

        if (recentsApiViewModel.baseApiResponseModelMutableLiveData.hasActiveObservers()) {
            Log.e(TAG, "onAttach: has observer");
        } else {
            Log.e(TAG, "onAttach: no observer");
        }

        attachObserverInterface.attachObserver(recentsApiViewModel);

        recentsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                recentsCelv.getSwipeLayout().setRefreshing(false);
                throbberIv.setVisibility(View.GONE);

                if (baseApiResponseModel != null) {

                    recentsApiResponseModel = (RecentsApiResponseModel) baseApiResponseModel;

                    totalCount = recentsApiResponseModel.getCount();

                    Log.e(TAG, "onChanged: ");
                    updateList();

                    if (!recentsApiResponseModel.getResult().isEmpty()) {

                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.USER_DETAIL, recentsApiResponseModel.getResult().get(0));
                        onOrientationChangeInterface.onDataReceived(bundle);
                        recentsCelv.hideEmptyState();
                    }
                } else {
                    Log.e(TAG, "onChanged: null");
                }
                isApiRequested = false;
                recentsCelv.setScrollable(true);
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
        recentsCelv = (CustomExpandableListView) view.findViewById(R.id.recents_celv);
        throbberIv = (ImageView) view.findViewById(R.id.throbber_iv);

        Glide.with(getActivity().getApplicationContext()).load(R.raw.throbber).into(throbberIv);

        recentsCelv.setEmptyState(EmptyViewConstants.EMPTY_CALLS);

        recentsCelv.hideEmptyState();

        recentsElv = recentsCelv.getExpandableView();
        recentsCelv.hideEmptyState();

        boolean isShowInfoAction = false;
        if (getArguments() != null) {
            isShowInfoAction = getArguments().getBoolean(Constants.IS_FROM_HOME);
        }
        if (getUserVisibleHint()) {
            makeApiCall(true);
        }
        recentListAdapter = new RecentListAdapter(getActivity(), listHeader, listChild, isShowInfoAction);

        recentsElv.setAdapter(recentListAdapter);

        recentsCelv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                throbberIv.setVisibility(View.VISIBLE);
                ++page;
                makeApiCall(false);
                isApiRequested = true;
                recentsCelv.setScrollable(false);
            }
        });

        recentsElv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

        recentsCelv.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                makeApiCall(false);
            }
        });

        recentsCelv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeApiCall(true);
            }
        });

        recentsCelv.setErrorModel(this, recentsApiViewModel.getErrorModelLiveData());
    }

    private void updateList() {
        if (page == 1) {
            listHeader.clear();
            listChild.clear();
        }
        Log.e(TAG, "updateList: 1");
        if (recentsApiResponseModel != null && recentsApiResponseModel.getResult().size() > 0) {
            Log.e(TAG, "updateList: 2");

            chatList.clear();
            chatListHeader.clear();
            videoList.clear();
            videoListHeader.clear();

            for (int i = 0; i < recentsApiResponseModel.getResult().size(); i++) {

                if (!recentsApiResponseModel.getResult().get(i).getUpdated_at().isEmpty()) {
                    if (recentsApiResponseModel.getResult().get(i).getCorr_type().equals(Constants.CALL)) {
                        addTocall(recentsApiResponseModel.getResult().get(i), i);

                    } else if (recentsApiResponseModel.getResult().get(i).getCorr_type().equals(Constants.CHAT)) {
                        addToChat(recentsApiResponseModel.getResult().get(i), i);
                    }
                }
            }

            if (chatList.size() > 0) {
                listChild.putAll(chatList);
                listHeader.add(chatListHeader.get(0));
            }

            if (videoList.size() > 0) {

                for (int i = 0; i < videoListHeader.size(); i++) {
                    List<RecentsApiResponseModel.ResultBean> listHashMap = new ArrayList<>();

                    if (!listHeader.contains(videoListHeader.get(i))) {
                        listHeader.add(videoListHeader.get(i));
                        listHashMap = videoList.get(videoListHeader.get(i));
                    } else {
                        listHashMap = listChild.get(videoListHeader.get(i));
                        if (listHashMap == null) {
                            listHashMap = new ArrayList<>();
                        }

                        listHashMap.addAll(videoList.get(videoListHeader.get(i)));
                    }
                    listChild.put(videoListHeader.get(i), listHashMap);
                }
            }

            recentsCelv.setTotalCount(totalCount + listHeader.size());

            if (recentListAdapter != null) {
                Log.e(TAG, "updateList: 3");
                recentListAdapter.setData(listHeader, listChild, page);
                expandListView();
            }

            Log.e(TAG, "updateList: " + new Gson().toJson(listChild));
            if (listHeader.isEmpty()) {
                recentsCelv.showEmptyState();
            } else {
                recentsCelv.hideEmptyState();
            }
        } else {
            Log.e(TAG, "updateList: 4");
            recentsCelv.showEmptyState();
        }

        Log.e(TAG, "updateList: 5");

    }

    private void expandListView() {
        for (int i = 0; i < listHeader.size(); i++) {
            recentsElv.expandGroup(i);
        }
    }

    private void addToChat(RecentsApiResponseModel.ResultBean resultBean, int i) {
        String date = Utils.getDayMonthYear(resultBean.getUpdated_at()).concat("-c");
        List<RecentsApiResponseModel.ResultBean> beanList = new ArrayList<>();

        if (!chatListHeader.contains(date)) {
            chatListHeader.add(date);
        }

        if (!chatList.isEmpty()) {
            beanList = chatList.get(date);
        }


        if (beanList == null) {
            beanList = new ArrayList<>();
        }

        beanList.add(resultBean);
        chatList.put(date, beanList);
    }

    private void addTocall(RecentsApiResponseModel.ResultBean resultBean, int i) {
        String date = Utils.getDayMonthYear(resultBean.getUpdated_at());
        List<RecentsApiResponseModel.ResultBean> beanList = new ArrayList<>();

        if (!videoListHeader.contains(date)) {
            videoListHeader.add(date);
        }

        if (!videoList.isEmpty()) {
            beanList = videoList.get(date);
        }

        if (beanList == null) {
            beanList = new ArrayList<>();
        }

        beanList.add(resultBean);
        videoList.put(date, beanList);
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
                        boolean isCalls = false;

                        CommonUserApiResponseModel userDetail = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
                        String userGuid = null;
                        if (userDetail != null) {
                            userGuid = userDetail.getUser_guid();
                        }
                        CommonUserApiResponseModel doctorDetail = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);
                        String doctorGuid = null;
                        if (doctorDetail != null) {
                            doctorGuid = doctorDetail.getUser_guid();
                            isCalls = true;
                        }
                        recentsApiViewModel.getUserCorrespondentList(userGuid, doctorGuid, page, isCalls, isShowProgress);
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;
        Log.e(TAG, "onResume: ");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e(TAG, "setUserVisibleHint: " + isVisibleToUser + " " + isResumed);

        isApiRequested = false;

        if (isVisibleToUser && isResumed)
            makeApiCall(true);
    }

}
