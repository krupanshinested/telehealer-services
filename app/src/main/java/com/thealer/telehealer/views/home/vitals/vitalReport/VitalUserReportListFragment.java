package com.thealer.telehealer.views.home.vitals.vitalReport;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
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
import com.thealer.telehealer.apilayer.models.recents.RecentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.visits.UpdateVisitRequestModel;
import com.thealer.telehealer.apilayer.models.visits.VisitsApiViewModel;
import com.thealer.telehealer.apilayer.models.vitalReport.VitalReportApiViewModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomExpandableListView;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VisitConstants;
import com.thealer.telehealer.common.emptyState.EmptyStateUtil;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.common.PdfViewerFragment;
import com.thealer.telehealer.views.common.RecentsSelectionActivity;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.recents.VisitDetailConstants;
import com.thealer.telehealer.views.home.vitals.VitalPdfGenerator;
import com.thealer.telehealer.views.home.vitals.VitalsDetailListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aswin on 04,February,2019
 */
public class VitalUserReportListFragment extends BaseFragment {
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private CustomExpandableListView vitalsListCelv;

    private OnCloseActionInterface onCloseActionInterface;
    private AttachObserverInterface attachObserverInterface;
    private VitalsApiViewModel vitalsApiViewModel;

    private ArrayList<VitalsApiResponseModel> vitalsApiResponseModel;
    private List<String> headerList = new ArrayList<>();
    private HashMap<String, List<VitalsApiResponseModel>> childList = new HashMap<>();
    private VitalsDetailListAdapter vitalsDetailListAdapter;
    private CommonUserApiResponseModel commonUserApiResponseModel;
    private String filter = VitalReportApiViewModel.LAST_WEEK, startDate = null, endDate = null;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private MenuItem nextMenu;
    private int mode = Constants.VIEW_MODE;
    private List<Integer> actualList = new ArrayList<>();
    private ArrayList<Integer> selectedList = new ArrayList<>();
    private List<Integer> unSelectedList = new ArrayList<>();
    private String userGuid;
    private VisitsApiViewModel visitsApiViewModel;
    private String doctorGuid, orderId;
    private HashMap<Integer, VitalsApiResponseModel> vitalsApiResponseModelMap = new HashMap<>();
    private boolean isDisableCancel = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();

        vitalsApiViewModel = ViewModelProviders.of(this).get(VitalsApiViewModel.class);
        visitsApiViewModel = ViewModelProviders.of(this).get(VisitsApiViewModel.class);

        attachObserverInterface.attachObserver(vitalsApiViewModel);
        attachObserverInterface.attachObserver(visitsApiViewModel);

        vitalsApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {
                    vitalsApiResponseModel = (ArrayList<VitalsApiResponseModel>) (Object) baseApiResponseModels;
                    updateList(vitalsApiResponseModel);
                    enableOrDisablePrint();
                }
            }
        });

        visitsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel.isSuccess()) {
                        changeToViewMode();
                        sendSuccessViewBroadCast(getActivity(), true, getString(R.string.success), String.format(getString(R.string.associate_order_success), VisitConstants.TYPE_VITALS));
                    }
                }
            }
        });

        visitsApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    sendSuccessViewBroadCast(getActivity(), false, getString(R.string.failure), String.format(getString(R.string.associate_order_failure), VisitConstants.TYPE_VITALS));
                }
            }
        });
    }

    private void updateList(ArrayList<VitalsApiResponseModel> vitalsApiResponseModelArrayList) {
        headerList.clear();
        childList.clear();

        for (int i = 0; i < vitalsApiResponseModelArrayList.size(); i++) {

            String date = Utils.getDayMonthYear(vitalsApiResponseModelArrayList.get(i).getCreated_at());

            if (!headerList.contains(date)) {
                headerList.add(date);
            }
            List<VitalsApiResponseModel> vitalsApiResponseModelList = new ArrayList<>();

            if (childList.get(date) != null) {
                vitalsApiResponseModelList.addAll(childList.get(date));
            }

            vitalsApiResponseModelList.add(vitalsApiResponseModelArrayList.get(i));

            childList.put(date, vitalsApiResponseModelList);

        }

        vitalsDetailListAdapter.setData(headerList, childList);

        if (headerList.size() > 0) {
            vitalsListCelv.hideEmptyState();
            expandListView();
        } else {
            vitalsListCelv.showEmptyState();
        }
    }

    private void expandListView() {
        for (int i = 0; i < headerList.size(); i++) {
            vitalsListCelv.getExpandableView().expandGroup(i);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vital_user_report, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        vitalsListCelv = (CustomExpandableListView) view.findViewById(R.id.vitals_list_cerv);

        toolbar.inflateMenu(R.menu.add_visit_menu);

        nextMenu = toolbar.getMenu().findItem(R.id.menu_next);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_filter:
                        selectedList.clear();
                        vitalsApiResponseModelMap.clear();
                        Utils.showMonitoringFilter(getActivity(), new OnListItemSelectInterface() {
                            @Override
                            public void onListItemSelected(int position, Bundle bundle) {
                                String selectedItem = bundle.getString(Constants.SELECTED_ITEM);
                                startDate = null;
                                endDate = null;

                                if (selectedItem != null) {
                                    if (selectedItem.equals(getString(R.string.last_week))) {
                                        vitalsListCelv.setEmptyState(EmptyViewConstants.EMPTY_DOCTOR_VITAL_LAST_WEEK);
                                        filter = VitalReportApiViewModel.LAST_WEEK;
                                    } else if (selectedItem.equals(getString(R.string.all))) {
                                        vitalsListCelv.setEmptyState(EmptyViewConstants.EMPTY_DOCTOR_VITAL_SEARCH);
                                        filter = VitalReportApiViewModel.ALL;
                                    }

                                } else {
                                    filter = null;
                                    startDate = bundle.getString(ArgumentKeys.START_DATE);
                                    endDate = bundle.getString(ArgumentKeys.END_DATE);

                                    String title = EmptyStateUtil.getTitle(getActivity(), EmptyViewConstants.EMPTY_VITAL_FROM_TO);

                                    vitalsListCelv.setEmptyStateTitle(String.format(title, Utils.getDayMonthYear(startDate), Utils.getDayMonthYear(endDate)));
                                }
                                getUserVitals();
                            }
                        });
                        break;
                    case R.id.menu_print:
                        generatePrintList();
                        break;
                    case R.id.menu_next:
                        switch (mode) {
                            case Constants.EDIT_MODE:
                                if (nextMenu.getTitle().equals(getString(R.string.cancel))) {
                                    changeToViewMode();
                                } else {
                                    if (orderId == null) {
                                        startActivityForResult(new Intent(getActivity(), RecentsSelectionActivity.class)
                                                .putExtra(ArgumentKeys.USER_GUID, userGuid)
                                                .putExtra(ArgumentKeys.DOCTOR_GUID, doctorGuid), RequestID.REQ_VISIT_RECENT);
                                    } else {
                                        if (getTargetFragment() != null)
                                            getTargetFragment().onActivityResult(RequestID.REQ_VISIT_UPDATE, Activity.RESULT_OK,
                                                    new Intent()
                                                            .putExtra(ArgumentKeys.UPDATE_TYPE, VisitDetailConstants.VISIT_TYPE_VITALS)
                                                            .putExtra(ArgumentKeys.SELECTED_VITALS, vitalsApiResponseModelMap)
                                                            .putExtra(ArgumentKeys.SELECTED_LIST_ID, selectedList));
                                        onCloseActionInterface.onClose(false);
                                    }
                                }
                                break;
                            case Constants.VIEW_MODE:
                                mode = Constants.EDIT_MODE;
                                updateUI();
                                if (vitalsDetailListAdapter != null) {
                                    vitalsDetailListAdapter.setMode(mode);
                                }
                                break;
                        }

                        break;
                }
                return true;
            }
        });

        if (getArguments() != null) {
            isDisableCancel = getArguments().getBoolean(ArgumentKeys.DISABLE_CANCEL, false);
            commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            filter = getArguments().getString(ArgumentKeys.SEARCH_TYPE);
            doctorGuid = getArguments().getString(ArgumentKeys.DOCTOR_GUID);
            orderId = getArguments().getString(ArgumentKeys.ORDER_ID);

            if (orderId != null) {
                mode = Constants.EDIT_MODE;
            }
            if (commonUserApiResponseModel != null) {
                toolbarTitle.setText(commonUserApiResponseModel.getUserDisplay_name());
            }

            if (getArguments().getBoolean(ArgumentKeys.IS_SHOW_FILTER)) {
                toolbar.getMenu().findItem(R.id.menu_filter).setVisible(true);
            }
        }

        enableOrDisablePrint();

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseActionInterface.onClose(false);
            }
        });

        vitalsListCelv.setEmptyState(EmptyViewConstants.EMPTY_DOCTOR_VITAL_LAST_WEEK);

        vitalsDetailListAdapter = new VitalsDetailListAdapter(getActivity(), headerList, childList, true, mode, new OnListItemSelectInterface() {
            @Override
            public void onListItemSelected(int position, Bundle bundle) {
                int groupPosition = bundle.getInt(ArgumentKeys.GROUP_POSITION);
                int childPosition = bundle.getInt(ArgumentKeys.CHILD_POSITION);

                VitalsApiResponseModel apiResponseModel = childList.get(headerList.get(groupPosition)).get(childPosition);
                int vitalId = apiResponseModel.getUser_vital_id();

                if (selectedList.contains(vitalId)) {
                    selectedList.remove((Object) vitalId);
                } else {
                    selectedList.add(vitalId);
                }

                if (orderId != null) {
                    if (vitalsApiResponseModelMap.containsKey(vitalId)) {
                        vitalsApiResponseModelMap.remove((Object) vitalId);
                    } else {
                        vitalsApiResponseModelMap.put(vitalId, apiResponseModel);
                    }
                }

                enableOrDisableNext();
            }
        });

        vitalsListCelv.getExpandableView().setAdapter(vitalsDetailListAdapter);

        vitalsListCelv.setErrorModel(this, vitalsApiViewModel.getErrorModelLiveData());

        vitalsListCelv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserVitals();
            }
        });

        vitalsListCelv.getSwipeLayout().setEnabled(false);

        if (getArguments() != null) {
            commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            filter = getArguments().getString(ArgumentKeys.SEARCH_TYPE);

            if (commonUserApiResponseModel != null) {
                userGuid = commonUserApiResponseModel.getUser_guid();
                toolbarTitle.setText(commonUserApiResponseModel.getUserDisplay_name());
            }
        }

        updateUI();

        getUserVitals();

    }

    private void generatePrintList() {
        VitalPdfGenerator vitalPdfGenerator = new VitalPdfGenerator(getActivity());
        String htmlContent = vitalPdfGenerator.generatePdfFor(vitalsApiResponseModel, commonUserApiResponseModel, true);

        PdfViewerFragment pdfViewerFragment = new PdfViewerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ArgumentKeys.HTML_FILE, htmlContent);
        bundle.putString(ArgumentKeys.PDF_TITLE, getString(R.string.vitals_report));
        pdfViewerFragment.setArguments(bundle);
        showSubFragmentInterface.onShowFragment(pdfViewerFragment);

    }

    private void enableOrDisablePrint() {
        if (vitalsApiResponseModel == null || vitalsApiResponseModel.size() == 0 || mode == Constants.EDIT_MODE) {
            toolbar.getMenu().findItem(R.id.menu_print).setVisible(false);
        } else {
            toolbar.getMenu().findItem(R.id.menu_print).setVisible(true);
        }
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

    private void enableOrDisableNext() {
        if (selectedList.isEmpty()) {
            setNextTitle(getString(R.string.cancel));
            if (isDisableCancel)
                nextMenu.setVisible(false);
        } else {
            setNextTitle(getString(R.string.next));
            nextMenu.setVisible(true);
        }
    }

    private void setNextTitle(String title) {
        nextMenu.setTitle(title);
    }

    private void changeToViewMode() {
        mode = Constants.VIEW_MODE;
        actualList.clear();
        selectedList.clear();
        unSelectedList.clear();
        vitalsApiResponseModelMap.clear();
        updateUI();
        vitalsDetailListAdapter.setMode(mode);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void getUserVitals() {
        if (commonUserApiResponseModel != null) {
            vitalsListCelv.hideEmptyState();
            vitalsApiViewModel.getUserFilteredVitals(filter, startDate, endDate, commonUserApiResponseModel.getUser_guid(), doctorGuid);
        }
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
                    if (orderId != null) {
                        if (getTargetFragment() != null)
                            getTargetFragment().onActivityResult(RequestID.REQ_VISIT_UPDATE, Activity.RESULT_OK, new Intent().putExtra(ArgumentKeys.UPDATE_TYPE, VisitDetailConstants.VISIT_TYPE_VITALS));
                        onCloseActionInterface.onClose(false);
                    } else {
                        getUserVitals();
                    }
                }
                break;
        }
    }

    private void addVisit(String order_id) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.loading));
        bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.posting_please_wait));

        showSuccessView(this, RequestID.REQ_SHOW_SUCCESS_VIEW, bundle);

        UpdateVisitRequestModel updateVisitRequestModel = new UpdateVisitRequestModel();
        updateVisitRequestModel.setAssociation_type(VisitConstants.TYPE_VITALS);
        updateVisitRequestModel.setAdd_associations(selectedList);

        visitsApiViewModel.updateOrder(order_id, updateVisitRequestModel, doctorGuid, false);
    }

}
