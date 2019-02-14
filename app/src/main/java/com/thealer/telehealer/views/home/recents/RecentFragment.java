package com.thealer.telehealer.views.home.recents;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomExpandableListView;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnOrientationChangeInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aswin on 14,November,2018
 */
public class RecentFragment extends BaseFragment {

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
    private boolean isApiRequested = false;
    private int totalCount = 0;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
            makeApiCall(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        attachObserverInterface = (AttachObserverInterface) getActivity();

        onOrientationChangeInterface = (OnOrientationChangeInterface) getActivity();

        recentsApiViewModel = ViewModelProviders.of(this).get(RecentsApiViewModel.class);

        attachObserverInterface.attachObserver(recentsApiViewModel);

        recentsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {

                    recentsApiResponseModel = (RecentsApiResponseModel) baseApiResponseModel;

                    totalCount = recentsApiResponseModel.getCount();

                    updateList();

                    if (!recentsApiResponseModel.getResult().isEmpty()) {

                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.USER_DETAIL, recentsApiResponseModel.getResult().get(0));
                        onOrientationChangeInterface.onDataReceived(bundle);
                        recentsCelv.hideEmptyState();
                    }
                }
                isApiRequested = false;
                recentsCelv.hideProgressBar();
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

        recentsCelv.setEmptyState(EmptyViewConstants.EMPTY_CALLS);

        recentsElv = recentsCelv.getExpandableView();

        boolean isShowInfoAction = false;
        if (getArguments() != null &&
                getArguments().getBoolean(Constants.IS_FROM_HOME)) {
            makeApiCall(true);
            isShowInfoAction = true;
        }
        recentListAdapter = new RecentListAdapter(getActivity(), listHeader, listChild, isShowInfoAction);

        recentsElv.setAdapter(recentListAdapter);

        recentsCelv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
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

    }

    private void updateList() {
        if (page == 1) {
            listHeader.clear();
            listChild.clear();
        }
        if (recentsApiResponseModel != null && recentsApiResponseModel.getResult().size() > 0) {


            chatList.clear();
            chatListHeader.clear();
            videoList.clear();
            videoListHeader.clear();

            for (int i = 0; i < recentsApiResponseModel.getResult().size(); i++) {

                if (!recentsApiResponseModel.getResult().get(i).getUpdated_at().isEmpty()) {
                    if (recentsApiResponseModel.getResult().get(i).getCorr_type().equals("call")) {
                        addTocall(recentsApiResponseModel.getResult().get(i), i);
                    } else if (recentsApiResponseModel.getResult().get(i).getCorr_type().equals("chat")) {
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
                recentListAdapter.setData(listHeader, listChild, page);
                expandListView();
            }
        }
    }

    private void expandListView() {
        for (int i = 0; i < listHeader.size(); i++) {
            recentsElv.expandGroup(i);
        }
    }

    private void addToChat(RecentsApiResponseModel.ResultBean resultBean, int i) {
        String date = Utils.getDayMonthYear(resultBean.getUpdated_at()).concat("-c");
        List<RecentsApiResponseModel.ResultBean> beanList = new ArrayList<>();

        if (!chatList.containsKey(date)) {

            beanList.add(resultBean);

            chatList.put(date, beanList);
            chatListHeader.add(date);
        } else {

            beanList.addAll(chatList.get(date));
            beanList.add(resultBean);

            chatList.put(chatListHeader.get(i), beanList);

        }

    }

    private void addTocall(RecentsApiResponseModel.ResultBean resultBean, int i) {
        String date = Utils.getDayMonthYear(resultBean.getUpdated_at());
        List<RecentsApiResponseModel.ResultBean> beanList = new ArrayList<>();

        if (!videoListHeader.contains(date)) {
            videoListHeader.add(date);
            beanList.add(resultBean);
        } else {
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
                        recentsApiViewModel.getMyCorrespondentList(page);
                    } else {
                        CommonUserApiResponseModel userDetail = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
                        if (userDetail != null) {
                            recentsApiViewModel.getUserCorrespondentList(userDetail.getUser_guid(), page, false, isShowProgress);
                        }
                    }
                }
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
}
