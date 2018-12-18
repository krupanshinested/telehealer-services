package com.thealer.telehealer.views.home.schedules;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiViewModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesListAdapter;
import com.thealer.telehealer.common.CustomExpandableListView;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aswin on 18,December,2018
 */
public class SchedulesListFragment extends BaseFragment {

    private CustomExpandableListView schedulesElv;
    private FloatingActionButton addFab;

    private SchedulesApiViewModel schedulesApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private SchedulesApiResponseModel schedulesApiResponseModel;
    private AssociationApiViewModel associationApiViewModel;

    private int page = 1;
    private List<String> headerList = new ArrayList<>();
    private HashMap<String, List<SchedulesApiResponseModel.ResultBean>> childList = new HashMap<>();
    private boolean isApiRequested = false;
    private SchedulesListAdapter schedulesListAdapter;
    private StringBuilder doctorGuidList = new StringBuilder();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        associationApiViewModel = ViewModelProviders.of(this).get(AssociationApiViewModel.class);
        schedulesApiViewModel = ViewModelProviders.of(this).get(SchedulesApiViewModel.class);
        attachObserverInterface.attachObserver(schedulesApiViewModel);
        attachObserverInterface.attachObserver(associationApiViewModel);

        schedulesApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    isApiRequested = false;
                    schedulesElv.hideProgressBar();
                    schedulesApiResponseModel = (SchedulesApiResponseModel) baseApiResponseModel;
                    schedulesElv.setTotalCount(schedulesApiResponseModel.getCount());
                    generateList();
                }
            }
        });

        associationApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {
                    ArrayList<CommonUserApiResponseModel> commonUserApiResponseModels = (ArrayList<CommonUserApiResponseModel>) (Object) baseApiResponseModels;

                    for (int i = 0; i < commonUserApiResponseModels.size(); i++) {
                        if (doctorGuidList.length() == 0) {
                            doctorGuidList = doctorGuidList.append(commonUserApiResponseModels.get(i).getUser_guid());
                        } else {
                            doctorGuidList = doctorGuidList.append(",").append(commonUserApiResponseModels.get(i).getUser_guid());
                        }
                    }
                    getSchedulesList(page);
                }
            }
        });
    }

    private void generateList() {
        List<SchedulesApiResponseModel.ResultBean> resultBeanList = schedulesApiResponseModel.getResult();
        headerList.clear();
        childList.clear();
        if (resultBeanList != null && resultBeanList.size() > 0) {
            for (int i = 0; i < resultBeanList.size(); i++) {
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

            if (headerList.size() > 0) {
                schedulesListAdapter.setData(headerList, childList, page);
                schedulesElv.hideEmptyState();
                expandListView();
            } else {
                schedulesElv.showEmptyState();
            }
        }
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
                getSchedulesList(page);
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
    }

    private void getSchedulesList(int page) {
        if (!isApiRequested) {
            isApiRequested = true;
            schedulesApiViewModel.getSchedule(page, true, doctorGuidList.toString());
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
        Log.e("aswin", "onResume: ");
        if (UserType.isUserAssistant()) {
            getDoctorsList();
        } else {
            getSchedulesList(page);
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e("aswin", "setUserVisibleHint: " + getUserVisibleHint());
    }

    private void getDoctorsList() {
        associationApiViewModel.getAssociationList(true, null);
    }
}
