package com.thealer.telehealer.views.home.pendingInvites;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationApiResponseModel;
import com.thealer.telehealer.apilayer.models.pendingInvites.PendingInvitesApiViewModel;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;

/**
 * Created by Aswin on 04,March,2019
 */
public class PendingInvitesReceivedFragment extends BaseFragment {
    private CustomRecyclerView pendingInvitesCrv;
    private int page = 1, totalCount;
    private boolean isApiRequested = false, isResume = false;

    private PendingInvitesApiViewModel pendingInvitesApiViewModel;
    public NotificationApiResponseModel invitesResponseModel;
    private AttachObserverInterface attachObserverInterface;
    public PendingInvitesListAdapter pendingInvitesListAdapter;


    private void attachObservers() {
        attachObserverInterface = (AttachObserverInterface) getActivity();
        pendingInvitesApiViewModel = ViewModelProviders.of(PendingInvitesReceivedFragment.this).get(PendingInvitesApiViewModel.class);
        attachObserverInterface.attachObserver(pendingInvitesApiViewModel);

        pendingInvitesApiViewModel.baseApiResponseModelMutableLiveData.observe(PendingInvitesReceivedFragment.this,
                new Observer<BaseApiResponseModel>() {
                    @Override
                    public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                        if (baseApiResponseModel != null) {
                            if (baseApiResponseModel instanceof NotificationApiResponseModel) {
                                invitesResponseModel = (NotificationApiResponseModel) baseApiResponseModel;

                                if (invitesResponseModel.getCount() == 0) {
                                    pendingInvitesCrv.showOrhideEmptyState(true);
                                } else {
                                    totalCount = invitesResponseModel.getCount();
                                    pendingInvitesCrv.setTotalCount(totalCount);
                                    pendingInvitesCrv.showOrhideEmptyState(false);
                                    pendingInvitesListAdapter.setData(invitesResponseModel.getResult(), page);
                                }
                                isApiRequested = false;
                                pendingInvitesCrv.getSwipeLayout().setRefreshing(false);
                                pendingInvitesCrv.setScrollable(true);
                            }
                        }
                    }
                });

        pendingInvitesCrv.setErrorModel(this, pendingInvitesApiViewModel.getErrorModelLiveData());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_invite, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        pendingInvitesCrv = (CustomRecyclerView) view.findViewById(R.id.pending_invites_crv);
        pendingInvitesCrv.setEmptyState(EmptyViewConstants.EMPTY_RECEIVED_PENDING_INVITES);
        pendingInvitesCrv.hideEmptyState();
        pendingInvitesListAdapter = new PendingInvitesListAdapter(getActivity(), true);
        pendingInvitesCrv.getRecyclerView().setAdapter(pendingInvitesListAdapter);

        pendingInvitesCrv.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getReceivedPendingInvites(page, false);
            }
        });

        pendingInvitesCrv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                pendingInvitesCrv.setScrollable(false);
                page = page + 1;
                getReceivedPendingInvites(page, false);
                pendingInvitesCrv.showProgressBar();
            }
        });

        pendingInvitesCrv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReceivedPendingInvites(page, true);
            }
        });
    }

    private void getReceivedPendingInvites(int page, boolean isShowProgress) {
        if (!isApiRequested) {
            isApiRequested = true;
            pendingInvitesApiViewModel.getPendingInvites(page, false, true, isShowProgress);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResume) {
            attachObservers();
            getReceivedPendingInvites(page, true);
        } else {
            if (pendingInvitesApiViewModel != null) {
                pendingInvitesApiViewModel.baseApiResponseModelMutableLiveData.removeObservers(this);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isResume = true;
        setUserVisibleHint(true);
    }
}
