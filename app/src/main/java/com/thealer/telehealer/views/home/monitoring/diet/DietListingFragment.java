package com.thealer.telehealer.views.home.monitoring.diet;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.thealer.telehealer.apilayer.models.commonResponseModel.UserBean;
import com.thealer.telehealer.apilayer.models.diet.DietApiResponseModel;
import com.thealer.telehealer.apilayer.models.diet.DietApiViewModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.visits.UpdateVisitRequestModel;
import com.thealer.telehealer.apilayer.models.visits.VisitsApiViewModel;
import com.thealer.telehealer.apilayer.models.vitalReport.VitalReportApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VisitConstants;
import com.thealer.telehealer.common.emptyState.EmptyStateUtil;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.CustomDialogs.ItemPickerDialog;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.common.PdfViewerFragment;
import com.thealer.telehealer.views.common.RecentsSelectionActivity;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.recents.VisitDetailConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Aswin on 15,April,2019
 */
public class DietListingFragment extends BaseFragment implements View.OnClickListener {
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private ImageView closeIv;
    private CustomRecyclerView dietListCrv;
    private MenuItem nextTv;

    private OnCloseActionInterface onCloseActionInterface;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private AttachObserverInterface attachObserverInterface;
    private DietApiViewModel dietApiViewModel;
    private ArrayList<DietApiResponseModel> dietApiResponseModelArrayList;
    private DietSelectionListAdapter dietSelectionListAdapter;
    private VisitsApiViewModel visitsApiViewModel;

