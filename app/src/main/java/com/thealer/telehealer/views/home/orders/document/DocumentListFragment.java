package com.thealer.telehealer.views.home.orders.document;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.documents.DocumentsApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OverlayViewConstants;
import com.thealer.telehealer.views.home.orders.CreateOrderActivity;
import com.thealer.telehealer.views.home.orders.OrderConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.toptas.fancyshowcase.listener.DismissListener;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 29,November,2018
 */
public class DocumentListFragment extends BaseFragment implements View.OnClickListener {
    private ImageView backIv;
    private TextView toolbarTitle;
    private FloatingActionButton addFab;

    private OnCloseActionInterface onCloseActionInterface;
    private OrdersApiViewModel ordersApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private DocumentsApiResponseModel documentsApiResponseModel;

    private List<String> headerList = new ArrayList<>();
    private HashMap<String, List<DocumentsApiResponseModel.ResultBean>> childList = new HashMap<>();
    private CommonUserApiResponseModel commonUserApiResponseModel;

    private int page = 1;
    private boolean isFromHome = true;
    private boolean isApiRequested = false;
    private AppBarLayout appbarLayout;
    private boolean onResume = false;
    private CustomRecyclerView documentsCrv;
    private DocumentListAdapter documentListAdapter;
    private Toolbar toolbar;
    private GridLayoutManager gridLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();

        ordersApiViewModel = ViewModelProviders.of(this).get(OrdersApiViewModel.class);
        attachObserverInterface.attachObserver(ordersApiViewModel);

        ordersApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                documentsCrv.getSwipeLayout().setRefreshing(false);
                if (baseApiResponseModel != null) {
                    documentsApiResponseModel = (DocumentsApiResponseModel) baseApiResponseModel;
                    if (UserType.isUserPatient() && page == 1 && documentsApiResponseModel.getResult().size() == 0) {
                        if (!appPreference.getBoolean(PreferenceConstants.IS_OVERLAY_ADD_DOCUMENT)) {
                            appPreference.setBoolean(PreferenceConstants.IS_OVERLAY_ADD_DOCUMENT, true);

                            documentsCrv.showOrHideMessage(false);

                            Utils.showOverlay(getActivity(), addFab, OverlayViewConstants.OVERLAY_NO_DOCUMENT, new DismissListener() {
                                @Override
                                public void onDismiss(@org.jetbrains.annotations.Nullable String s) {
                                    documentsCrv.showOrHideMessage(true);
                                }

                                @Override
                                public void onSkipped(@org.jetbrains.annotations.Nullable String s) {

                                }
                            });
                        }
                    }
                    setData();
                }
            }
        });
    }

    private void setData() {

        documentsCrv.setTotalCount(documentsApiResponseModel.getCount());

        if (documentsApiResponseModel.getCount() > 0) {
            documentListAdapter.setData(documentsApiResponseModel.getResult(), page);
            documentsCrv.hideEmptyState();
        } else {
            documentsCrv.showEmptyState();
        }

        isApiRequested = false;
        documentsCrv.setScrollable(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_document_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        addFab = (FloatingActionButton) view.findViewById(R.id.add_fab);
        documentsCrv = (CustomRecyclerView) view.findViewById(R.id.documents_crv);

        toolbar.inflateMenu(R.menu.menu_documents);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_list:
                        toolbar.getMenu().findItem(R.id.menu_list).setVisible(false);
                        toolbar.getMenu().findItem(R.id.menu_grid).setVisible(true);
                        setGridSpan(1);
                        break;
                    case R.id.menu_grid:
                        setGridSpan(2);
                        toolbar.getMenu().findItem(R.id.menu_list).setVisible(true);
                        toolbar.getMenu().findItem(R.id.menu_grid).setVisible(false);
                        break;
                }
                return false;
            }
        });

        backIv.setOnClickListener(this);
        addFab.setOnClickListener(this);

        toolbarTitle.setText(OrderConstant.ORDER_DOCUMENTS);

        if (getArguments() != null) {
            isFromHome = getArguments().getBoolean(Constants.IS_FROM_HOME);
            if (getArguments().getBoolean(ArgumentKeys.IS_HIDE_TOOLBAR, false)) {
                appbarLayout.setVisibility(View.GONE);
            }
            commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            if (!isFromHome) {
                addFab.hide();
                documentsCrv.setEmptyState(EmptyViewConstants.EMPTY_DOCUMENTS);
            } else {
                documentsCrv.setEmptyState(EmptyViewConstants.EMPTY_DOCUMENTS_WITH_BTN);
            }
        }

        documentsCrv.hideEmptyState();

        documentsCrv.setScrollable(true);

        documentsCrv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                if (!isApiRequested) {
                    documentsCrv.setScrollable(false);
                    page = page + 1;
                    getDocuments(false);
                }

            }
        });

        documentsCrv.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getDocuments(false);
            }
        });

        documentsCrv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDocuments(true);
            }
        });

        documentsCrv.setErrorModel(this, ordersApiViewModel.getErrorModelLiveData());

        setGridSpan(1);
    }

    private void setGridSpan(int spanCount) {
        List<DocumentsApiResponseModel.ResultBean> beanList = new ArrayList<>();

        if (documentListAdapter != null) {
            beanList = documentListAdapter.getDataList();
        }

        gridLayoutManager = new GridLayoutManager(getActivity(), spanCount, LinearLayoutManager.VERTICAL, false);
        documentsCrv.setLayoutManager(gridLayoutManager);

        documentListAdapter = new DocumentListAdapter(getActivity(), isFromHome, spanCount);
        documentsCrv.getRecyclerView().setAdapter(documentListAdapter);

        documentListAdapter.setData(beanList, 1);
    }

    private void getDocuments(boolean isShowProgress) {
        if (!isApiRequested) {
            isApiRequested = true;

            if (isFromHome) {
                ordersApiViewModel.getDocuments(page, isShowProgress);
            } else {
                ordersApiViewModel.getUserDocuments(page, commonUserApiResponseModel.getUser_guid(), isShowProgress);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
            case R.id.add_fab:
                addFab.setClickable(false);
                startActivityForResult(new Intent(getActivity(), CreateOrderActivity.class).putExtras(getArguments()), RequestID.REQ_UPDATE_DOCUMENT);
                break;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && onResume) {
            getDocuments(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestID.REQ_UPDATE_DOCUMENT && resultCode == Activity.RESULT_OK) {
            getDocuments(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        addFab.setClickable(true);
        onResume = true;
        setUserVisibleHint(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.hideOverlay();
    }

}
