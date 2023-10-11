package com.thealer.telehealer.views.home.orders;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideoApiResponseModel;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideoResponse;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersPrescriptionApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersSpecialistApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.forms.OrdersUserFormsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.OrdersLabApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.miscellaneous.MiscellaneousApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.GetRadiologyResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomExpandableListView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.GetUserDetails;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Util.TimerInterface;
import com.thealer.telehealer.common.Util.TimerRunnable;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ContentActivity;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OverlayViewConstants;
import com.thealer.telehealer.views.common.SearchCellView;
import com.thealer.telehealer.views.common.SearchInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.settings.SignatureActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.toptas.fancyshowcase.listener.DismissListener;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

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
    private CommonUserApiResponseModel commonUserApiResponseModel, doctorModel;
    private boolean isFromHome;
    private int page = 1;
    private String selectedItem;
    private List<String> headerList = new ArrayList<>();
    private HashMap<String, List<OrdersDetailListAdapterModel>> childList = new HashMap<>();
    private boolean isVisibleToUser;
    private OrdersDetailListAdapter ordersDetailListAdapter;
    private ExpandableListView expandableListView;
    private OnCloseActionInterface onCloseActionInterface;
    private Set<String> userGuidSet = new HashSet<>();
    private HashMap<String, CommonUserApiResponseModel> userDetailHashMap = new HashMap<>();
    private String userGuid, doctorGuid;
    @Nullable
    private SearchCellView searchView;
    private  Boolean isPermissionAllowed = true;
    private Toolbar toolbar;

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
        ordersApiViewModel = new ViewModelProvider(this).get(OrdersApiViewModel.class);
        attachObserverInterface.attachObserver(ordersApiViewModel);

        ordersApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                orderDetailCelv.getSwipeLayout().setRefreshing(false);
                if (baseApiResponseModel != null) {
                    updateList(baseApiResponseModel);
                }
            }
        });

        ordersApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                orderDetailCelv.getSwipeLayout().setRefreshing(false);
                if (baseApiResponseModels != null && baseApiResponseModels.size() > 0) {
                    updateArrayList(baseApiResponseModels);
                } else {
                    orderDetailCelv.showEmptyState();
                }
            }
        });
    }

    private void updateArrayList(ArrayList<BaseApiResponseModel> baseApiResponseModels) {
        headerList.clear();
        childList.clear();
        userGuidSet.clear();

        ArrayList<OrdersUserFormsApiResponseModel> formsApiResponseModels = (ArrayList<OrdersUserFormsApiResponseModel>) (Object) baseApiResponseModels;

//        setTitle(Utils.getPaginatedTitle(OrderConstant.getDislpayTitle(getContext(), selectedItem), formsApiResponseModels.size()));

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

            if (formsApiResponseModel.getDoctor() != null && formsApiResponseModel.getDoctor().getUser_guid() != null) {
                userGuidSet.add(formsApiResponseModel.getDoctor().getUser_guid());
            }
            if (formsApiResponseModel.getPatient() != null && formsApiResponseModel.getPatient().getUser_guid() != null) {
                userGuidSet.add(formsApiResponseModel.getPatient().getUser_guid());
            }
        }
        ordersDetailListAdapter.setData(headerList, childList);

        if (headerList.size() > 0) {
            orderDetailCelv.hideEmptyState();
            expandListView();
        } else {
            orderDetailCelv.showEmptyState();
        }

        getUserDetails(userGuidSet);

    }

    private void updateList(BaseApiResponseModel baseApiResponseModel) {

        headerList.clear();
        childList.clear();
        userGuidSet.clear();

        DismissListener dismissListener = new DismissListener() {
            @Override
            public void onDismiss(@org.jetbrains.annotations.Nullable String s) {
                orderDetailCelv.showOrHideMessage(true);
            }

            @Override
            public void onSkipped(@org.jetbrains.annotations.Nullable String s) {

            }
        };

        if (page == 1) {
            OrdersApiResponseModel ordersBaseApiResponseModel = (OrdersApiResponseModel) baseApiResponseModel;
//            setTitle(Utils.getPaginatedTitle(OrderConstant.getDislpayTitle(getContext(), selectedItem), ordersBaseApiResponseModel.getCount()));
        }
        if (baseApiResponseModel instanceof OrdersLabApiResponseModel) {

            OrdersLabApiResponseModel labApiResponseModel = (OrdersLabApiResponseModel) baseApiResponseModel;

            if (labApiResponseModel.getCount() > 0) {
                orderDetailCelv.showOrhideEmptyState(false);
            } else {
                orderDetailCelv.showOrhideEmptyState(true);
            }
            if (!UserType.isUserPatient() && page == 1 && labApiResponseModel.getLabsResponseBeanList().size() == 0) {
                if (!appPreference.getBoolean(PreferenceConstants.IS_OVERLAY_ADD_LAB)) {
                    appPreference.setBoolean(PreferenceConstants.IS_OVERLAY_ADD_LAB, true);
                    orderDetailCelv.showOrHideMessage(false);
                    Utils.showOverlay(getActivity(), addFab, OverlayViewConstants.OVERLAY_NO_LAB_RECORD, dismissListener);
                }
            }

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
            if (prescriptionApiResponseModel.getCount() > 0) {
                orderDetailCelv.showOrhideEmptyState(false);
            } else {
                orderDetailCelv.showOrhideEmptyState(true);
            }
            if (!UserType.isUserPatient() && page == 1 && prescriptionApiResponseModel.getOrdersResultBeanList().size() == 0) {
                if (!appPreference.getBoolean(PreferenceConstants.IS_OVERLAY_ADD_PRESCRIPTION)) {
                    appPreference.setBoolean(PreferenceConstants.IS_OVERLAY_ADD_PRESCRIPTION, true);
                    orderDetailCelv.showOrHideMessage(false);
                    Utils.showOverlay(getActivity(), addFab, OverlayViewConstants.OVERLAY_NO_PRESCRIPTION, dismissListener);
                }
            }

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
            if (ordersSpecialistApiResponseModel.getCount() > 0) {
                orderDetailCelv.showOrhideEmptyState(false);
            } else {
                orderDetailCelv.showOrhideEmptyState(true);
            }
            if (!UserType.isUserPatient() && page == 1 && ordersSpecialistApiResponseModel.getResult().size() == 0) {
                if (!appPreference.getBoolean(PreferenceConstants.IS_OVERLAY_ADD_SPECIALIST)) {
                    appPreference.setBoolean(PreferenceConstants.IS_OVERLAY_ADD_SPECIALIST, true);
                    orderDetailCelv.showOrHideMessage(false);
                    Utils.showOverlay(getActivity(), addFab, OverlayViewConstants.OVERLAY_NO_SPECIALIST, dismissListener);
                }
            }

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
            if (getRadiologyResponseModel.getCount() > 0) {
                orderDetailCelv.showOrhideEmptyState(false);
            } else {
                orderDetailCelv.showOrhideEmptyState(true);
            }
            if (!UserType.isUserPatient() && page == 1 && getRadiologyResponseModel.getResultBeanList().size() == 0) {
                if (!appPreference.getBoolean(PreferenceConstants.IS_OVERLAY_ADD_RADIOLOGY)) {
                    appPreference.setBoolean(PreferenceConstants.IS_OVERLAY_ADD_RADIOLOGY, true);
                    orderDetailCelv.showOrHideMessage(false);
                    Utils.showOverlay(getActivity(), addFab, OverlayViewConstants.OVERLAY_NO_RADIOLOGY, dismissListener);
                }
            }
            int count = 0;
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

                if (title != null){
                    count++;
                    ordersDetailListAdapterModel.setSubTitle(title == null ? "" : title.toString());
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
            setTitle(Utils.getPaginatedTitle(OrderConstant.getDislpayTitle(getContext(), selectedItem), count));
        } else if (baseApiResponseModel instanceof MiscellaneousApiResponseModel) {
            MiscellaneousApiResponseModel miscellaneousApiResponseModel = (MiscellaneousApiResponseModel) baseApiResponseModel;
            if (miscellaneousApiResponseModel.getCount() > 0) {
                orderDetailCelv.showOrhideEmptyState(false);
            } else {
                orderDetailCelv.showOrhideEmptyState(true);
            }
            if (!UserType.isUserPatient() && page == 1 && miscellaneousApiResponseModel.getResult().size() == 0) {
                if (!appPreference.getBoolean(PreferenceConstants.IS_OVERLAY_ADD_MISC)) {
                    appPreference.setBoolean(PreferenceConstants.IS_OVERLAY_ADD_MISC, true);
                    orderDetailCelv.showOrHideMessage(false);
                    Utils.showOverlay(getActivity(), addFab, OverlayViewConstants.OVERLAY_NO_RADIOLOGY, dismissListener);
                }
            }

            for (int i = 0; i < miscellaneousApiResponseModel.getResult().size(); i++) {

                String date = Utils.getDayMonthYear(miscellaneousApiResponseModel.getResult().get(i).getCreated_at());
                OrdersDetailListAdapterModel ordersDetailListAdapterModel = new OrdersDetailListAdapterModel();

                ordersDetailListAdapterModel.setSubTitle(miscellaneousApiResponseModel.getResult().get(i).getDetail().getNotes());
                ordersDetailListAdapterModel.setCommonResultResponseModel(miscellaneousApiResponseModel.getResult().get(i));

                if (!headerList.contains(date)) {
                    headerList.add(date);
                }

                List<OrdersDetailListAdapterModel> childListData = new ArrayList<>();

                if (childList.containsKey(date)) {
                    childListData.addAll(childList.get(date));
                }

                childListData.add(ordersDetailListAdapterModel);

                childList.put(date, childListData);

                if (miscellaneousApiResponseModel.getResult().get(i).getDoctor() != null &&
                        miscellaneousApiResponseModel.getResult().get(i).getDoctor().getUser_guid() != null) {
                    addToUserGuidList(miscellaneousApiResponseModel.getResult().get(i).getDoctor().getUser_guid());
                }
                if (miscellaneousApiResponseModel.getResult().get(i).getPatient() != null &&
                        miscellaneousApiResponseModel.getResult().get(i).getPatient().getUser_guid() != null) {
                    addToUserGuidList(miscellaneousApiResponseModel.getResult().get(i).getPatient().getUser_guid());
                }
            }
        } else if (baseApiResponseModel instanceof EducationalVideoApiResponseModel) {
            EducationalVideoApiResponseModel educationalVideoResponse = (EducationalVideoApiResponseModel) baseApiResponseModel;
                if (educationalVideoResponse.getCount() > 0) {
                    orderDetailCelv.showOrhideEmptyState(false);
                } else {
                    orderDetailCelv.showOrhideEmptyState(true);
                }
                if (!UserType.isUserPatient() && page == 1 && educationalVideoResponse.getResult().size() == 0) {
                    if (!appPreference.getBoolean(PreferenceConstants.IS_OVERLAY_EDUCATIONAL_VIDEO)) {
                        appPreference.setBoolean(PreferenceConstants.IS_OVERLAY_EDUCATIONAL_VIDEO, true);
                        orderDetailCelv.showOrHideMessage(false);
                        Utils.showOverlay(getActivity(), addFab, OverlayViewConstants.OVERLAY_NO_EDUCATIONAL_VIDEO, dismissListener);
                    }
                }

                for (int i = 0; i < educationalVideoResponse.getResult().size(); i++) {

                    String date = Utils.getDayMonthYear(educationalVideoResponse.getResult().get(i).getCreated_at());
                    OrdersDetailListAdapterModel ordersDetailListAdapterModel = new OrdersDetailListAdapterModel();

                    if (educationalVideoResponse.getResult().get(i).getVideo() != null) {
                        ordersDetailListAdapterModel.setSubTitle(educationalVideoResponse.getResult().get(i).getVideo().getTitle());
                    } else {
                        ordersDetailListAdapterModel.setSubTitle("");
                    }

                    ordersDetailListAdapterModel.setCommonResultResponseModel(educationalVideoResponse.getResult().get(i));

                    ordersDetailListAdapterModel.setOtherImageUrl(educationalVideoResponse.getResult().get(i).getVideo().getUrl());

                    if (!headerList.contains(date)) {
                        headerList.add(date);
                    }

                    List<OrdersDetailListAdapterModel> childListData = new ArrayList<>();

                    if (childList.containsKey(date)) {
                        childListData.addAll(childList.get(date));
                    }

                    childListData.add(ordersDetailListAdapterModel);

                    childList.put(date, childListData);

                    if (educationalVideoResponse.getResult().get(i).getDoctor() != null &&
                            educationalVideoResponse.getResult().get(i).getDoctor().getUser_guid() != null) {
                        addToUserGuidList(educationalVideoResponse.getResult().get(i).getDoctor().getUser_guid());
                    }
                    if (educationalVideoResponse.getResult().get(i).getPatient() != null &&
                            educationalVideoResponse.getResult().get(i).getPatient().getUser_guid() != null) {
                        addToUserGuidList(educationalVideoResponse.getResult().get(i).getPatient().getUser_guid());
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

        getUserDetails(userGuidSet);

    }

    private void addToUserGuidList(String user_guid) {
        if (!userDetailHashMap.containsKey(user_guid)) {
            userGuidSet.add(user_guid);
        }
    }

    private void getUserDetails(Set<String> userGuidSet) {
        GetUserDetails
                .getInstance(getActivity())
                .getDetails(userGuidSet)
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
        searchView = view.findViewById(R.id.search_view);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        orderDetailCelv = (CustomExpandableListView) view.findViewById(R.id.order_detail_celv);
        addFab = (FloatingActionButton) view.findViewById(R.id.add_fab);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        backIv.setOnClickListener(this);
        addFab.setOnClickListener(this);
        if (getArguments() != null) {
            selectedItem = getArguments().getString(Constants.SELECTED_ITEM);

            isFromHome = getArguments().getBoolean(Constants.IS_FROM_HOME);
            isPermissionAllowed = getArguments().getBoolean(ArgumentKeys.isPermissionAllowed,true);
            if (!isFromHome) {
                commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
                if (commonUserApiResponseModel != null) {
                    userGuid = commonUserApiResponseModel.getUser_guid();
                }

                doctorModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);
                if (doctorModel != null)
                    doctorGuid = doctorModel.getUser_guid();

            }
            searchView.setSearchInterface(new SearchInterface() {
                @Override
                public void doSearch() {
                    page = 1;
                    if (selectedItem.equals(OrderConstant.ORDER_PRESCRIPTIONS)) {
                        ordersApiViewModel.getPrescriptionOrders(searchView.getCurrentSearchResult(),page,true);
                    } else if (selectedItem.equals(OrderConstant.ORDER_REFERRALS)) {
                        ordersApiViewModel.getSpecialist(searchView.getCurrentSearchResult(), page, true);
                    }  else if (selectedItem.equals(OrderConstant.ORDER_LABS)) {
                        ordersApiViewModel.getLabOrders(searchView.getCurrentSearchResult(), page, true);
                    } else if (selectedItem.equals(OrderConstant.ORDER_FORM)) {
                        ordersApiViewModel.getForms(searchView.getCurrentSearchResult(),true);
                    } else if (selectedItem.equals(OrderConstant.ORDER_RADIOLOGY)) {
                        ordersApiViewModel.getRadiologyList(searchView.getCurrentSearchResult(), page, true);
                    } else if (selectedItem.equals(OrderConstant.ORDER_MISC)) {
                        ordersApiViewModel.getMiscellaneousList(searchView.getCurrentSearchResult(), page, true);
                    } else if (selectedItem.equals(OrderConstant.ORDER_EDUCATIONAL_VIDEO)) {
                        ordersApiViewModel.getEducationalVideoList(searchView.getCurrentSearchResult(), page, true);
                    }
                }
            });

            if (selectedItem != null) {

                setEmptyState();

                ordersDetailListAdapter = new OrdersDetailListAdapter(getActivity(), headerList, childList, userDetailHashMap, doctorGuid);

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

        orderDetailCelv.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                makeApiCall(false);
            }
        });

        orderDetailCelv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeApiCall(true);
            }
        });
        orderDetailCelv.setErrorModel(this, ordersApiViewModel.getErrorModelLiveData());
    }

    @Override
    public void onResume() {
        super.onResume();
        addFab.setClickable(true);
        addFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(),
                isPermissionAllowed ? R.color.app_gradient_start : R.color.colorGrey)));
        makeApiCall(true);
    }

    private void makeApiCall(boolean isShowProgress) {
        orderDetailCelv.hideEmptyState();
        if (selectedItem.equals(OrderConstant.ORDER_DOCUMENTS)) {

        } else if (selectedItem.equals(OrderConstant.ORDER_FORM)) {
            searchView.setSearchHint(getString(R.string.search_form));
            if (isFromHome) {
                ordersApiViewModel.getForms(searchView.getCurrentSearchResult(),isShowProgress);
            } else {
                ordersApiViewModel.getUserForms(userGuid, doctorGuid, isShowProgress, !UserType.isUserPatient());
            }
        } else if (selectedItem.equals(OrderConstant.ORDER_LABS)) {
            searchView.setSearchHint(getString(R.string.search_lab));
            if (isFromHome) {
                ordersApiViewModel.getLabOrders(searchView.getCurrentSearchResult(), page, isShowProgress);
            } else {
                ordersApiViewModel.getUserLabOrders(userGuid, doctorGuid, page, isShowProgress);
            }

        } else if (selectedItem.equals(OrderConstant.ORDER_PRESCRIPTIONS)) {
            searchView.setSearchHint(getString(R.string.search_prescription));
            if (isFromHome) {
                ordersApiViewModel.getPrescriptionOrders(searchView.getCurrentSearchResult(), page, isShowProgress);
             } else {
                ordersApiViewModel.getUserPrescriptionOrders(userGuid, doctorGuid, page, isShowProgress);
            }

        } else if (selectedItem.equals(OrderConstant.ORDER_RADIOLOGY)) {
            searchView.setSearchHint(getString(R.string.search_radiology));
            if (isFromHome) {
                ordersApiViewModel.getRadiologyList(searchView.getCurrentSearchResult(), page, isShowProgress);
            } else {
                ordersApiViewModel.getUserRadiologyList(userGuid, doctorGuid, page, isShowProgress);
            }

        } else if (selectedItem.equals(OrderConstant.ORDER_REFERRALS)) {
            searchView.setSearchHint(getString(R.string.search_referral));
            if (isFromHome) {
                ordersApiViewModel.getSpecialist(searchView.getCurrentSearchResult(), page, isShowProgress);
            } else {
                ordersApiViewModel.getUserSpecialist(userGuid, doctorGuid, page, isShowProgress);
            }

        } else if (selectedItem.equals(OrderConstant.ORDER_MISC)) {
            searchView.setSearchHint(getString(R.string.search_misc));
            if (isFromHome) {
                ordersApiViewModel.getMiscellaneousList(searchView.getCurrentSearchResult(), page, isShowProgress);
            } else {
                ordersApiViewModel.getUserMiscellaneousList(userGuid, doctorGuid, page, isShowProgress);
            }

        } else if (selectedItem.equals(OrderConstant.ORDER_EDUCATIONAL_VIDEO)) {
            searchView.setSearchHint(getString(R.string.search_educational_video));
            if (isFromHome) {
                ordersApiViewModel.getEducationalVideoList(searchView.getCurrentSearchResult(), page, isShowProgress);
            } else {
                ordersApiViewModel.getUserEducationalVideoList(userGuid, doctorGuid, page, isShowProgress);
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

            if (UserType.isUserPatient()) {
                showOrHideFab(false);
                emptyStateType = EmptyViewConstants.EMPTY_DOCUMENTS;
            } else {
                emptyStateType = EmptyViewConstants.EMPTY_DOCUMENTS_WITH_BTN;
            }

        } else if (selectedItem.equals(OrderConstant.ORDER_FORM)) {

            if (UserType.isUserPatient()) {
                showOrHideFab(false);
                emptyStateType = EmptyViewConstants.EMPTY_FORMS;
            } else {
                emptyStateType = EmptyViewConstants.EMPTY_FORMS_WITH_BTN;
            }

        } else if (selectedItem.equals(OrderConstant.ORDER_LABS)) {

            if (UserType.isUserPatient()) {
                showOrHideFab(false);
                emptyStateType = EmptyViewConstants.EMPTY_LABS;
            } else {
                emptyStateType = EmptyViewConstants.EMPTY_LABS_WITH_BTN;
            }

        } else if (selectedItem.equals(OrderConstant.ORDER_PRESCRIPTIONS)) {

            if (UserType.isUserPatient()) {
                showOrHideFab(false);
                emptyStateType = EmptyViewConstants.EMPTY_PRESCRIPTION;
            } else {
                emptyStateType = EmptyViewConstants.EMPTY_PRESCRIPTION_WITH_BTN;
            }

        } else if (selectedItem.equals(OrderConstant.ORDER_RADIOLOGY)) {

            if (UserType.isUserPatient()) {
                showOrHideFab(false);
                emptyStateType = EmptyViewConstants.EMPTY_XRAY;
            } else {
                emptyStateType = EmptyViewConstants.EMPTY_XRAY_WITH_BTN;
            }

        } else if (selectedItem.equals(OrderConstant.ORDER_REFERRALS)) {

            if (UserType.isUserPatient()) {
                showOrHideFab(false);
                emptyStateType = EmptyViewConstants.EMPTY_SPECIALIST;
            } else {
                emptyStateType = EmptyViewConstants.EMPTY_SPECIALIST_WITH_BTN;
            }
        } else if (selectedItem.equals(OrderConstant.ORDER_MISC)) {

            if (UserType.isUserPatient()) {
                showOrHideFab(false);
                emptyStateType = EmptyViewConstants.EMPTY_MISC;
            } else {
                emptyStateType = EmptyViewConstants.EMPTY_MISC_WITH_BTN;
            }
        } else if (selectedItem.equals(OrderConstant.ORDER_EDUCATIONAL_VIDEO)) {

            if (UserType.isUserPatient()) {
                showOrHideFab(false);
                emptyStateType = EmptyViewConstants.EMPTY_EDUCATIONAL_VIDEO_ORDER;
            } else {
                emptyStateType = EmptyViewConstants.EMPTY_EDUCATIONAL_VIDEO_ORDER_WITH_BTN;
            }
        }

        setTitle(OrderConstant.getDislpayTitle(getContext(), selectedItem));
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
                if(!isPermissionAllowed){
                    Utils.displayPermissionMsg(getActivity());
                    return;
                }
                addFab.setClickable(false);
                Bundle bundle = getArguments();
                if (UserType.isUserDoctor() && (UserDetailPreferenceManager.getSignature() == null || UserDetailPreferenceManager.getSignature().isEmpty())) {
                    if (bundle == null) {
                        bundle = new Bundle();
                    }
                    bundle.putBoolean(ArgumentKeys.SHOW_SIGNATURE_PROPOSER, true);
                    startActivity(new Intent(getActivity(), SignatureActivity.class).putExtras(bundle));
                } else if (UserType.isUserAssistant() && doctorModel != null && doctorModel.getUser_detail() != null && (doctorModel.getUser_detail().getSignature() == null || doctorModel.getUser_detail().getSignature().isEmpty())) {

                    startActivity(new Intent(getActivity(), ContentActivity.class)
                            .putExtra(ArgumentKeys.TITLE, R.string.ma_signature_required_title)
                            .putExtra(ArgumentKeys.DESCRIPTION, String.format(getString(R.string.ma_signature_required_message), doctorModel.getDisplayName(), doctorModel.getDisplayName()))
                            .putExtra(ArgumentKeys.RESOURCE_ICON, R.drawable.app_icon)
                            .putExtra(ArgumentKeys.IS_BUTTON_NEEDED, true)
                            .putExtra(ArgumentKeys.OK_BUTTON_TITLE, getString(R.string.ok)));

                } else {
                    startActivity(new Intent(getActivity(), CreateOrderActivity.class).putExtras(bundle));
                }
                break;
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.hideOverlay();
    }

}
