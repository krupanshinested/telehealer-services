package com.thealer.telehealer.views.home.orders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersIdListApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersPrescriptionApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersSpecialistApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.forms.OrdersUserFormsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.OrdersLabApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.miscellaneous.MiscellaneousApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.GetRadiologyResponseModel;
import com.thealer.telehealer.apilayer.models.visits.UpdateVisitRequestModel;
import com.thealer.telehealer.apilayer.models.visits.VisitsApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.VisitConstants;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.home.recents.VisitDetailConstants;
import com.thealer.telehealer.views.home.recents.adapterModels.VisitOrdersAdapterModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aswin on 01,May,2019
 */
public class OrderSelectionListFragment extends OrdersBaseFragment implements View.OnClickListener {
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private TextView nextTv;
    private ImageView closeIv;
    private CustomRecyclerView ordersListCrv;

    private OnCloseActionInterface onCloseActionInterface;
    private AttachObserverInterface attachObserverInterface;
    private VisitsApiViewModel visitsApiViewModel;
    private OrdersApiViewModel ordersApiViewModel;
    private OrdersIdListApiResponseModel ordersIdListApiResponseModel;
    private OrderSelectionListAdapter orderSelectionListAdapter;

    private int mode = Constants.VIEW_MODE;
    private String orderId, userGuid, doctorGuid;
    private CommonUserApiResponseModel userModel, doctorModel;
    private List<Integer> prescriptionList = new ArrayList<>();
    private List<Integer> labList = new ArrayList<>();
    private List<Integer> referralList = new ArrayList<>();
    private List<Integer> radiologyList = new ArrayList<>();
    private List<Integer> miscellaneousList = new ArrayList<>();
    private List<Integer> formList = new ArrayList<>();
    private List<Integer> selectedList = null;
    private boolean isSuccessViewShown = false;
    private String currentUpdateType = null;
    private HashMap<Integer, OrdersLabApiResponseModel.LabsResponseBean> labsMap = new HashMap<>();
    private HashMap<Integer, GetRadiologyResponseModel.ResultBean> xraysMap = new HashMap<>();
    private HashMap<Integer, OrdersSpecialistApiResponseModel.ResultBean> specialistsMap = new HashMap<>();
    private HashMap<Integer, OrdersPrescriptionApiResponseModel.OrdersResultBean> prescriptionsMap = new HashMap<>();
    private HashMap<Integer, MiscellaneousApiResponseModel.ResultBean> miscellaneousMap = new HashMap<>();
    private HashMap<Integer, OrdersUserFormsApiResponseModel> formMap = new HashMap<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();

