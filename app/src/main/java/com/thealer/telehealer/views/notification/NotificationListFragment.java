package com.thealer.telehealer.views.notification;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationApiResponseModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationApiViewModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationRequestUpdateResponseModel;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 07,January,2019
 */
public class NotificationListFragment extends BaseFragment {

    private CustomExpandableListView notificationCelv;

    private AttachObserverInterface attachObserverInterface;
    private NotificationApiViewModel notificationApiViewModel;
    private NotificationApiResponseModel notificationApiResponseModel;

    private int page = 1;
    private NotificationListAdapter notificationListAdapter;
    private List<String> headerList = new ArrayList<>();
    private Map<String, List<NotificationApiResponseModel.ResultBean.RequestsBean>> childList = new HashMap<>();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        notificationApiViewModel = ViewModelProviders.of(getActivity()).get(NotificationApiViewModel.class);
        attachObserverInterface.attachObserver(notificationApiViewModel);

        notificationApiViewModel.baseApiResponseModelMutableLiveData.observe(this,
                new Observer<BaseApiResponseModel>() {
                    @Override
                    public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                        notificationCelv.hideProgressBar();
                        notificationCelv.getSwipeLayout().setRefreshing(false);
                        if (baseApiResponseModel != null) {
                            if (baseApiResponseModel instanceof NotificationApiResponseModel) {
                                notificationApiResponseModel = (NotificationApiResponseModel) baseApiResponseModel;
                                updateNotificationList();

                                if (notificationApiResponseModel.getResult().getUnread_count() > 0) {
                                    updateNotificationStatus();
                                }
                            } else if (baseApiResponseModel instanceof NotificationRequestUpdateResponseModel) {
                                if (baseApiResponseModel.isSuccess()) {
                                    page = 1;
                                    getNotification(true);
                                }
                            }
                        } else {
                            notificationListAdapter.notifyDataSetChanged();
                        }
                    }
                });
        notificationApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                notificationListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void updateNotificationList() {
        if (page == 1) {
            headerList.clear();
            childList.clear();
        }

        for (int i = 0; i < notificationApiResponseModel.getResult().getRequests().size(); i++) {

            String date = Utils.getDayMonthYear(notificationApiResponseModel.getResult().getRequests().get(i).getCreated_at());

            if (!headerList.contains(date)) {
                headerList.add(date);
            }

            List<NotificationApiResponseModel.ResultBean.RequestsBean> list = null;

            if (childList.containsKey(date)) {
                list = childList.get(date);
            }

            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(notificationApiResponseModel.getResult().getRequests().get(i));

            childList.put(date, list);
        }

        notificationListAdapter.setData(headerList, childList, page);

        if (headerList.size() > 0) {
            notificationCelv.hideEmptyState();
            expandList();
        }
        notificationCelv.setTotalCount(notificationApiResponseModel.getCount() + headerList.size());

        notificationCelv.setScrollable(true);
    }

    private void expandList() {
        for (int i = 0; i < headerList.size(); i++) {
            notificationCelv.getExpandableView().expandGroup(i);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        notificationCelv = (CustomExpandableListView) view.findViewById(R.id.notification_celv);
        notificationCelv.setEmptyState(EmptyViewConstants.EMPTY_NOTIFICATIONS);
        notificationCelv.setScrollable(true);

        notificationCelv.getExpandableView().setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

        notificationCelv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                notificationCelv.setScrollable(false);
                page = page + 1;
                getNotification(false);
                notificationCelv.showProgressBar();
            }
        });

        notificationCelv.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getNotification(false);
                notificationCelv.showProgressBar();
            }
        });

        notificationListAdapter = new NotificationListAdapter(getActivity());
        notificationCelv.getExpandableView().setAdapter(notificationListAdapter);

        notificationCelv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNotification(true);
            }
        });

        notificationCelv.setErrorModel(this, notificationApiViewModel.getErrorModelLiveData());
    }

    private void getNotification(boolean isShowProgress) {
        String associationGuids = null;
        if (UserType.isUserAssistant()) {
            associationGuids = appPreference.getString(PreferenceConstants.ASSOCIATION_GUID_LIST);
            associationGuids = associationGuids.concat(",").concat(UserDetailPreferenceManager.getWhoAmIResponse().getUser_guid());
        }
        notificationApiViewModel.getNotifications(page, isShowProgress, associationGuids);
    }


    private void updateNotificationStatus() {
        notificationApiViewModel.updateNotificationStatus();
    }

    @Override
    public void onResume() {
        super.onResume();
        getNotification(true);
    }
}
