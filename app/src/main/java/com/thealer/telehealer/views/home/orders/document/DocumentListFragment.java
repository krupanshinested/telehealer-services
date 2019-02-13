package com.thealer.telehealer.views.home.orders.document;

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
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.documents.DocumentsApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomExpandableListView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.PreferenceConstants;
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
    private CustomExpandableListView documentsCelv;
    private FloatingActionButton addFab;

    private OnCloseActionInterface onCloseActionInterface;
    private OrdersApiViewModel ordersApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private DocumentsApiResponseModel documentsApiResponseModel;

    private DocumentListAdapter documentListAdapter;
    private List<String> headerList = new ArrayList<>();
    private HashMap<String, List<DocumentsApiResponseModel.ResultBean>> childList = new HashMap<>();
    private CommonUserApiResponseModel commonUserApiResponseModel;

    private int page = 1;
    private boolean isFromHome = true;
    private boolean isApiRequested = false;

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
                documentsCelv.getSwipeLayout().setRefreshing(false);
                if (baseApiResponseModel != null) {
                    documentsApiResponseModel = (DocumentsApiResponseModel) baseApiResponseModel;
                    if (UserType.isUserPatient() && page == 1 && documentsApiResponseModel.getResult().size() == 0) {
                        if (!appPreference.getBoolean(PreferenceConstants.IS_OVERLAY_ADD_DOCUMENT)) {
                            appPreference.setBoolean(PreferenceConstants.IS_OVERLAY_ADD_DOCUMENT, true);

                            documentsCelv.showOrHideMessage(false);

                            Utils.showOverlay(getActivity(), addFab, OverlayViewConstants.OVERLAY_NO_DOCUMENT, new DismissListener() {
                                @Override
                                public void onDismiss(@org.jetbrains.annotations.Nullable String s) {
                                    documentsCelv.showOrHideMessage(true);
                                }

                                @Override
                                public void onSkipped(@org.jetbrains.annotations.Nullable String s) {

                                }
                            });
                        }
                    }
                    updateList();
                }
            }
        });
    }

    private void updateList() {
        if (page == 1) {
            headerList.clear();
            childList.clear();
        }

        if (documentsApiResponseModel.getResult().size() > 0) {
            for (int i = 0; i < documentsApiResponseModel.getResult().size(); i++) {

                DocumentsApiResponseModel.ResultBean resultBean = documentsApiResponseModel.getResult().get(i);

                String date = Utils.getDayMonthYear(resultBean.getCreated_at());

                if (!headerList.contains(date)) {
                    headerList.add(date);
                }

                List<DocumentsApiResponseModel.ResultBean> resultBeanList = new ArrayList<>();

                if (childList.containsKey(date)) {
                    resultBeanList.addAll(childList.get(date));
                }

                resultBeanList.add(resultBean);

                childList.put(date, resultBeanList);

            }

            documentListAdapter.setData(headerList, childList, documentsApiResponseModel, page);

            documentsCelv.setTotalCount(documentsApiResponseModel.getCount() + headerList.size());


            if (documentsCelv.getExpandableView().getCount() > 0) {
                documentsCelv.hideEmptyState();
                expandListView();
            } else {
                documentsCelv.showEmptyState();
            }
            Log.e("aswin", "updateList: " + documentsCelv.getExpandableView().getCount());

            isApiRequested = false;
        } else {
            documentsCelv.setScrollable(false);
            if (page == 1) {
                documentsCelv.showEmptyState();
            }
        }
    }

    private void expandListView() {
        for (int i = 0; i < headerList.size(); i++) {
            documentsCelv.getExpandableView().expandGroup(i);
        }
        documentsCelv.setScrollable(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_document_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        documentsCelv = (CustomExpandableListView) view.findViewById(R.id.documents_celv);
        addFab = (FloatingActionButton) view.findViewById(R.id.add_fab);

        backIv.setOnClickListener(this);
        addFab.setOnClickListener(this);

        toolbarTitle.setText(OrderConstant.ORDER_DOCUMENTS);

        if (getArguments() != null) {
            isFromHome = getArguments().getBoolean(Constants.IS_FROM_HOME);
            commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            if (!isFromHome) {
                addFab.hide();
                documentsCelv.setEmptyState(EmptyViewConstants.EMPTY_DOCUMENTS);
            } else {
                documentsCelv.setEmptyState(EmptyViewConstants.EMPTY_DOCUMENTS_WITH_BTN);
            }
        }

        documentsCelv.hideEmptyState();

        documentListAdapter = new DocumentListAdapter(getActivity(), headerList, childList, isFromHome);

        documentsCelv.getExpandableView().setAdapter(documentListAdapter);

        documentsCelv.setScrollable(true);

        documentsCelv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                if (!isApiRequested) {
                    documentsCelv.setScrollable(false);
                    Log.e("aswin", "onPaginate: ");
                    page = page + 1;
                    getDocuments(false);
                } else {
                    Log.e("aswin", "onPaginate: false");
                }

            }
        });

        documentsCelv.getExpandableView().setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

        documentsCelv.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDocuments(false);
            }
        });
        getDocuments(true);

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
                startActivity(new Intent(getActivity(), CreateOrderActivity.class).putExtras(getArguments()));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        addFab.setClickable(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.hideOverlay();
    }

}