    private Map<String, List<DietApiResponseModel>> dietApiResponseModelMap;
    private static int mode = Constants.VIEW_MODE;
    private List<Integer> actualList = new ArrayList<>();
    private ArrayList<Integer> selectedList = new ArrayList<>();
    private List<Integer> unSelectedList = new ArrayList<>();
    private String userGuid, doctorGuid;
    private UserBean userModel;
    private CommonUserApiResponseModel doctorModel;
    private String orderId, filter = null, startDate = null, endDate = null;
    private HashMap<Integer, DietApiResponseModel> selectedDietMap = new HashMap<>();
    private boolean isDisableCancel = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) context;
        showSubFragmentInterface = (ShowSubFragmentInterface) context;
        attachObserverInterface = (AttachObserverInterface) context;

        dietApiViewModel = ViewModelProviders.of(this).get(DietApiViewModel.class);
        visitsApiViewModel = ViewModelProviders.of(this).get(VisitsApiViewModel.class);

        attachObserverInterface.attachObserver(dietApiViewModel);
        attachObserverInterface.attachObserver(visitsApiViewModel);

        dietApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {
                    dietApiResponseModelArrayList = (ArrayList<DietApiResponseModel>) (Object) baseApiResponseModels;
                    if (dietApiResponseModelArrayList.isEmpty()) {
                        dietListCrv.showOrhideEmptyState(true);
                    } else {
                        dietListCrv.showOrhideEmptyState(false);
                        updateList();
                    }
                    enableOrDisablePrint();
                } else {
                    dietListCrv.showOrhideEmptyState(true);
                }

                dietListCrv.getSwipeLayout().setRefreshing(false);
            }
        });

        visitsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel.isSuccess()) {
                        changeToViewMode();
                        sendSuccessViewBroadCast(getActivity(), true, getString(R.string.success), String.format(getString(R.string.associate_order_success), VisitConstants.TYPE_DIET));
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
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diet_listing, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        closeIv = (ImageView) view.findViewById(R.id.close_iv);
        dietListCrv = (CustomRecyclerView) view.findViewById(R.id.diet_list_crv);

        setToolbarTitle(getString(R.string.last_week));

        toolbar.inflateMenu(R.menu.add_visit_menu);
        nextTv = toolbar.getMenu().findItem(R.id.menu_next);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_filter:
                        selectedList.clear();
                        selectedDietMap.clear();
                        Utils.showMonitoringFilter(getActivity(), new OnListItemSelectInterface() {
                            @Override
                            public void onListItemSelected(int position, Bundle bundle) {
                                String selectedItem = bundle.getString(Constants.SELECTED_ITEM);
                                startDate = null;
                                endDate = null;

                                if (selectedItem != null) {
                                    if (selectedItem.equals(getString(R.string.last_week))) {
                                        dietListCrv.setEmptyState(EmptyViewConstants.EMPTY_DIET_LAST_WEEK);
                                        filter = VitalReportApiViewModel.LAST_WEEK;
                                    } else if (selectedItem.equals(getString(R.string.all))) {
                                        dietListCrv.setEmptyState(EmptyViewConstants.EMPTY_DIET);
                                        filter = null;
                                    } else {
                                        filter = null;
                                        startDate = bundle.getString(ArgumentKeys.START_DATE);
                                        endDate = bundle.getString(ArgumentKeys.END_DATE);

                                        String title = EmptyStateUtil.getTitle(getActivity(), EmptyViewConstants.EMPTY_DIET_FROM_TO);

                                        dietListCrv.setEmptyStateTitle(String.format(title, Utils.getDayMonthYear(startDate), Utils.getDayMonthYear(endDate)));
                                    }
                                    setToolbarTitle(selectedItem);
                                }
                                getDiets(true);
                            }
                        });
                        break;
                    case R.id.menu_print:
                        showDietPrintOptions();
                        break;
                    case R.id.menu_next:
                        switch (mode) {
                            case Constants.EDIT_MODE:
                                if (nextTv.getTitle().equals(getString(R.string.cancel))) {
                                    changeToViewMode();
                                } else {
                                    if (orderId == null) {
                                        startActivityForResult(new Intent(getActivity(), RecentsSelectionActivity.class)
                                                .putExtra(ArgumentKeys.USER_GUID, userGuid)
                                                .putExtra(ArgumentKeys.DOCTOR_GUID, doctorGuid), RequestID.REQ_VISIT_RECENT);
                                    } else {
//                                        addVisit(orderId);

                                        if (getTargetFragment() != null)
                                            getTargetFragment().onActivityResult(RequestID.REQ_VISIT_UPDATE, Activity.RESULT_OK,
                                                    new Intent().putExtra(ArgumentKeys.UPDATE_TYPE, VisitDetailConstants.VISIT_TYPE_DIETS)
                                                            .putExtra(ArgumentKeys.SELECTED_DIET, selectedDietMap)
                                                            .putExtra(ArgumentKeys.SELECTED_LIST_ID, selectedList));
                                        onCloseActionInterface.onClose(false);
                                    }
                                }
                                break;
                            case Constants.VIEW_MODE:
                                mode = Constants.EDIT_MODE;
                                updateUI();
                                if (dietSelectionListAdapter != null) {
                                    dietSelectionListAdapter.setMode(mode);
                                }
                                break;
                        }
                        break;
                }
                return true;
            }
        });

        backIv.setOnClickListener(this);

        dietListCrv.setEmptyState(EmptyViewConstants.EMPTY_DIET_LAST_WEEK);
        dietListCrv.showOrhideEmptyState(false);
        dietListCrv.setErrorModel(this, dietApiViewModel.getErrorModelLiveData());

        dietSelectionListAdapter = new DietSelectionListAdapter(getActivity(), mode, new OnListItemSelectInterface() {
            @Override
            public void onListItemSelected(int position, Bundle bundle) {
                String date = bundle.getString(ArgumentKeys.SELECTED_DATE);
                switch (mode) {
                    case Constants.VIEW_MODE:
                        DietDetailFragment dietDetailFragment = new DietDetailFragment();

                        List<DietApiResponseModel> modelArrayList = dietApiResponseModelMap.get(date);
                        DietDetailModel dietDetailModel = new DietDetailModel(date, modelArrayList);

                        bundle = new Bundle();
                        bundle.putSerializable(ArgumentKeys.DIET_ITEM, dietDetailModel);

                        dietDetailFragment.setArguments(bundle);

                        showSubFragmentInterface.onShowFragment(dietDetailFragment);

                        break;
                    case Constants.EDIT_MODE:
                        List<DietApiResponseModel> responseModelList = dietApiResponseModelMap.get(date);

                        for (int i = 0; i < responseModelList.size(); i++) {

                            int id = responseModelList.get(i).getUser_diet_id();

                            if (selectedList.contains(id)) {
                                selectedList.remove((Object) id);
                            } else {
                                selectedList.add(id);
                            }
                            if (orderId != null) {
                                if (selectedDietMap.containsKey(id)) {
                                    selectedDietMap.remove((Object) id);
                                } else {
                                    selectedDietMap.put(id, responseModelList.get(i));
                                }
                            }
                        }
                        Log.e(TAG, "updateList: selected " + selectedList.toString());
                        enableOrDisableNext();
                        break;
                }
            }
        });

        dietListCrv.getRecyclerView().setAdapter(dietSelectionListAdapter);

        dietListCrv.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDiets(false);
            }
        });

        if (getArguments() != null) {
            isDisableCancel = getArguments().getBoolean(ArgumentKeys.DISABLE_CANCEL, false);
            userModel = (UserBean) getArguments().getSerializable(Constants.USER_DETAIL);
            if (userModel != null) {
                userGuid = userModel.getUser_guid();
            }

            doctorModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);

            orderId = getArguments().getString(ArgumentKeys.ORDER_ID);

            if (orderId != null) {
                mode = Constants.EDIT_MODE;
                dietSelectionListAdapter.setMode(mode);
            }

            filter = getArguments().getString(ArgumentKeys.SEARCH_TYPE);
        }

        updateUI();

        getDiets(true);
    }

    private void setToolbarTitle(String text) {
        if (text.equals(getString(R.string.all))) {
            toolbarTitle.setText(getString(R.string.diets));
        } else {
            toolbarTitle.setText(String.format(getString(R.string.diets) + " (%s)", text));
        }
    }

    private void changeToViewMode() {
        mode = Constants.VIEW_MODE;
        actualList.clear();
        selectedList.clear();
        unSelectedList.clear();
        selectedDietMap.clear();
        updateUI();
        dietSelectionListAdapter.setMode(mode);
    }

    private void updateUI() {
        switch (mode) {
            case Constants.VIEW_MODE:
                setNextTitle(getString(R.string.add_visit));
                break;
            case Constants.EDIT_MODE:
                setNextTitle(getString(R.string.cancel));
                enableOrDisableNext();
                break;
        }
        enableOrDisablePrint();
    }

    private void enableOrDisablePrint() {
        if (dietApiResponseModelArrayList == null || dietApiResponseModelArrayList.size() == 0 || mode == Constants.EDIT_MODE) {
            toolbar.getMenu().findItem(R.id.menu_print).setVisible(false);
        } else {
            toolbar.getMenu().findItem(R.id.menu_print).setVisible(true);
        }
    }

    private void enableOrDisableNext() {
        if (selectedList.isEmpty()) {
            setNextTitle(getString(R.string.cancel));
            if (isDisableCancel) {
                nextTv.setVisible(false);
            }
        } else {
            setNextTitle(getString(R.string.next));
            nextTv.setVisible(true);
        }
    }

    private void setNextTitle(String title) {
        nextTv.setTitle(title);
    }

    private void getDiets(boolean showProgress) {
        if (UserType.isUserAssistant()) {
            if (doctorModel != null) {
                doctorGuid = doctorModel.getUser_guid();
            }
        }
        dietApiViewModel.getUserDietDetails(filter, startDate, endDate, userGuid, doctorGuid, showProgress);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
        }
    }

    private void updateList() {

        actualList.clear();
        selectedList.clear();
        unSelectedList.clear();
        selectedDietMap.clear();

        dietApiResponseModelMap = new HashMap<>();

        for (int i = 0; i < dietApiResponseModelArrayList.size(); i++) {

            String date = Utils.getDayMonthYear(dietApiResponseModelArrayList.get(i).getDate());
            List<DietApiResponseModel> modelList = new ArrayList<>();

            if (dietApiResponseModelMap.containsKey(date)) {
                modelList.addAll(dietApiResponseModelMap.get(date));
            }

            modelList.add(dietApiResponseModelArrayList.get(i));

            dietApiResponseModelMap.put(date, modelList);

            if (dietApiResponseModelArrayList.get(i).getOrder_id() != null) {
                actualList.add(dietApiResponseModelArrayList.get(i).getUser_diet_id());
            }
        }

        Log.e(TAG, "updateList: actual " + actualList.toString());

        dietSelectionListAdapter.setData(dietApiResponseModelMap);
    }

    @Override
    public void onDestroy() {
        mode = Constants.VIEW_MODE;
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestID.REQ_VISIT_RECENT:
                if (resultCode == Activity.RESULT_OK) {
                    RecentsApiResponseModel.ResultBean resultBean = (RecentsApiResponseModel.ResultBean) data.getSerializableExtra(ArgumentKeys.SELECTED_RECENT_DETAIL);
                    addVisit(resultBean.getOrder_id());
                }
                break;
            case RequestID.REQ_SHOW_SUCCESS_VIEW:
                if (resultCode == Activity.RESULT_OK) {
                    if (orderId == null) {
                        getDiets(true);
                    } else {
                        if (getTargetFragment() != null)
                            getTargetFragment().onActivityResult(RequestID.REQ_VISIT_UPDATE, Activity.RESULT_OK, new Intent().putExtra(ArgumentKeys.UPDATE_TYPE, VisitDetailConstants.VISIT_TYPE_DIETS));
                        onCloseActionInterface.onClose(false);
                    }
                }
                break;
        }
    }

    private void addVisit(String order_id) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.loading));
        bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, String.format(getString(R.string.posting_your_visit_association_request), getString(R.string.diet)));

        showSuccessView(this, RequestID.REQ_SHOW_SUCCESS_VIEW, bundle);

        UpdateVisitRequestModel updateVisitRequestModel = new UpdateVisitRequestModel();
        updateVisitRequestModel.setAssociation_type(VisitConstants.TYPE_DIET);
        updateVisitRequestModel.setAdd_associations(selectedList);

        visitsApiViewModel.updateOrder(order_id, updateVisitRequestModel, doctorGuid, false);
    }

    private void showDietPrintOptions() {
        ItemPickerDialog itemPickerDialog = new ItemPickerDialog(getActivity(), getString(R.string.choose_time_period),
                DietConstant.getDietPrintOptions(getActivity()),
                new PickerListener() {
                    @Override
                    public void didSelectedItem(int position) {
                        generatePdf(position);
                    }

                    @Override
                    public void didCancelled() {

                    }
                });
        itemPickerDialog.setCancelable(false);
        itemPickerDialog.show();
    }

    private void generatePdf(int position) {
        Map<String, ArrayList<DietApiResponseModel>> listMap = new HashMap<>();

        for (DietApiResponseModel dietResponse : dietApiResponseModelArrayList) {
            String date = Utils.getFormatedDateTime(dietResponse.getDate(), Utils.yyyy_mm_dd);

            if (date != null) {
                ArrayList<DietApiResponseModel> modelArrayList = new ArrayList<>();
                if (!listMap.containsKey(date)) {
                    listMap.put(date, modelArrayList);
                }

                modelArrayList = listMap.get(date);

                if (modelArrayList == null) {
                    modelArrayList = new ArrayList<>();
                }
                modelArrayList.add(dietResponse);

                listMap.put(date, modelArrayList);
            }
        }


        Calendar calendar = Calendar.getInstance();

        Map<String, ArrayList<DietApiResponseModel>> pdfList = new HashMap<>();

        Set<String> dates = listMap.keySet();

        ArrayList<String> printOptions = DietConstant.getDietPrintOptions(getActivity());
        String selectedOption = printOptions.get(position);
        if (selectedOption.equals(getString(R.string.DIET_PRINT_ALL))) {
            for (String date : dates) {
                String utcdate = Utils.getUTCFormat(date, Utils.yyyy_mm_dd);
                pdfList.put(utcdate, listMap.get(date));
            }

        } else {
            if (selectedOption.equals(getString(R.string.PRINT_1_WEEK))) {
                calendar.add(Calendar.DAY_OF_MONTH, -7);

            } else if (selectedOption.equals(getString(R.string.PRINT_2_WEEK))) {
                calendar.add(Calendar.DAY_OF_MONTH, -14);

            } else if (selectedOption.equals(getString(R.string.PRINT_1_MONTH))) {
                calendar.add(Calendar.MONTH, -1);
            }

            for (String date : dates) {
                String utcdate = Utils.getUTCFormat(date, Utils.yyyy_mm_dd);
                Log.e(TAG, "generatePdf: " + date + "\t" + utcdate + "\t" + calendar.getTime());
                if (Utils.getDateFromString(utcdate).compareTo(calendar.getTime()) >= 0) {
                    pdfList.put(utcdate, listMap.get(date));
                }
            }
        }

        if (pdfList.size() > 0) {
            DietPdfGenerator dietPdfGenerator = new DietPdfGenerator(getActivity());
            String htmlContent = dietPdfGenerator.getPdfHtmlContent(pdfList, userModel);

            PdfViewerFragment pdfViewerFragment = new PdfViewerFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ArgumentKeys.HTML_FILE, htmlContent);
            bundle.putString(ArgumentKeys.PDF_TITLE, getString(R.string.diet_report));
            pdfViewerFragment.setArguments(bundle);
            showSubFragmentInterface.onShowFragment(pdfViewerFragment);

        } else {
            Utils.showAlertDialog(getActivity(), getString(R.string.alert), getString(R.string.no_data_available_for) + " " + selectedOption,
                    getString(R.string.ok), null,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, null);
        }

    }

}
