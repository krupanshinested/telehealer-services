package com.thealer.telehealer.views.home.vitals.vitalReport;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitalReport.VitalReportApiReponseModel;
import com.thealer.telehealer.apilayer.models.vitalReport.VitalReportApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Util.TimerInterface;
import com.thealer.telehealer.common.Util.TimerRunnable;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VisitConstants;
import com.thealer.telehealer.common.emptyState.EmptyStateUtil;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.stripe.AppPaymentCardUtils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.common.PdfViewerFragment;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.settings.ProfileSettingsActivity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Aswin on 04,February,2019
 */
public class VitalReportFragment extends BaseFragment {
    private LinearLayout searchLl;
    private View topView;
    private CardView searchCv;
    private EditText searchEt;
    private ImageView searchClearIv;
    private View bottomView;
    private ImageView filterIv;
    private CustomRecyclerView patientListCrv;

    private AttachObserverInterface attachObserverInterface;
    private VitalReportApiViewModel vitalReportApiViewModel;

    private VitalReportApiReponseModel vitalReportApiReponseModel;
    private VitalBulkPdfApiResponseModel vitalBulkPdfApiResponseModel;
    private VitalReportUserListAdapter vitalReportUserListAdapter;
    private static String selectedFilter, title;
    private String startDate = null;
    private String endDate = null;
    private List<CommonUserApiResponseModel> searchList = new ArrayList<>();
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private OnCloseActionInterface onCloseActionInterface;
    private String doctorGuid;
    private ShowSubFragmentInterface showSubFragmentInterface;

    private CommonUserApiResponseModel doctorModel;

    @Nullable
    private TimerRunnable uiToggleTimer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();

