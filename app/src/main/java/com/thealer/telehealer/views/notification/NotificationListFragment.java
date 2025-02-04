package com.thealer.telehealer.views.notification;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationApiResponseModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationApiViewModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationRequestUpdateResponseModel;
import com.thealer.telehealer.apilayer.models.transaction.AskToAddCardViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnFilterSelectedInterface;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.SearchCellView;
import com.thealer.telehealer.views.common.SearchInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 07,January,2019
 */
public class NotificationListFragment extends BaseFragment implements OnFilterSelectedInterface {

    private CustomRecyclerView notificationCrv;

    private AttachObserverInterface attachObserverInterface;
    private NotificationApiViewModel notificationApiViewModel;
    private NotificationApiResponseModel notificationApiResponseModel;
    private ChangeTitleInterface changeTitleInterface;
    private AskToAddCardViewModel askToAddCardViewModel;

    private int page = 1;
    private NewNotificationListAdapter notificationListAdapter;
    private List<String> headerList = new ArrayList<>();
    private Map<String, List<NotificationApiResponseModel.ResultBean.RequestsBean>> childList = new HashMap<>();

    private String selectedFilterTypes = null;
    private SearchCellView searchView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        notificationApiViewModel = new ViewModelProvider(getActivity()).get(NotificationApiViewModel.class);
        attachObserverInterface.attachObserver(notificationApiViewModel);
        changeTitleInterface = (ChangeTitleInterface) context;

        notificationApiViewModel.baseApiResponseModelMutableLiveData.observe(this,
                new Observer<BaseApiResponseModel>() {
                    @Override
                    public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                        notificationCrv.hideProgressBar();
                        notificationCrv.getSwipeLayout().setRefreshing(false);
                        if (baseApiResponseModel != null) {
                            if (baseApiResponseModel instanceof NotificationApiResponseModel) {
                                notificationApiResponseModel = (NotificationApiResponseModel) baseApiResponseModel;
//                                updateNotificationList();

                                if (page == 1) {
                                    changeTitleInterface.onTitleChange(Utils.getPaginatedTitle(getString(R.string.notifications), notificationApiResponseModel.getCount()));
                                }
                                if (notificationApiResponseModel.getResult().getCount() > 0) {
                                    notificationListAdapter.setData(notificationApiResponseModel.getResult().getRequests(), page);
                                    notificationCrv.showOrhideEmptyState(false);
                                } else {
                                    notificationCrv.showOrhideEmptyState(true);
                                }

                                if (notificationApiResponseModel.getResult().getUnread_count() > 0) {
                                    updateNotificationStatus();
                                }

                                notificationCrv.setNextPage(notificationApiResponseModel.getNext());
                                notificationCrv.setScrollable(true);

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
                Utils.showAlertDialog(getActivity(),"Failed",errorModel.getMessage(),"Ok",null,null,null);
                notificationListAdapter.setbuttonON(true);
//                notificationListAdapter.notifyDataSetChanged();
            }
        });

        askToAddCardViewModel = new ViewModelProvider(this).get(AskToAddCardViewModel.class);
        attachObserverInterface.attachObserver(askToAddCardViewModel);
        askToAddCardViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(ErrorModel errorModel) {
                Utils.showAlertDialog(getContext(), getString(R.string.app_name),
                        errorModel.getMessage() != null && !errorModel.getMessage().isEmpty() ? errorModel.getMessage() : getString(R.string.failed_to_connect),
                        null, getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
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

//        notificationListAdapter.setData(headerList, childList, page);

        if (headerList.size() > 0) {
            notificationCrv.hideEmptyState();
        }
        notificationCrv.setNextPage(notificationApiResponseModel.getNext());

        notificationCrv.setScrollable(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        notificationCrv = (CustomRecyclerView) view.findViewById(R.id.notification_crv);
        searchView = view.findViewById(R.id.search_view);
        notificationCrv.setEmptyState(EmptyViewConstants.EMPTY_NOTIFICATIONS);
        notificationCrv.setScrollable(true);

        searchView.setSearchInterface(new SearchInterface() {
            @Override
            public void doSearch() {
                page = 1;
                getNotification(true);
            }
        });
        notificationCrv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                notificationCrv.setScrollable(false);
                page = page + 1;
                getNotification(false);
                notificationCrv.showProgressBar();
            }
        });

        searchView.setSearchHint(getString(R.string.search_notification));
        notificationCrv.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getNotification(false);
                notificationCrv.showProgressBar();
            }
        });

        notificationListAdapter = new NewNotificationListAdapter(getActivity(), askToAddCardViewModel);
        notificationCrv.getRecyclerView().setAdapter(notificationListAdapter);

        notificationCrv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNotification(true);
            }
        });

        notificationCrv.setErrorModel(this, notificationApiViewModel.getErrorModelLiveData());
    }

    private void getNotification(boolean isShowProgress) {
        String associationGuids = null;
        if (UserType.isUserAssistant()) {
            associationGuids = appPreference.getString(PreferenceConstants.ASSOCIATION_GUID_LIST);
            associationGuids = associationGuids.concat(",").concat(UserDetailPreferenceManager.getWhoAmIResponse().getUser_guid());
        }
        notificationApiViewModel.getNotifications(searchView.getCurrentSearchResult(), page, isShowProgress, associationGuids, selectedFilterTypes);
    }


    private void updateNotificationStatus() {
        notificationApiViewModel.updateNotificationStatus();
    }

    @Override
    public void onResume() {
        super.onResume();
        getNotification(true);
    }

    @Override
    public void onFilterSelected(@Nullable Bundle bundle) {
        if (bundle != null) {
            ArrayList<String> selectedFilters = (ArrayList<String>) bundle.getSerializable(ArgumentKeys.SELECTED_ITEMS);
            if (selectedFilters == null || selectedFilters.isEmpty()) {
                selectedFilterTypes = null;
            } else {
                selectedFilterTypes = "";
                for (int i = 0; i < selectedFilters.size(); i++) {
                    if (i > 0) {
                        selectedFilterTypes = selectedFilterTypes.concat(",");
                    }
                    selectedFilterTypes = selectedFilterTypes.concat(selectedFilters.get(i));
                }
            }
            page = 1;
            getNotification(true);
        }
    }
}
