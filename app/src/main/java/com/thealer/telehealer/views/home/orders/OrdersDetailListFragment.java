package com.thealer.telehealer.views.home.orders;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.thealer.telehealer.apilayer.models.orders.OrdersPrescriptionApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersSpecialistApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.forms.OrdersUserFormsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.OrdersLabApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.GetRadiologyResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomExpandableListView;
import com.thealer.telehealer.common.GetUserDetails;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.settings.SignatureActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Aswin on 21,November,2018
 */
public class OrdersDetailListFragment extends BaseFragment implements View.OnClickListener {

    private ImageView backIv;
    private TextView toolbarTitle;
    private CustomExpandableListView orderDetailCelv;
    private FloatingActionButton addFab;

    private OrdersApiViewModel ordersApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private CommonUserApiResponseModel commonUserApiResponseModel;
    private boolean isFromHome;
    private int page = 1;
    private String selectedItem;
    private List<String> headerList = new ArrayList<>();
    private HashMap<String, List<OrdersDetailListAdapterModel>> childList = new HashMap<>();
    private boolean isVisibleToUser;
    private OrdersDetailListAdapter ordersDetailListAdapter;
    private ExpandableListView expandableListView;
    private OnCloseActionInterface onCloseActionInterface;
    private Set<String> userGuid = new HashSet<>();
    private HashMap<String, CommonUserApiResponseModel> userDetailHashMap = new HashMap<>();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
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
                if (baseApiResponseModel != null) {
                    updateList(baseApiResponseModel);
                }
            }
        });

        ordersApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null && baseApiResponseModels.size() > 0) {
                    updateArrayList(baseApiResponseModels);
                }
            }
        });
    }

    private void updateArrayList(ArrayList<BaseApiResponseModel> baseApiResponseModels) {
        headerList.clear();
        childList.clear();

        ArrayList<OrdersUserFormsApiResponseModel> formsApiResponseModels = (ArrayList<OrdersUserFormsApiResponseModel>) (Object) baseApiResponseModels;

        for (OrdersUserFormsApiResponseModel formsApiResponseModel : formsApiResponseModels) {
            String date = Utils.getDayMonthYear(formsApiResponseModel.getCreated_at());

            if (!headerList.contains(date)) {
                headerList.add(date);
            }

            OrdersDetailListAdapterModel listAdapterModel = new OrdersDetailListAdapterModel();

            listAdapterModel.setSubTitle(formsApiResponseModel.getName());
            listAdapterModel.setForm(true);
            listAdapterModel.setOrdersFormsApiResponseModel(formsApiResponseModel);

            List<OrdersDetailListAdapterModel> childListData = new ArrayList<>();

            if (childList.containsKey(date)) {
                childListData.addAll(childList.get(date));
            }
            childListData.add(listAdapterModel);

            childList.put(date, childListData);

            if (formsApiResponseModel.getAssigned_by_user() != null && formsApiResponseModel.getAssigned_by_user().getUser_guid() != null) {
                userGuid.add(formsApiResponseModel.getAssigned_by_user().getUser_guid());
            }
            if (formsApiResponseModel.getAssigned_to_user() != null && formsApiResponseModel.getAssigned_to_user().getUser_guid() != null) {
                userGuid.add(formsApiResponseModel.getAssigned_to_user().getUser_guid());
            }
        }
        ordersDetailListAdapter.setData(headerList, childList);

        if (headerList.size() > 0) {
            orderDetailCelv.hideEmptyState();
            expandListView();
        } else {
            orderDetailCelv.showEmptyState();
        }

        getUserDetails(userGuid);

    }

    private void updateList(BaseApiResponseModel baseApiResponseModel) {

        headerList.clear();
        childList.clear();
        userGuid.clear();

        if (baseApiResponseModel instanceof OrdersLabApiResponseModel) {

            OrdersLabApiResponseModel labApiResponseModel = (OrdersLabApiResponseModel) baseApiResponseModel;

            for (int i = 0; i < labApiResponseModel.getLabsResponseBeanList().size(); i++) {

                String date = Utils.getDayMonthYear(labApiResponseModel.getLabsResponseBeanList().get(i).getCreated_at());

                OrdersDetailListAdapterModel listAdapterModel = new OrdersDetailListAdapterModel();

                if (!headerList.contains(date)) {
                    headerList.add(date);
                }

                listAdapterModel.setSubTitle(labApiResponseModel.getLabsResponseBeanList().get(i).getDetail().getDisplayName());

                listAdapterModel.setCommonResultResponseModel(labApiResponseModel.getLabsResponseBeanList().get(i));

                List<OrdersDetailListAdapterModel> childListData = new ArrayList<>();

                if (childList.containsKey(date)) {
                    childListData.addAll(childList.get(date));
                }

                childListData.add(listAdapterModel);

                childList.put(date, childListData);

                if (labApiResponseModel.getLabsResponseBeanList().get(i).getDoctor() != null &&
                        labApiResponseModel.getLabsResponseBeanList().get(i).getDoctor().getUser_guid() != null) {
                    addToUserGuidList(labApiResponseModel.getLabsResponseBeanList().get(i).getDoctor().getUser_guid());
                }
                if (labApiResponseModel.getLabsResponseBeanList().get(i).getPatient() != null &&
                        labApiResponseModel.getLabsResponseBeanList().get(i).getPatient().getUser_guid() != null) {
                    addToUserGuidList(labApiResponseModel.getLabsResponseBeanList().get(i).getPatient().getUser_guid());
                }
            }

        } else if (baseApiResponseModel instanceof OrdersPrescriptionApiResponseModel) {

            OrdersPrescriptionApiResponseModel prescriptionApiResponseModel = (OrdersPrescriptionApiResponseModel) baseApiResponseModel;

            for (int i = 0; i < prescriptionApiResponseModel.getOrdersResultBeanList().size(); i++) {

                String date = Utils.getDayMonthYear(prescriptionApiResponseModel.getOrdersResultBeanList().get(i).getCreated_at());

                OrdersDetailListAdapterModel listAdapterModel = new OrdersDetailListAdapterModel();

                if (!headerList.contains(date)) {
                    headerList.add(date);
                }

                listAdapterModel.setSubTitle(prescriptionApiResponseModel.getOrdersResultBeanList().get(i).getDetail().getRx_drug_name());

                listAdapterModel.setCommonResultResponseModel(prescriptionApiResponseModel.getOrdersResultBeanList().get(i));

                List<OrdersDetailListAdapterModel> childListData = new ArrayList<>();

                if (childList.containsKey(date)) {
                    childListData.addAll(childList.get(date));
                }

                childListData.add(listAdapterModel);

                childList.put(date, childListData);

                if (prescriptionApiResponseModel.getOrdersResultBeanList().get(i).getDoctor() != null &&
                        prescriptionApiResponseModel.getOrdersResultBeanList().get(i).getDoctor().getUser_guid() != null) {
                    addToUserGuidList(prescriptionApiResponseModel.getOrdersResultBeanList().get(i).getDoctor().getUser_guid());
                }
                if (prescriptionApiResponseModel.getOrdersResultBeanList().get(i).getPatient() != null &&
                        prescriptionApiResponseModel.getOrdersResultBeanList().get(i).getPatient().getUser_guid() != null) {
                    addToUserGuidList(prescriptionApiResponseModel.getOrdersResultBeanList().get(i).getPatient().getUser_guid());
                }
            }

        } else if (baseApiResponseModel instanceof OrdersSpecialistApiResponseModel) {

            OrdersSpecialistApiResponseModel ordersSpecialistApiResponseModel = (OrdersSpecialistApiResponseModel) baseApiResponseModel;

            for (int i = 0; i < ordersSpecialistApiResponseModel.getResult().size(); i++) {

                String date = Utils.getDayMonthYear(ordersSpecialistApiResponseModel.getResult().get(i).getCreated_at());

                OrdersDetailListAdapterModel listAdapterModel = new OrdersDetailListAdapterModel();

                listAdapterModel.setSubTitle(ordersSpecialistApiResponseModel.getResult().get(i).getDetail().getCopy_to().getName());
                listAdapterModel.setCommonResultResponseModel(ordersSpecialistApiResponseModel.getResult().get(i));

                if (!headerList.contains(date)) {
                    headerList.add(date);
                }

                List<OrdersDetailListAdapterModel> childListData = new ArrayList<>();

                if (childList.containsKey(date)) {
                    childListData.addAll(childList.get(date));
                }

                childListData.add(listAdapterModel);

                childList.put(date, childListData);

                if (ordersSpecialistApiResponseModel.getResult().get(i).getDoctor() != null &&
                        ordersSpecialistApiResponseModel.getResult().get(i).getDoctor().getUser_guid() != null) {
                    addToUserGuidList(ordersSpecialistApiResponseModel.getResult().get(i).getDoctor().getUser_guid());
                }
                if (ordersSpecialistApiResponseModel.getResult().get(i).getPatient() != null &&
                        ordersSpecialistApiResponseModel.getResult().get(i).getPatient().getUser_guid() != null) {
                    addToUserGuidList(ordersSpecialistApiResponseModel.getResult().get(i).getPatient().getUser_guid());
                }

            }
        } else if (baseApiResponseModel instanceof GetRadiologyResponseModel) {
            GetRadiologyResponseModel getRadiologyResponseModel = (GetRadiologyResponseModel) baseApiResponseModel;

            for (int i = 0; i < getRadiologyResponseModel.getResultBeanList().size(); i++) {
                String date = Utils.getDayMonthYear(getRadiologyResponseModel.getResultBeanList().get(i).getCreated_at());

                OrdersDetailListAdapterModel ordersDetailListAdapterModel = new OrdersDetailListAdapterModel();

                StringBuilder title = null;
                for (int j = 0; j < getRadiologyResponseModel.getResultBeanList().get(i).getDetail().getLabs().get(0).getXRayTests().size(); j++) {
                    if (title == null) {
                        title = new StringBuilder(getRadiologyResponseModel.getResultBeanList().get(i).getDetail().getLabs().get(0).getXRayTests().get(j).getDisplayText());
                    } else {
                        title.append(",").append(getRadiologyResponseModel.getResultBeanList().get(i).getDetail().getLabs().get(0).getXRayTests().get(j).getDisplayText());
                    }
                }
                ordersDetailListAdapterModel.setSubTitle(title.toString());
                ordersDetailListAdapterModel.setCommonResultResponseModel(getRadiologyResponseModel.getResultBeanList().get(i));

                if (!headerList.contains(date)) {
                    headerList.add(date);
                }

                List<OrdersDetailListAdapterModel> childListData = new ArrayList<>();

                if (childList.containsKey(date)) {
                    childListData.addAll(childList.get(date));
                }

                childListData.add(ordersDetailListAdapterModel);

                childList.put(date, childListData);

                if (getRadiologyResponseModel.getResultBeanList().get(i).getDoctor() != null &&
                        getRadiologyResponseModel.getResultBeanList().get(i).getDoctor().getUser_guid() != null) {
                    addToUserGuidList(getRadiologyResponseModel.getResultBeanList().get(i).getDoctor().getUser_guid());
                }
                if (getRadiologyResponseModel.getResultBeanList().get(i).getPatient() != null &&
                        getRadiologyResponseModel.getResultBeanList().get(i).getPatient().getUser_guid() != null) {
                    addToUserGuidList(getRadiologyResponseModel.getResultBeanList().get(i).getPatient().getUser_guid());
                }
            }
        }

        ordersDetailListAdapter.setData(headerList, childList);

        if (headerList.size() > 0) {
            orderDetailCelv.hideEmptyState();
            expandListView();
        } else {
            orderDetailCelv.showEmptyState();
        }

        getUserDetails(userGuid);

    }

    private void addToUserGuidList(String user_guid) {
        if (!userDetailHashMap.containsKey(user_guid)) {
            userGuid.add(user_guid);
        }
    }

    private void getUserDetails(Set<String> userGuid) {
        GetUserDetails
                .getInstance(getActivity())
                .getDetails(userGuid)
                .getHashMapMutableLiveData()
                .observe(getActivity(),
                        new Observer<HashMap<String, CommonUserApiResponseModel>>() {
                            @Override
                            public void onChanged(@Nullable HashMap<String, CommonUserApiResponseModel> data) {
                                if (data != null) {
                                    userDetailHashMap.putAll(data);
                                    if (ordersDetailListAdapter != null) {
                                        ordersDetailListAdapter.setUserDetailHashMap(userDetailHashMap);
                                    }
                                }
                            }
                        });
    }

    private void expandListView() {
        for (int i = 0; i < headerList.size(); i++) {
            expandableListView.expandGroup(i);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders_detail_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        orderDetailCelv = (CustomExpandableListView) view.findViewById(R.id.order_detail_celv);
        addFab = (FloatingActionButton) view.findViewById(R.id.add_fab);

        backIv.setOnClickListener(this);
        addFab.setOnClickListener(this);

        if (getArguments() != null) {
            selectedItem = getArguments().getString(Constants.SELECTED_ITEM);

            isFromHome = getArguments().getBoolean(Constants.IS_FROM_HOME);

            if (!isFromHome) {
                commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            }

            if (selectedItem != null) {

                setEmptyState();

                ordersDetailListAdapter = new OrdersDetailListAdapter(getActivity(), headerList, childList, userDetailHashMap);

                expandableListView = orderDetailCelv.getExpandableView();

                expandableListView.setAdapter(ordersDetailListAdapter);

                orderDetailCelv.setOnPaginateInterface(new OnPaginateInterface() {
                    @Override
                    public void onPaginate() {
                        //handle pagination here
                    }
                });

                expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        return true;
                    }
                });
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        addFab.setClickable(true);
        makeApiCall();
    }

    private void makeApiCall() {
        if (selectedItem.equals(OrderConstant.ORDER_DOCUMENTS)) {

        } else if (selectedItem.equals(OrderConstant.ORDER_FORM)) {
            if (isFromHome) {
                ordersApiViewModel.getForms(true);
            } else {
                ordersApiViewModel.getUserForms(commonUserApiResponseModel.getUser_guid(), true);
            }
        } else if (selectedItem.equals(OrderConstant.ORDER_LABS)) {

            if (isFromHome) {
                ordersApiViewModel.getLabOrders(page, true);
            } else {
                ordersApiViewModel.getUserLabOrders(commonUserApiResponseModel.getUser_guid(), page, true);
            }

        } else if (selectedItem.equals(OrderConstant.ORDER_PRESCRIPTIONS)) {

            if (isFromHome) {
                ordersApiViewModel.getPrescriptionOrders(page, true);
            } else {
                ordersApiViewModel.getUserPrescriptionOrders(commonUserApiResponseModel.getUser_guid(), page, true);
            }

        } else if (selectedItem.equals(OrderConstant.ORDER_RADIOLOGY)) {

            if (isFromHome) {
                ordersApiViewModel.getRadiologyList(page, true);
            } else {
                ordersApiViewModel.getUserRadiologyList(commonUserApiResponseModel.getUser_guid(), page, true);
            }

        } else if (selectedItem.equals(OrderConstant.ORDER_REFERRALS)) {

            if (isFromHome) {
                ordersApiViewModel.getSpecialist(page, true);
            } else {
                ordersApiViewModel.getUserSpecialist(commonUserApiResponseModel.getUser_guid(), page, true);
            }

        }
    }

    private void showOrHideFab(boolean isShow) {
        if (isShow) {
            addFab.show();
        } else {
            addFab.hide();
        }
    }

    private void setEmptyState() {
        String emptyStateType = "";

        if (selectedItem.equals(OrderConstant.ORDER_DOCUMENTS)) {

            setTitle(OrderConstant.ORDER_DOCUMENTS);

            if (UserType.isUserPatient()) {
                showOrHideFab(false);
                emptyStateType = EmptyViewConstants.EMPTY_DOCUMENTS;
            } else {
                emptyStateType = EmptyViewConstants.EMPTY_DOCUMENTS_WITH_BTN;
            }

        } else if (selectedItem.equals(OrderConstant.ORDER_FORM)) {

            setTitle(OrderConstant.ORDER_FORM);
            if (UserType.isUserPatient()) {
                showOrHideFab(false);
                emptyStateType = EmptyViewConstants.EMPTY_FORMS;
            } else {
                emptyStateType = EmptyViewConstants.EMPTY_FORMS_WITH_BTN;
            }

        } else if (selectedItem.equals(OrderConstant.ORDER_LABS)) {

            setTitle(OrderConstant.ORDER_LABS);

            if (UserType.isUserPatient()) {
                showOrHideFab(false);
                emptyStateType = EmptyViewConstants.EMPTY_LABS;
            } else {
                emptyStateType = EmptyViewConstants.EMPTY_LABS_WITH_BTN;
            }

        } else if (selectedItem.equals(OrderConstant.ORDER_PRESCRIPTIONS)) {

            setTitle(OrderConstant.ORDER_PRESCRIPTIONS);

            if (UserType.isUserPatient()) {
                showOrHideFab(false);
                emptyStateType = EmptyViewConstants.EMPTY_PRESCRIPTION;
            } else {
                emptyStateType = EmptyViewConstants.EMPTY_PRESCRIPTION_WITH_BTN;
            }

        } else if (selectedItem.equals(OrderConstant.ORDER_RADIOLOGY)) {

            setTitle(OrderConstant.ORDER_RADIOLOGY);
            if (UserType.isUserPatient()) {
                showOrHideFab(false);
                emptyStateType = EmptyViewConstants.EMPTY_XRAY;
            } else {
                emptyStateType = EmptyViewConstants.EMPTY_XRAY_WITH_BTN;
            }

        } else if (selectedItem.equals(OrderConstant.ORDER_REFERRALS)) {

            setTitle(OrderConstant.ORDER_REFERRALS);

            if (UserType.isUserPatient()) {
                showOrHideFab(false);
                emptyStateType = EmptyViewConstants.EMPTY_SPECIALIST;
            } else {
                emptyStateType = EmptyViewConstants.EMPTY_SPECIALIST_WITH_BTN;
            }
        }

        orderDetailCelv.setEmptyState(emptyStateType);
    }

    private void setTitle(String title) {
        toolbarTitle.setText(title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
            case R.id.add_fab:
                addFab.setClickable(false);
                if (UserDetailPreferenceManager.getWhoAmIResponse().getUser_detail().getSignature() != null) {
                    if (getArguments() != null) {
                        startActivity(new Intent(getActivity(), CreateOrderActivity.class).putExtras(getArguments()));
                    }
                } else {
                    Bundle bundle = getArguments();
                    if (bundle == null) {
                        bundle = new Bundle();
                    }
                    bundle.putBoolean(ArgumentKeys.SHOW_SIGNATURE_PROPOSER, true);
                    startActivity(new Intent(getActivity(), SignatureActivity.class).putExtras(bundle));
                }
                break;
        }
    }
}