        visitsApiViewModel = ViewModelProviders.of(this).get(VisitsApiViewModel.class);
        visitsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel.isSuccess()) {
                        switch (currentUpdateType) {
                            case VisitDetailConstants.VISIT_TYPE_PRESCRIPTIONS:
                                prescriptionList.clear();
                                break;
                            case VisitDetailConstants.VISIT_TYPE_LABS:
                                labList.clear();
                                break;
                            case VisitDetailConstants.VISIT_TYPE_XRAYS:
                                radiologyList.clear();
                                break;
                            case VisitDetailConstants.VISIT_TYPE_SPECIALIST:
                                referralList.clear();
                                break;
                            case VisitDetailConstants.VISIT_TYPE_MISCELLANEOUS:
                                miscellaneousList.clear();
                                break;
                        }
                        if (isHasNextRequest()) {
                            addVisit(orderId);
                        } else {
                            isSuccessViewShown = false;
                            sendSuccessViewBroadCast(getActivity(), true, getString(R.string.success), String.format(getString(R.string.associate_order_success), VisitConstants.TYPE_DIET));
                        }
                    }
                }
            }
        });

        visitsApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    sendSuccessViewBroadCast(getActivity(), false, getString(R.string.failure), String.format(getString(R.string.associate_order_failure), VisitConstants.TYPE_DIET));
                }
            }
        });

        ordersApiViewModel = ViewModelProviders.of(this).get(OrdersApiViewModel.class);
        attachObserverInterface.attachObserver(ordersApiViewModel);

        ordersApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    ordersIdListApiResponseModel = (OrdersIdListApiResponseModel) baseApiResponseModel;
                    ordersApiViewModel.getFormsDetail(userGuid, doctorGuid, null, true);
                }
            }
        });

        ordersApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {
                    ArrayList<OrdersUserFormsApiResponseModel> formsList = (ArrayList<OrdersUserFormsApiResponseModel>) (Object) baseApiResponseModels;

                    orderSelectionListAdapter.setData(ordersIdListApiResponseModel, formsList);

                    if (ordersIdListApiResponseModel.isEmpty() && formsList.isEmpty()) {
                        ordersListCrv.showOrhideEmptyState(true);
                    } else {
                        ordersListCrv.showOrhideEmptyState(false);
                    }
                }
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_selection, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        nextTv = (TextView) view.findViewById(R.id.next_tv);
        closeIv = (ImageView) view.findViewById(R.id.close_iv);
        ordersListCrv = (CustomRecyclerView) view.findViewById(R.id.orders_list_crv);

        toolbarTitle.setText(getString(R.string.orders));

        backIv.setOnClickListener(this);
        nextTv.setOnClickListener(this);

        ordersListCrv.setEmptyState(EmptyViewConstants.EMPTY_PRESCRIPTION);
        ordersListCrv.showOrhideEmptyState(false);
        ordersListCrv.setErrorModel(this, ordersApiViewModel.getErrorModelLiveData());
        ordersListCrv.getSwipeLayout().setEnabled(false);

        if (getArguments() != null) {
            userModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            if (userModel != null) {
                userGuid = userModel.getUser_guid();
            }

            doctorModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);

            orderId = getArguments().getString(ArgumentKeys.ORDER_ID);

            if (orderId != null) {
                mode = Constants.EDIT_MODE;
            }
        }

        updateUI();

        setupAdapter();

        getUserOrders();
    }

    private void getUserOrders() {
        if (UserType.isUserAssistant()) {
            if (doctorModel != null) {
                doctorGuid = doctorModel.getUser_guid();
            }
        }
        ordersApiViewModel.getOrderdsDetail(userGuid, doctorGuid, null, true);
    }

    private void setupAdapter() {
        ArrayList<Integer> selectedList = new ArrayList<>();
        if (getArguments() != null) {
            miscellaneousMap = (HashMap<Integer, MiscellaneousApiResponseModel.ResultBean>) getArguments().getSerializable(ArgumentKeys.SELECTED_MISCELLANEOUS);
            prescriptionsMap = (HashMap<Integer, OrdersPrescriptionApiResponseModel.OrdersResultBean>) getArguments().getSerializable(ArgumentKeys.SELECTED_PRESCRIPTION);
            specialistsMap = (HashMap<Integer, OrdersSpecialistApiResponseModel.ResultBean>) getArguments().getSerializable(ArgumentKeys.SELECTED_SPECIALIST);
            xraysMap = (HashMap<Integer, GetRadiologyResponseModel.ResultBean>) getArguments().getSerializable(ArgumentKeys.SELECTED_XRAYS);
            labsMap = (HashMap<Integer, OrdersLabApiResponseModel.LabsResponseBean>) getArguments().getSerializable(ArgumentKeys.SELECTED_LABS);
            formMap = (HashMap<Integer, OrdersUserFormsApiResponseModel>) getArguments().getSerializable(ArgumentKeys.SELECTED_FORMS);

            if (!miscellaneousMap.isEmpty())
                miscellaneousList = new ArrayList<>(miscellaneousMap.keySet());
            if (!prescriptionsMap.isEmpty())
                prescriptionList = new ArrayList<>(prescriptionsMap.keySet());
            if (!specialistsMap.isEmpty())
                referralList = new ArrayList<>(specialistsMap.keySet());
            if (!xraysMap.isEmpty())
                radiologyList = new ArrayList<>(xraysMap.keySet());
            if (!labsMap.isEmpty())
                labList = new ArrayList<>(labsMap.keySet());
            if (!formMap.isEmpty())
                formList = new ArrayList<>(formMap.keySet());

            selectedList.addAll(miscellaneousList);
            selectedList.addAll(prescriptionList);
            selectedList.addAll(referralList);
            selectedList.addAll(radiologyList);
            selectedList.addAll(labList);
            selectedList.addAll(formList);

//            enableOrDisableNext();
            Log.e(TAG, "setupAdapter: " + selectedList.toString());
        }
        orderSelectionListAdapter = new OrderSelectionListAdapter(getActivity(), selectedList, userModel, mode, new OnListItemSelectInterface() {
            @Override
            public void onListItemSelected(int position, Bundle bundle) {
                if (bundle != null) {
                    VisitOrdersAdapterModel visitOrdersAdapterModel = (VisitOrdersAdapterModel) bundle.getSerializable(ArgumentKeys.ORDER_DATA);
                    if (visitOrdersAdapterModel != null) {
                        int id = visitOrdersAdapterModel.getReferralId();
                        switch (visitOrdersAdapterModel.getOrderType()) {
                            case OrderConstant.ORDER_PRESCRIPTIONS:
                                if (prescriptionList.contains(id)) {
                                    prescriptionList.remove((Object) id);
                                } else {
                                    prescriptionList.add(id);
                                }
                                if (orderId != null) {
                                    if (prescriptionsMap.containsKey(id)) {
                                        prescriptionsMap.remove((Object) id);
                                    } else {
                                        prescriptionsMap.put(id, visitOrdersAdapterModel.getPrescriptions());
                                    }
                                }
                                break;
                            case OrderConstant.ORDER_LABS:
                                if (labList.contains(id)) {
                                    labList.remove((Object) id);
                                } else {
                                    labList.add(id);
                                }
                                if (orderId != null) {
                                    if (labsMap.containsKey(id)) {
                                        labsMap.remove((Object) id);
                                    } else {
                                        labsMap.put(id, visitOrdersAdapterModel.getLabs());
                                    }
                                }
                                break;
                            case OrderConstant.ORDER_REFERRALS:
                                if (referralList.contains(id)) {
                                    referralList.remove((Object) id);
                                } else {
                                    referralList.add(id);
                                }
                                if (orderId != null) {
                                    if (specialistsMap.containsKey(id)) {
                                        specialistsMap.remove((Object) id);
                                    } else {
                                        specialistsMap.put(id, visitOrdersAdapterModel.getSpecialists());
                                    }
                                }
                                break;
                            case OrderConstant.ORDER_RADIOLOGY:
                                if (radiologyList.contains(id)) {
                                    radiologyList.remove((Object) id);
                                } else {
                                    radiologyList.add(id);
                                }
                                if (orderId != null) {
                                    if (xraysMap.containsKey(id)) {
                                        xraysMap.remove((Object) id);
                                    } else {
                                        xraysMap.put(id, visitOrdersAdapterModel.getXrays());
                                    }
                                }
                                break;
                            case OrderConstant.ORDER_MISC:
                                if (miscellaneousList.contains(id)) {
                                    miscellaneousList.remove((Object) id);
                                } else {
                                    miscellaneousList.add(id);
                                }
                                if (orderId != null) {
                                    if (miscellaneousMap.containsKey(id)) {
                                        miscellaneousMap.remove((Object) id);
                                    } else {
                                        miscellaneousMap.put(id, visitOrdersAdapterModel.getMiscellaneous());
                                    }
                                }
                                break;
                            case OrderConstant.ORDER_FORM:
                                if (formList.contains(id)) {
                                    formList.remove((Object) id);
                                } else {
                                    formList.add(id);
                                }
                                if (orderId != null) {
                                    if (formMap.containsKey(id)) {
                                        formMap.remove((Object) id);
                                    } else {
                                        formMap.put(id, visitOrdersAdapterModel.getForms());
                                    }
                                }
                                break;
                        }
                    }
//                    enableOrDisableNext();
                }
            }
        });
        ordersListCrv.getRecyclerView().setAdapter(orderSelectionListAdapter);

    }

    private void updateUI() {
        updateToolbarOption();
        switch (mode) {
            case Constants.VIEW_MODE:
                setNextTitle(getString(R.string.add_visit));
                break;
            case Constants.EDIT_MODE:
                setNextTitle(getString(R.string.Save));
//                enableOrDisableNext();
                break;
        }
    }

    private void enableOrDisableNext() {
        boolean isEnabled = true;

        if (prescriptionList.isEmpty() && referralList.isEmpty() && labList.isEmpty() && miscellaneousList.isEmpty() && radiologyList.isEmpty()
                && formList.isEmpty())
            isEnabled = false;

        if (isEnabled) {
            nextTv.setVisibility(View.VISIBLE);
        } else {
            nextTv.setVisibility(View.GONE);
        }
    }

    private void setNextTitle(String title) {
        nextTv.setText(title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
            case R.id.cancel_tv:
                mode = Constants.VIEW_MODE;
                updateToolbarOption();
                clearAllList();
                break;
            case R.id.next_tv:
                switch (mode) {
                    case Constants.VIEW_MODE:
                        mode = Constants.EDIT_MODE;
                        updateToolbarOption();
                        break;
                    case Constants.EDIT_MODE:
                        if (orderId == null) {
                            addVisit(orderId);
                        } else {
                            if (getTargetFragment() != null)
                                getTargetFragment().onActivityResult(RequestID.REQ_VISIT_UPDATE, Activity.RESULT_OK,
                                        new Intent().putExtra(ArgumentKeys.UPDATE_TYPE, VisitDetailConstants.VISIT_TYPE_ORDERS)
                                                .putExtra(ArgumentKeys.SELECTED_PRESCRIPTION, prescriptionsMap)
                                                .putExtra(ArgumentKeys.SELECTED_SPECIALIST, specialistsMap)
                                                .putExtra(ArgumentKeys.SELECTED_XRAYS, xraysMap)
                                                .putExtra(ArgumentKeys.SELECTED_LABS, labsMap)
                                                .putExtra(ArgumentKeys.SELECTED_MISCELLANEOUS, miscellaneousMap)
                                                .putExtra(ArgumentKeys.SELECTED_FORMS, formMap));

                            onCloseActionInterface.onClose(false);
                        }
                        break;
                }
                break;
        }
    }

    private void clearAllList() {
        prescriptionList.clear();
        labList.clear();
        referralList.clear();
        radiologyList.clear();
        miscellaneousList.clear();
        formList.clear();
        formMap.clear();
        labsMap.clear();
        prescriptionsMap.clear();
        specialistsMap.clear();
        xraysMap.clear();
        miscellaneousMap.clear();
    }

    private void updateToolbarOption() {
        if (orderSelectionListAdapter != null)
            orderSelectionListAdapter.setMode(mode);
        switch (mode) {
            case Constants.VIEW_MODE:
                nextTv.setText(getString(R.string.add_visit));
                nextTv.setEnabled(true);
                nextTv.setAlpha(1);
                break;
            case Constants.EDIT_MODE:
                nextTv.setText(getString(R.string.Save));
                break;
        }
    }

    private void addVisit(String order_id) {

        if (!prescriptionList.isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_PRESCRIPTIONS;
            selectedList = prescriptionList;
        } else if (!referralList.isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_SPECIALIST;
            selectedList = referralList;
        } else if (!labList.isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_LABS;
            selectedList = labList;
        } else if (!radiologyList.isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_XRAYS;
            selectedList = radiologyList;
        } else if (!miscellaneousList.isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_MISCELLANEOUS;
            selectedList = miscellaneousList;
        } else if (!formList.isEmpty()) {
            currentUpdateType = VisitDetailConstants.VISIT_TYPE_FORMS;
            selectedList = formList;
        }

        if (!isSuccessViewShown) {
            isSuccessViewShown = true;
            Bundle bundle = new Bundle();
            bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.loading));
            bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.updating_your_visit_please_wait));

            showSuccessView(this, RequestID.REQ_SHOW_SUCCESS_VIEW, bundle);
        }

        if (selectedList != null && !selectedList.isEmpty()) {
            UpdateVisitRequestModel updateVisitRequestModel = new UpdateVisitRequestModel();
            updateVisitRequestModel.setAssociation_type(currentUpdateType);
            updateVisitRequestModel.setAdd_associations(selectedList);

            visitsApiViewModel.updateOrder(order_id, updateVisitRequestModel, doctorGuid, false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestID.REQ_SHOW_SUCCESS_VIEW:
                if (resultCode == Activity.RESULT_OK) {
                    if (getTargetFragment() != null)
                        getTargetFragment().onActivityResult(RequestID.REQ_VISIT_UPDATE, Activity.RESULT_OK, new Intent().putExtra(ArgumentKeys.UPDATE_TYPE, VisitDetailConstants.VISIT_TYPE_ORDERS));
                }
                break;
        }
    }

    private boolean isHasNextRequest() {
        return !prescriptionList.isEmpty() ||
                !referralList.isEmpty() ||
                !labList.isEmpty() ||
                !radiologyList.isEmpty() ||
                !miscellaneousList.isEmpty() ||
                !formList.isEmpty();
    }

}
