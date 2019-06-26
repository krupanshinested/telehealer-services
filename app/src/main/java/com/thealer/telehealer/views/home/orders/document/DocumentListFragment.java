package com.thealer.telehealer.views.home.orders.document;

import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.documents.DocumentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.visits.UpdateVisitRequestModel;
import com.thealer.telehealer.apilayer.models.visits.VisitsApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VisitConstants;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.common.OverlayViewConstants;
import com.thealer.telehealer.views.home.orders.CreateOrderActivity;
import com.thealer.telehealer.views.home.recents.VisitDetailConstants;

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
    private VisitsApiViewModel visitsApiViewModel;

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
    private String doctorGuid = null, userGuid = null;
    private String orderId;
    private int mode = Constants.VIEW_MODE;
    private ArrayList<Integer> selectedList = new ArrayList<>();
    private TextView nextTv;
    private ImageView closeIv;
    private HashMap<Integer, DocumentsApiResponseModel.ResultBean> selectedDocumentMap = new HashMap<>();

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

        visitsApiViewModel = ViewModelProviders.of(this).get(VisitsApiViewModel.class);
        attachObserverInterface.attachObserver(visitsApiViewModel);

        visitsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel.isSuccess()) {
                        sendSuccessViewBroadCast(getActivity(), true, getString(R.string.success), String.format(getString(R.string.associate_order_success), getString(R.string.documents)));
                    }
                }
            }
        });

        visitsApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    sendSuccessViewBroadCast(getActivity(), false, getString(R.string.failure), String.format(getString(R.string.associate_order_failure), getString(R.string.documents)));
                }
            }
        });
    }

    private void setData() {

        documentsCrv.setNextPage(documentsApiResponseModel.getNext());

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
        nextTv = (TextView) view.findViewById(R.id.next_tv);
        closeIv = (ImageView) view.findViewById(R.id.close_iv);

        inflateToolbarMenu();

        backIv.setOnClickListener(this);
        addFab.setOnClickListener(this);
        nextTv.setOnClickListener(this);

        toolbarTitle.setText(getString(R.string.documents));

        if (getArguments() != null) {
            isFromHome = getArguments().getBoolean(Constants.IS_FROM_HOME);
            if (getArguments().getBoolean(ArgumentKeys.IS_HIDE_TOOLBAR, false)) {
                appbarLayout.setVisibility(View.GONE);
            }
            commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            if (commonUserApiResponseModel != null) {
                userGuid = commonUserApiResponseModel.getUser_guid();
            }

            CommonUserApiResponseModel doctorDetail = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);
            if (doctorDetail != null)
                doctorGuid = doctorDetail.getUser_guid();

            orderId = getArguments().getString(ArgumentKeys.ORDER_ID);

            if (orderId != null) {
                mode = Constants.EDIT_MODE;
            }

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

        updateToolbarOptions();
    }

    private void inflateToolbarMenu() {
        if (toolbar.getMenu() != null) {
            toolbar.getMenu().clear();
        }
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

    }

    private void updateToolbarOptions() {
        switch (mode) {
            case Constants.VIEW_MODE:
                nextTv.setVisibility(View.GONE);
                inflateToolbarMenu();
                break;
            case Constants.EDIT_MODE:
                toolbar.getMenu().clear();
                nextTv.setVisibility(View.VISIBLE);
                nextTv.setText(getString(R.string.Save));
                enableOrDisableNext();
                break;
        }
    }

    private void setGridSpan(int spanCount) {
        List<DocumentsApiResponseModel.ResultBean> beanList = new ArrayList<>();

        if (documentListAdapter != null) {
            beanList = documentListAdapter.getDataList();
        }

        gridLayoutManager = new GridLayoutManager(getActivity(), spanCount, LinearLayoutManager.VERTICAL, false);
        documentsCrv.setLayoutManager(gridLayoutManager);

        documentListAdapter = new DocumentListAdapter(getActivity(), isFromHome, spanCount, mode, new OnListItemSelectInterface() {
            @Override
            public void onListItemSelected(int position, Bundle bundle) {
                if (bundle != null) {
                    DocumentsApiResponseModel.ResultBean selectedDocument = (DocumentsApiResponseModel.ResultBean) bundle.getSerializable(ArgumentKeys.SELECTED_DOCUMENT);
                    if (selectedDocument != null) {
                        int id = selectedDocument.getUser_file_id();
                        if (selectedList.contains(id)) {
                            selectedList.remove((Object) id);
                        } else {
                            selectedList.add(id);
                        }
                        if (orderId != null) {
                            if (selectedDocumentMap.containsKey(id)) {
                                selectedDocumentMap.remove((Object) id);
                            } else {
                                selectedDocumentMap.put(id, selectedDocument);
                            }
                        }
                        enableOrDisableNext();
                    }
                }
            }
        });
        documentsCrv.getRecyclerView().setAdapter(documentListAdapter);

        documentListAdapter.setData(beanList, 1);
    }

    private void enableOrDisableNext() {
        if (selectedList.isEmpty()) {
            nextTv.setVisibility(View.GONE);
        } else {
            nextTv.setVisibility(View.VISIBLE);
        }
    }

    private void getDocuments(boolean isShowProgress) {
        if (!isApiRequested) {
            isApiRequested = true;

            if (isFromHome) {
                ordersApiViewModel.getDocuments(page, isShowProgress);
            } else {
                ordersApiViewModel.getUserDocuments(page, commonUserApiResponseModel.getUser_guid(), doctorGuid, isShowProgress);
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
            case R.id.cancel_tv:
                onCloseActionInterface.onClose(false);
                break;
            case R.id.next_tv:
                if (getTargetFragment() != null) {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
                            new Intent()
                                    .putExtra(ArgumentKeys.UPDATE_TYPE, VisitDetailConstants.VISIT_TYPE_FILES)
                                    .putExtra(ArgumentKeys.SELECTED_FILES, selectedDocumentMap)
                                    .putExtra(ArgumentKeys.SELECTED_LIST_ID, selectedList));
                }
                onCloseActionInterface.onClose(false);
//                associateVisitDocuments(orderId);
                break;
        }
    }

    private void associateVisitDocuments(String orderId) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.please_wait));
        bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, String.format(getString(R.string.posting_your_visit_association_request), getString(R.string.documents)));

        showSuccessView(this, RequestID.REQ_SHOW_SUCCESS_VIEW, bundle);

        UpdateVisitRequestModel updateVisitRequestModel = new UpdateVisitRequestModel();
        updateVisitRequestModel.setAssociation_type(VisitConstants.TYPE_FILES);
        updateVisitRequestModel.setAdd_associations(selectedList);

        visitsApiViewModel.updateOrder(orderId, updateVisitRequestModel, doctorGuid, false);
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

        switch (requestCode) {
            case RequestID.REQ_UPDATE_DOCUMENT:
                if (resultCode == Activity.RESULT_OK) {
                    getDocuments(true);
                }
                break;
            case RequestID.REQ_SHOW_SUCCESS_VIEW:
                if (getTargetFragment() != null) {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent().putExtra(ArgumentKeys.UPDATE_TYPE, VisitDetailConstants.VISIT_TYPE_FILES));
                }
                onCloseActionInterface.onClose(false);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        addFab.setClickable(true);
        onResume = true;
        if (getUserVisibleHint())
            setUserVisibleHint(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.hideOverlay();
    }

}