        vitalReportApiViewModel = new ViewModelProvider(this).get(VitalReportApiViewModel.class);
        attachObserverInterface.attachObserver(vitalReportApiViewModel);
        vitalReportApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof VitalReportApiReponseModel) {
                        vitalReportApiReponseModel = (VitalReportApiReponseModel) baseApiResponseModel;
                        if (vitalReportApiReponseModel.getResult().size() > 0) {
                            vitalReportUserListAdapter.setData(vitalReportApiReponseModel.getResult());
                            patientListCrv.showOrhideEmptyState(false);
                        } else {
                            patientListCrv.showOrhideEmptyState(true);
                        }
                    } else if (baseApiResponseModel instanceof VitalBulkPdfApiResponseModel) {
                        vitalBulkPdfApiResponseModel = (VitalBulkPdfApiResponseModel) baseApiResponseModel;
                        if (vitalBulkPdfApiResponseModel.getCombined_pdf_path() != null) {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, true);
                            bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
                            bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, "");
                            bundle.putBoolean(Constants.SUCCESS_VIEW_AUTO_DISMISS, true);
                            LocalBroadcastManager
                                    .getInstance(getActivity())
                                    .sendBroadcast(new Intent(getString(R.string.success_broadcast_receiver))
                                            .putExtras(bundle));

                            PdfViewerFragment pdfViewerFragment = new PdfViewerFragment();
                            bundle.putString(ArgumentKeys.PDF_TITLE, getString(R.string.vitals_report));
                            bundle.putString(ArgumentKeys.PDF_URL, vitalBulkPdfApiResponseModel.getCombined_pdf_path());
                            bundle.putBoolean(ArgumentKeys.IS_PDF_DECRYPT, true);
                            pdfViewerFragment.setArguments(bundle);
                            showSubFragmentInterface.onShowFragment(pdfViewerFragment);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, true);
                            bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
                            bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.bulk_pdf_success_message));
                            LocalBroadcastManager
                                    .getInstance(getActivity())
                                    .sendBroadcast(new Intent(getString(R.string.success_broadcast_receiver))
                                            .putExtras(bundle));
                        }
                    }
                }
            }
        });

        vitalReportApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {

                    if (!errorModel.geterrorCode().isEmpty() && !errorModel.geterrorCode().equals("SUBSCRIPTION")) {

                        if (AppPaymentCardUtils.hasValidPaymentCard(errorModel)) {
                            sendSuccessViewBroadCast(getActivity(), false, getString(R.string.failure), errorModel.getMessage() != null && !errorModel.getMessage().isEmpty() ? errorModel.getMessage() : getString(R.string.failed_to_connect));
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, true);
                            bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
                            bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, "");
                            bundle.putBoolean(Constants.SUCCESS_VIEW_AUTO_DISMISS, true);
                            LocalBroadcastManager
                                    .getInstance(getActivity())
                                    .sendBroadcast(new Intent(getString(R.string.success_broadcast_receiver))
                                            .putExtras(bundle));
                            AppPaymentCardUtils.handleCardCasesFromErrorModel(VitalReportFragment.this, errorModel, doctorModel != null ? doctorModel.getDoctorDisplayName() : null);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        selectedFilter = VitalReportApiViewModel.LAST_MONTH;

        Calendar calendar = (Calendar) Calendar.getInstance().clone();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        startDate = Utils.getUTCfromGMT(new Timestamp(calendar.getTimeInMillis()).toString());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        endDate = Utils.getUTCfromGMT(new Timestamp(calendar.getTimeInMillis()).toString());

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vital_report, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        searchLl = (LinearLayout) view.findViewById(R.id.search_ll);
        topView = (View) view.findViewById(R.id.top_view);
        searchCv = (CardView) view.findViewById(R.id.search_cv);
        searchEt = (EditText) view.findViewById(R.id.search_et);
        searchClearIv = (ImageView) view.findViewById(R.id.search_clear_iv);
        bottomView = (View) view.findViewById(R.id.bottom_view);
        filterIv = (ImageView) view.findViewById(R.id.filter_iv);
        patientListCrv = (CustomRecyclerView) view.findViewById(R.id.patient_list_crv);

        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);

        filterIv.setVisibility(View.GONE);

        toolbar.inflateMenu(R.menu.filter_menu);
        MenuItem filterItem = toolbar.getMenu().findItem(R.id.menu_filter);
        View filterView = filterItem.getActionView();
        ImageView filterIv = filterView.findViewById(R.id.filter_iv);
        ImageView filterIndicatorIv = filterView.findViewById(R.id.filter_indicatior_iv);
        filterIndicatorIv.setVisibility(View.VISIBLE);

        filterIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });
        toolbar.inflateMenu(R.menu.orders_detail_menu);
        toolbar.getMenu().removeItem(R.id.send_fax_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.print_menu:
                        Snackbar snackbar = Snackbar.make(view, "", 5000).setBackgroundTint(getResources().getColor(R.color.app_gradient_start));
                        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
                        View snackbarView = getLayoutInflater().inflate(R.layout.view_snackbar, null);
                        TextView textView = snackbarView.findViewById(R.id.snackbar_tv);
                        textView.setMaxLines(10);
                        textView.setText(getString(R.string.bulk_print_disclaimer));
                        snackbarLayout.addView(snackbarView);
                        snackbar.addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                super.onDismissed(transientBottomBar, event);
                                if (UserType.isUserAssistant()) {
                                    vitalReportApiViewModel.getBulkVitalPdf(doctorGuid, startDate, endDate);
                                } else {
                                    vitalReportApiViewModel.getBulkVitalPdf(null, startDate, endDate);
                                }
                                SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
                                successViewDialogFragment.setTargetFragment(getTargetFragment(), RequestID.REQ_SHOW_SUCCESS_VIEW);
                                successViewDialogFragment.show(getActivity().getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());
                            }
                        }).show();
                }
                return true;
            }
        });
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                searchList.clear();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    if (vitalReportApiReponseModel != null) {

                        if (uiToggleTimer != null) {
                            uiToggleTimer.setStopped(true);
                            uiToggleTimer = null;
                        }

                        Handler handler = new Handler();
                        TimerRunnable runnable = new TimerRunnable(new TimerInterface() {
                            @Override
                            public void run() {
                                for (int i = 0; i < vitalReportApiReponseModel.getResult().size(); i++) {
                                    if (vitalReportApiReponseModel.getResult().get(i).getUserDisplay_name().toLowerCase().contains(searchEt.getText().toString())) {
                                        searchList.add(vitalReportApiReponseModel.getResult().get(i));
                                    }
                                }
                            }
                        });
                        uiToggleTimer = runnable;
                        handler.postDelayed(runnable, ArgumentKeys.SEARCH_INTERVAL);

                        if (searchList.size() > 0) {
                            if (vitalReportUserListAdapter != null) {
                                vitalReportUserListAdapter.setData(searchList);
                                patientListCrv.showOrhideEmptyState(false);
                            }
                        } else {
                            patientListCrv.showOrhideEmptyState(true);
                        }
                    } else {
                        patientListCrv.showOrhideEmptyState(true);
                    }
                } else {
                    if (vitalReportApiReponseModel != null) {
                        vitalReportUserListAdapter.setData(vitalReportApiReponseModel.getResult());
                        patientListCrv.showOrhideEmptyState(false);
                    } else {
                        patientListCrv.showOrhideEmptyState(true);
                    }
                }
            }
        });

        patientListCrv.setEmptyState(EmptyViewConstants.EMPTY_DOCTOR_VITAL_SEARCH);
        patientListCrv.setEmptyStateTitle(String.format(EmptyStateUtil.getTitle(getActivity(), EmptyViewConstants.EMPTY_VITAL_FROM_TO), Utils.getDayMonthYear(startDate), Utils.getDayMonthYear(endDate)));

        if (getArguments() != null) {
            doctorModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);
            if (doctorModel != null) {
                doctorGuid = doctorModel.getUser_guid();
            }

            if (getArguments().getBoolean(ArgumentKeys.SHOW_TOOLBAR)) {
                appbarLayout.setVisibility(View.VISIBLE);
                if (startDate != null && endDate != null) {
                    setToolbarTitle(Utils.getMonitoringTitle(startDate, endDate));
                } else {
                    setToolbarTitle(selectedFilter);
                }
                onCloseActionInterface = (OnCloseActionInterface) getActivity();
                backIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCloseActionInterface.onClose(false);
                    }
                });
            } else {
                appbarLayout.setVisibility(View.GONE);
            }
        }
        vitalReportUserListAdapter = new VitalReportUserListAdapter(getActivity(), new OnListItemSelectInterface() {
            @Override
            public void onListItemSelected(int position, Bundle bundle) {

                VitalUserReportListFragment vitalUserReportListFragment = new VitalUserReportListFragment();
                bundle.putString(ArgumentKeys.SEARCH_TYPE, selectedFilter);
                bundle.putString(ArgumentKeys.START_DATE, startDate);
                bundle.putString(ArgumentKeys.END_DATE, endDate);
                bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);
                bundle.putString(ArgumentKeys.DOCTOR_NAME, doctorModel != null ? doctorModel.getDoctorDisplayName() : null);
                bundle.putString(ArgumentKeys.TITLE, title);
                vitalUserReportListFragment.setArguments(bundle);
                showSubFragmentInterface.onShowFragment(vitalUserReportListFragment);

            }
        });

        patientListCrv.getRecyclerView().setAdapter(vitalReportUserListAdapter);

        patientListCrv.getSwipeLayout().setEnabled(false);

        patientListCrv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsersList(selectedFilter, startDate, endDate);
            }
        });

        patientListCrv.setErrorModel(this, vitalReportApiViewModel.getErrorModelLiveData());

        if (selectedFilter != null && selectedFilter.equals(VitalReportApiViewModel.LAST_MONTH)) {
            getUsersList(null, startDate, endDate);
        } else {
            getUsersList(selectedFilter, startDate, endDate);
        }
    }

    private void showFilterDialog() {

        Utils.showMonitoringFilter(null, getActivity(), new OnListItemSelectInterface() {
            @Override
            public void onListItemSelected(int position, Bundle bundle) {
                String selectedItem = bundle.getString(Constants.SELECTED_ITEM);
                startDate = null;
                endDate = null;

                if (selectedItem != null) {
                    Calendar previousMonth = Calendar.getInstance();
                    previousMonth.add(Calendar.MONTH, -1);
                    String previous = previousMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                    setToolbarTitle(selectedItem);
                    if (selectedItem.equals(previous)) {
                        toolbar.getMenu().findItem(R.id.print_menu).setEnabled(true);
                        toolbar.getMenu().findItem(R.id.print_menu).getIcon().setTint(getActivity().getColor(R.color.colorWhite));
                    } else {
                        toolbar.getMenu().findItem(R.id.print_menu).setEnabled(false);
                        toolbar.getMenu().findItem(R.id.print_menu).getIcon().setTint(getActivity().getColor(R.color.colorGrey_light));
                    }
                    if (selectedItem.equals(getString(R.string.last_week))) {
                        patientListCrv.setEmptyState(EmptyViewConstants.EMPTY_DOCTOR_VITAL_LAST_WEEK);
                        selectedFilter = VitalReportApiViewModel.LAST_WEEK;
                    } else if (selectedItem.equals(getString(R.string.all))) {
                        patientListCrv.setEmptyState(EmptyViewConstants.EMPTY_DOCTOR_VITAL_SEARCH);
                        selectedFilter = VitalReportApiViewModel.ALL;
                    } else {
                        selectedFilter = null;
                        startDate = bundle.getString(ArgumentKeys.START_DATE);
                        endDate = bundle.getString(ArgumentKeys.END_DATE);

                        setToolbarTitle(Utils.getMonitoringTitle(startDate, endDate));
                        String title = EmptyStateUtil.getTitle(getActivity(), EmptyViewConstants.EMPTY_VITAL_FROM_TO);

                        patientListCrv.setEmptyStateTitle(String.format(title, Utils.getDayMonthYear(startDate), Utils.getDayMonthYear(endDate)));
                    }
                }
                getUsersList(selectedFilter, startDate, endDate);
            }
        });
    }

    private void getUsersList(String filter, String startDate, String endDate) {
        vitalReportApiViewModel.getVitalUsers(filter, startDate, endDate, doctorGuid, true);
    }

    private void setToolbarTitle(String text) {
        title = text;
        if (text.equals(getString(R.string.all))) {
            toolbarTitle.setText(getString(R.string.vitals));
        } else {
            toolbarTitle.setText(String.format(getString(R.string.vitals) + " (%s)", text));
        }
    }

}
