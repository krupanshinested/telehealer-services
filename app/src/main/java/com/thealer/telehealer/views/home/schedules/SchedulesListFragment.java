package com.thealer.telehealer.views.home.schedules;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiViewModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesListAdapter;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomExpandableListView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 18,December,2018
 */
public class SchedulesListFragment extends BaseFragment {

    private static final String TAG = "aswin schedules ";
    private CustomExpandableListView schedulesElv;
    private FloatingActionButton addFab;

    private SchedulesApiViewModel schedulesApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private SchedulesApiResponseModel schedulesApiResponseModel;

    private int page = 1;
    private List<String> headerList = new ArrayList<>();
    private HashMap<String, List<SchedulesApiResponseModel.ResultBean>> childList = new HashMap<>();
    private boolean isApiRequested = false;
    private SchedulesListAdapter schedulesListAdapter;
    private String doctorGuidList;
    private boolean isResumed = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        schedulesApiViewModel = ViewModelProviders.of(this).get(SchedulesApiViewModel.class);
        attachObserverInterface.attachObserver(schedulesApiViewModel);

        schedulesApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                schedulesElv.getSwipeLayout().setRefreshing(false);
                if (baseApiResponseModel != null) {
                    schedulesApiResponseModel = (SchedulesApiResponseModel) baseApiResponseModel;
                    if (page == 1){
                        if (context instanceof ChangeTitleInterface)
                            ((ChangeTitleInterface) context).onTitleChange(Utils.getPaginatedTitle(getString(R.string.schedules), schedulesApiResponseModel.getCount()));
                    }
                    generateList();
                }
                isApiRequested = false;
                schedulesElv.hideProgressBar();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedules_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        schedulesElv = (CustomExpandableListView) view.findViewById(R.id.schedules_elv);
        addFab = (FloatingActionButton) view.findViewById(R.id.add_fab);

        schedulesElv.setEmptyState(EmptyViewConstants.EMPTY_APPOINTMENTS_WITH_BTN);

        schedulesElv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                page = page + 1;
                getSchedulesList(page, true);
                schedulesElv.showProgressBar();
            }
        });

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), CreateNewScheduleActivity.class));
            }
        });

        schedulesListAdapter = new SchedulesListAdapter(getActivity(), headerList, childList);
        schedulesElv.getExpandableView().setAdapter(schedulesListAdapter);

        schedulesElv.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getSchedulesList(page, false);
            }
        });

        schedulesElv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSchedulesList(page, true);
            }
        });

        schedulesElv.setErrorModel(this, schedulesApiViewModel.getErrorModelLiveData());

        schedulesElv.hideEmptyState();

        if (getArguments() != null) {
            CommonUserApiResponseModel doctorDetail = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);
            if (getArguments().getBoolean(ArgumentKeys.HIDE_ADD)) {
                addFab.hide();
            }

            if (doctorDetail != null) {
                doctorGuidList = doctorDetail.getUser_guid();
            }
        } else {
            doctorGuidList = appPreference.getString(PreferenceConstants.ASSOCIATION_GUID_LIST);
        }

        if (getUserVisibleHint()) {
            makeApiCall();
        }
    }

    private void generateList() {
        Log.e(TAG, "onChanged: 4");
        List<SchedulesApiResponseModel.ResultBean> resultBeanList = schedulesApiResponseModel.getResult();
        if (page == 1) {
            headerList.clear();
            childList.clear();
        }
        if (resultBeanList != null && resultBeanList.size() > 0) {
            Log.e(TAG, "onChanged: 5");
            for (int i = 0; i < resultBeanList.size(); i++) {
                Log.e(TAG, "onChanged: 6");

                String date = Utils.getDayMonthYear(resultBeanList.get(i).getStart());

                List<SchedulesApiResponseModel.ResultBean> resultBeans = new ArrayList<>();

                if (!headerList.contains(date)) {
                    headerList.add(date);
                }

                if (childList.containsKey(date)) {
                    resultBeans.addAll(childList.get(date));
                }
                resultBeans.add(resultBeanList.get(i));

                childList.put(date, resultBeans);
            }

            schedulesElv.setTotalCount(schedulesApiResponseModel.getCount() + headerList.size());

            if (headerList.size() > 0) {
                Log.e(TAG, "onChanged: 8");
                schedulesListAdapter.setData(headerList, childList, page);
                schedulesElv.hideEmptyState();
                expandListView();
            } else {
                Log.e(TAG, "onChanged: 9");
                schedulesElv.showEmptyState();
            }
        } else {
            schedulesElv.showOrhideEmptyState(true);
        }

    }

    private void getSchedulesList(int page, boolean isShowProgress) {
        if (!isApiRequested) {
            schedulesElv.hideEmptyState();
            isApiRequested = true;
            schedulesApiViewModel.getSchedule(page, isShowProgress, doctorGuidList.toString());
        }
    }

    private void expandListView() {
        for (int i = 0; i < headerList.size(); i++) {
            schedulesElv.getExpandableView().expandGroup(i);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;
//        Log.e(TAG, "onResume: ");

    }

    private void makeApiCall() {
        getSchedulesList(page, true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        Log.e(TAG, "setUserVisibleHint: " + getUserVisibleHint() + " " + isResumed);
        isApiRequested = false;

        if (isVisibleToUser && isResumed) {
            makeApiCall();
        }
    }
}
