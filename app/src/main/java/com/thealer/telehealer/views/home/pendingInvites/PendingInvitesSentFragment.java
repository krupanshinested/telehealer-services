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
import com.thealer.telehealer.apilayer.models.pendingInvites.PendingInvitesNonRegisterdApiResponseModel;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;

/**
 * Created by Aswin on 04,March,2019
 */
public class PendingInvitesSentFragment extends BaseFragment {
    private CustomRecyclerView pendingInvitesCrv;
    private PendingInvitesNonRegisterdApiResponseModel nonRegisterdApiResponseModel;
    private int page = 1, nonRegisteredPage = 1;
    private boolean isApiRequested = false, isNonRegisteredInvite = false, isResume = false;

    private PendingInvitesApiViewModel pendingInvitesApiViewModel;
    public NotificationApiResponseModel invitesResponseModel;
    private AttachObserverInterface attachObserverInterface;
    public PendingInvitesListAdapter pendingInvitesListAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_invite, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        pendingInvitesCrv = (CustomRecyclerView) view.findViewById(R.id.pending_invites_crv);
        pendingInvitesCrv.setEmptyState(EmptyViewConstants.EMPTY_SENT_PENDING_INVITES);
        pendingInvitesCrv.hideEmptyState();
        pendingInvitesListAdapter = new PendingInvitesListAdapter(getActivity(), false);
        pendingInvitesCrv.getRecyclerView().setAdapter(pendingInvitesListAdapter);

        pendingInvitesCrv.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                nonRegisteredPage = 1;
                isNonRegisteredInvite = false;
                getReceivedPendingInvites(page, false);
            }
        });

        pendingInvitesCrv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                pendingInvitesCrv.setScrollable(false);
                pendingInvitesCrv.showProgressBar();

                if (!isNonRegisteredInvite) {
                    page = page + 1;
                    getReceivedPendingInvites(page, false);
                } else {
                    nonRegisteredPage = nonRegisteredPage + 1;
                    getNonRegisteredInvites(nonRegisteredPage, false);
                }
            }
        });

        pendingInvitesCrv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeApiCall();
            }
        });

    }

    private void getReceivedPendingInvites(int page, boolean isShowProgress) {
        if (!isApiRequested) {
            isApiRequested = true;
            pendingInvitesApiViewModel.getPendingInvites(page, true, false, isShowProgress);
        }
    }

    private void getNonRegisteredInvites(int page, boolean isShowProgress) {
        if (!isApiRequested) {
            isApiRequested = true;
            pendingInvitesApiViewModel.getNonRegisteredUserInvites(page, isShowProgress);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResume) {
            makeApiCall();
        }
    }

    private void makeApiCall() {
        if (pendingInvitesApiViewModel == null) {
            attachObserver();
            getReceivedPendingInvites(page, true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isResume = true;
        if (getUserVisibleHint()) {
            setUserVisibleHint(true);
        }
    }

    private void attachObserver() {
        attachObserverInterface = (AttachObserverInterface) getActivity();
        pendingInvitesApiViewModel = ViewModelProviders.of(this).get(PendingInvitesApiViewModel.class);
        attachObserverInterface.attachObserver(pendingInvitesApiViewModel);

        pendingInvitesApiViewModel.baseApiResponseModelMutableLiveData.observe(this,
                new Observer<BaseApiResponseModel>() {
                    @Override
                    public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                        if (baseApiResponseModel != null) {
                            if (baseApiResponseModel instanceof NotificationApiResponseModel) {
                                invitesResponseModel = (NotificationApiResponseModel) baseApiResponseModel;

                                if (invitesResponseModel.getCount() > 0) {
                                    if (page == 1) {
                                        pendingInvitesCrv.setNextPage(invitesResponseModel.getNext());
                                    }

                                    pendingInvitesListAdapter.setData(invitesResponseModel.getResult(), page);
                                } else {
                                    if (!UserType.isUserDoctor()) {
                                        pendingInvitesCrv.showOrhideEmptyState(true);
                                    }
                                }

                                isApiRequested = false;
                                pendingInvitesCrv.getSwipeLayout().setRefreshing(false);
                                pendingInvitesCrv.setScrollable(true);
                                pendingInvitesCrv.hideProgressBar();

                                if ((pendingInvitesListAdapter.getItemCount() >= invitesResponseModel.getCount()) && UserType.isUserDoctor()) {
                                    isNonRegisteredInvite = true;
                                    getNonRegisteredInvites(nonRegisteredPage, true);
                                }

                            } else if (baseApiResponseModel instanceof PendingInvitesNonRegisterdApiResponseModel) {
                                nonRegisterdApiResponseModel = (PendingInvitesNonRegisterdApiResponseModel) baseApiResponseModel;

                                if (nonRegisterdApiResponseModel.getCount() > 0) {
                                    if (nonRegisteredPage == 1) {
                                        pendingInvitesCrv.setNextPage(nonRegisterdApiResponseModel.getNext());
                                    }
                                    pendingInvitesListAdapter.setData(nonRegisterdApiResponseModel.getResult(), nonRegisteredPage);
                                } else {
                                    if (invitesResponseModel.getCount() == 0)
                                        pendingInvitesCrv.showOrhideEmptyState(true);
                                }

                                isApiRequested = false;
                                pendingInvitesCrv.getSwipeLayout().setRefreshing(false);
                                pendingInvitesCrv.setScrollable(true);
                                pendingInvitesCrv.hideProgressBar();
                            }
                        }
                    }
                });

        pendingInvitesCrv.setErrorModel(this, pendingInvitesApiViewModel.getErrorModelLiveData());

    }

}
