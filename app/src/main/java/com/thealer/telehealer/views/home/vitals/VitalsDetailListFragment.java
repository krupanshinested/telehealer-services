package com.thealer.telehealer.views.home.vitals;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.PDFUrlResponse;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.getUsers.GetUsersApiViewModel;
import com.thealer.telehealer.apilayer.models.vitalReport.VitalReportApiViewModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsPaginatedApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.OpenTok.CallManager;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.stripe.AppPaymentCardUtils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.common.OverlayViewConstants;
import com.thealer.telehealer.views.common.PdfViewerFragment;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.DoctorPatientDetailViewFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.thealer.telehealer.views.home.vitals.iHealth.pairing.VitalCreationActivity;
import com.thealer.telehealer.views.settings.ProfileSettingsActivity;

import de.hdodenhof.circleimageview.CircleImageView;
import me.toptas.fancyshowcase.listener.DismissListener;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 21,November,2018
 */
public class VitalsDetailListFragment extends BaseFragment implements View.OnClickListener {
    private ImageView backIv;
    private TextView toolbarTitle;
    private FloatingActionButton addFab;
    private Toolbar toolbar;
    private ConstraintLayout userDetailCl;
    private CircleImageView itemCiv;
    private TextView itemTitleTv;
    private TextView itemSubTitleTv;
    private ImageView infoIv;
    private CustomSwipeRefreshLayout swipeLayout;
    private LineChart linechart;
    private WhoAmIApiViewModel whoAmIApiViewModel;

    private OnCloseActionInterface onCloseActionInterface;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private CommonUserApiResponseModel commonUserApiResponseModel, doctorModel;
    private VitalsApiViewModel vitalsApiViewModel;
    private NewVitalsDetailListAdapter vitalsDetailListAdapter;
    private AttachObserverInterface attachObserverInterface;
    private GetUsersApiViewModel getUsersApiViewModel;

    private List<VitalsApiResponseModel> responseModelList = new ArrayList<>();
    private HashMap<Float, String> xaxisLables;
    private List<ILineDataSet> lineDataSetList;
    private ArrayList<Entry> line1Entry;
    private ArrayList<Entry> line2Entry;
    private ArrayList<Entry> line3Entry;
    private List<VitalsApiResponseModel> vitalsApiResponseModelArrayList = new ArrayList<>();
    private String userGuid, doctorGuid;
    private String selectedItem;
    private boolean isAbnormalVitalView = false;
    private boolean isFromHome, isApiRequested;
    private int page = 1;
    private CustomRecyclerView vitalDetailCrv;
    private boolean isGetType;
    private String filter, startDate, endDate;
    private boolean isOpenVitalMessage = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        vitalsApiViewModel = new ViewModelProvider(this).get(VitalsApiViewModel.class);
        attachObserverInterface.attachObserver(vitalsApiViewModel);

        vitalsApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {
                    ArrayList<VitalsApiResponseModel> arrayList = (ArrayList<VitalsApiResponseModel>) (Object) baseApiResponseModels;
                    if (isGetType) {
                        isGetType = false;
                        selectedItem = arrayList.get(0).getType();
                        initializeSelectedItemView(selectedItem);
                        makeApiCall(true);
                    }
                }
            }
        });
        vitalsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {

                    if (baseApiResponseModel instanceof PDFUrlResponse) {
                        PDFUrlResponse pdfUrlResponse = (PDFUrlResponse) baseApiResponseModel;

                        PdfViewerFragment pdfViewerFragment = new PdfViewerFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(ArgumentKeys.PDF_TITLE, getString(R.string.vitals_report));
                        bundle.putString(ArgumentKeys.PDF_URL, pdfUrlResponse.getUrl());
                        bundle.putBoolean(ArgumentKeys.IS_PDF_DECRYPT, true);
                        pdfViewerFragment.setArguments(bundle);
                        showSubFragmentInterface.onShowFragment(pdfViewerFragment);

                    } else {
                        VitalsPaginatedApiResponseModel vitalsPaginatedApiResponseModel = (VitalsPaginatedApiResponseModel) baseApiResponseModel;

                        vitalDetailCrv.setNextPage(vitalsPaginatedApiResponseModel.getNext());
                        if (page == 1) {
                            setTitle(Utils.getPaginatedTitle(getString(SupportedMeasurementType.getTitle(selectedItem)), vitalsPaginatedApiResponseModel.getCount()));
                        }
                        vitalDetailCrv.setNextPage(vitalsPaginatedApiResponseModel.getNext());

                        vitalsApiResponseModelArrayList = vitalsPaginatedApiResponseModel.getResult();


                        if (UserType.isUserPatient() && vitalsApiResponseModelArrayList.size() == 0) {
                            if (!appPreference.getBoolean(selectedItem)) {
                                boolean isShow = true;
                                int message = 0;
                                switch (selectedItem) {
                                    case SupportedMeasurementType.bp:
                                        message = OverlayViewConstants.OVERLAY_NO_BP;
                                        break;
                                    case SupportedMeasurementType.gulcose:
                                        message = OverlayViewConstants.OVERLAY_NO_GLUCOSE;
                                        break;
                                    case SupportedMeasurementType.heartRate:
                                        message = OverlayViewConstants.OVERLAY_NO_HEAR_RATE;
                                        break;
                                    case SupportedMeasurementType.pulseOximeter:
                                        message = OverlayViewConstants.OVERLAY_NO_PULSE;
                                        break;
                                    case SupportedMeasurementType.temperature:
                                        message = OverlayViewConstants.OVERLAY_NO_TEMPERATURE;
                                        break;
                                    case SupportedMeasurementType.weight:
                                        message = OverlayViewConstants.OVERLAY_NO_WEIGHT;
                                        break;
                                    default:
                                        isShow = false;
                                }
                                if (isShow) {
                                    appPreference.setBoolean(selectedItem, true);
                                    Utils.showOverlay(getActivity(), addFab, message, new DismissListener() {
                                        @Override
                                        public void onDismiss(@org.jetbrains.annotations.Nullable String s) {

                                        }

                                        @Override
                                        public void onSkipped(@org.jetbrains.annotations.Nullable String s) {

                                        }
                                    });
                                }
                            }
                        }

                        if (vitalsPaginatedApiResponseModel.getCount() > 0) {

                            vitalsDetailListAdapter.setData(vitalsApiResponseModelArrayList, page);

                            if (!selectedItem.equals(SupportedMeasurementType.stethoscope))
                                updateChart(vitalsApiResponseModelArrayList, vitalsPaginatedApiResponseModel.getNext());

                            vitalDetailCrv.showOrhideEmptyState(false);
                        } else {
                            vitalDetailCrv.showOrhideEmptyState(true);
                        }

                        if (isAbnormalVitalView) {
                            setUserDetailView();
                        }
                    }
                    isApiRequested = false;
                    vitalDetailCrv.setScrollable(true);
                    vitalDetailCrv.hideProgressBar();
                    swipeLayout.setRefreshing(false);
                }
            }
        });

        vitalsApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    if (AppPaymentCardUtils.hasValidPaymentCard(errorModel)) {
                        Utils.showAlertDialog(getContext(), getString(R.string.error),
                                errorModel.getMessage() != null && !errorModel.getMessage().isEmpty() ? errorModel.getMessage() : getString(R.string.failed_to_connect),
                                null, getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                    } else {
                        AppPaymentCardUtils.handleCardCasesFromErrorModel(VitalsDetailListFragment.this, errorModel, doctorModel != null ? doctorModel.getDoctorDisplayName() : null);
                    }
                }
            }
        });

        whoAmIApiViewModel = new ViewModelProvider(this).get(WhoAmIApiViewModel.class);
        attachObserverInterface.attachObserver(whoAmIApiViewModel);
        whoAmIApiViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    WhoAmIApiResponseModel whoAmIApiResponseModel = (WhoAmIApiResponseModel) baseApiResponseModel;
                    if (UserType.isUserDoctor() || UserType.isUserAssistant()) {
                        if (AppPaymentCardUtils.hasValidPaymentCard(whoAmIApiResponseModel.getPayment_account_info())) {
                            proceedAdd(selectedItem);
                        } else {
                            AppPaymentCardUtils.handleCardCasesFromPaymentInfo(getActivity(), whoAmIApiResponseModel.getPayment_account_info(), doctorModel != null ? doctorModel.getDoctorDisplayName() : null);
                        }

                    }
                }
            }
        });
        getUsersApiViewModel = new ViewModelProvider(this).get(GetUsersApiViewModel.class);
        attachObserverInterface.attachObserver(getUsersApiViewModel);

        getUsersApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged
                    (@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {
                    ArrayList<CommonUserApiResponseModel> commonUserApiResponseModelArrayList = (ArrayList<CommonUserApiResponseModel>) (Object) baseApiResponseModels;

                    for (CommonUserApiResponseModel model : commonUserApiResponseModelArrayList) {
                        if (model.getUser_guid().equals(userGuid)) {
                            commonUserApiResponseModel = model;
                        }

                        if (model.getUser_guid().equals(doctorGuid)) {
                            doctorModel = model;
                        }
                    }

                    makeApiCall(true);
                }
            }
        });
    }

    private void setUserDetailView() {
        itemTitleTv.setText(commonUserApiResponseModel.getUserDisplay_name());
        itemSubTitleTv.setText(commonUserApiResponseModel.getDisplayInfo());
        Utils.setImageWithGlide(getActivity().getApplicationContext(), itemCiv, commonUserApiResponseModel.getUser_avatar(), getActivity().getDrawable(R.drawable.profile_placeholder), true, true);
        infoIv.setOnClickListener(this);
        userDetailCl.setVisibility(View.VISIBLE);
    }


    private void updateChart(List<VitalsApiResponseModel> vitalsApiResponseModelList, Object next) {
        if (page == 1) {
            responseModelList = new ArrayList<>(vitalsApiResponseModelList);
        } else {
            responseModelList.addAll(vitalsApiResponseModelList);
        }

        xaxisLables = new HashMap<>();
        lineDataSetList = new ArrayList<>();
        line1Entry = new ArrayList<>();
        line2Entry = new ArrayList<>();
        line3Entry = new ArrayList<>();

        if (responseModelList.size() > 0) {

            Collections.sort(responseModelList, new Comparator<VitalsApiResponseModel>() {
                @Override
                public int compare(VitalsApiResponseModel o1, VitalsApiResponseModel o2) {
                    return Utils.getDateFromString(o2.getCreated_at()).compareTo(Utils.getDateFromString(o1.getCreated_at()));
                }
            });

            linechart.setTouchEnabled(true);
            linechart.getDescription().setEnabled(false);
            linechart.setDrawGridBackground(false);


            String dataSet1Name = getString(SupportedMeasurementType.getTitle(selectedItem));

            float maxVlaue = 0;
            float minValue = 0;


            XAxis xAxis = linechart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setSpaceMin(1f);
            xAxis.setAxisMinimum(-0.2f);
            xAxis.setSpaceMax(1f);
            xAxis.setDrawAxisLine(false);
            xAxis.setTextColor(Color.BLACK);
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    if (xaxisLables.containsKey(value)) {
                        return Utils.getDayMonth(xaxisLables.get(value));
                    } else {
                        return "";
                    }
                }
            });

            YAxis yAxis = linechart.getAxisLeft();
            yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            yAxis.setDrawGridLines(false);
            yAxis.setTextColor(Color.BLACK);
            yAxis.setDrawAxisLine(false);

            linechart.getAxisLeft().setEnabled(true);
            linechart.getAxisRight().setEnabled(false);

            yAxis.setAxisMinimum(0f);

            for (int i = 0; i < responseModelList.size(); i++) {
                try {

                    String value = responseModelList.get(i).getValue().toString()
                            .replace(SupportedMeasurementType.getVitalUnit(selectedItem), "").trim();

                    String type = responseModelList.get(i).getType();

                    if (!value.isEmpty()) {
                        if (selectedItem.equals(SupportedMeasurementType.bp)) {

                            switch (type) {
                                case SupportedMeasurementType.bp:
                                    try {
                                        String[] values = value.split("/");

                                        if (Float.parseFloat(values[0]) > maxVlaue) {
                                            maxVlaue = Float.parseFloat(values[0]);
                                        }
                                        if (i == 1) {
                                            minValue = Float.parseFloat(values[1]);
                                        } else {
                                            if (Float.parseFloat(values[0]) < minValue) {
                                                maxVlaue = Float.parseFloat(values[0]);
                                            }
                                            if (Float.parseFloat(values[1]) < minValue) {
                                                maxVlaue = Float.parseFloat(values[1]);
                                            }
                                        }

                                        line1Entry.add(new Entry(i + 1, Float.parseFloat(values[0]), responseModelList.get(i).getMode()));
                                        line2Entry.add(new Entry(i + 1, Float.parseFloat(values[1]), responseModelList.get(i).getMode()));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case SupportedMeasurementType.heartRate:
                                    if (!value.isEmpty()) {
                                        if (Float.parseFloat(value) > maxVlaue) {
                                            maxVlaue = Float.parseFloat(value);
                                        }
                                        if (i == 1) {
                                            minValue = Float.parseFloat(value);
                                        } else {
                                            if (Float.parseFloat(value) < minValue) {
                                                minValue = Float.parseFloat(value);
                                            }
                                        }
                                        line3Entry.add(new Entry(i + 1, Float.parseFloat(value), responseModelList.get(i).getMode()));
                                    }
                                    break;
                            }
                        } else {
                            if (Float.parseFloat(value) > maxVlaue) {
                                maxVlaue = Float.parseFloat(value);
                            }
                            if (i == 1) {
                                minValue = Float.parseFloat(value);
                            } else {
                                if (Float.parseFloat(value) < minValue) {
                                    minValue = Float.parseFloat(value);
                                }
                            }
                            line1Entry.add(new Entry(i + 1, Float.parseFloat(value), responseModelList.get(i).getMode()));
                        }

                        xaxisLables.put(Float.valueOf(i + 1), responseModelList.get(i).getCreated_at());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            yAxis.calculate(maxVlaue, minValue);

            int color1 = getContext().getColor(R.color.app_gradient_start);

            if (selectedItem.equals(SupportedMeasurementType.bp)) {
                dataSet1Name = VitalsConstant.SYSTOLE;
                color1 = getContext().getColor(R.color.char_line_1);
            }

            LineDataSet lineDataSet1 = new LineDataSet(line1Entry, dataSet1Name);
            lineDataSet1.setCircleColor(color1);
            lineDataSet1.setColor(color1);
            lineDataSet1.setLineWidth(2f);
            lineDataSet1.setCircleRadius(4f);
            lineDataSet1.setDrawValues(false);
            lineDataSet1.setDrawCircleHole(false);
            lineDataSet1.setDrawHighlightIndicators(false);

            lineDataSetList.add(lineDataSet1);

            if (selectedItem.equals(SupportedMeasurementType.bp)) {
                String dataSet2Name = VitalsConstant.DIASTOLE;
                String dataSet3Name = getString(SupportedMeasurementType.getTitle(SupportedMeasurementType.heartRate));

                int color2 = getContext().getColor(R.color.char_line_2);

                LineDataSet lineDataSet2 = new LineDataSet(line2Entry, dataSet2Name);
                lineDataSet2.setCircleColor(color2);
                lineDataSet2.setColor(color2);
                lineDataSet2.setLineWidth(2f);
                lineDataSet2.setDrawValues(false);
                lineDataSet2.setCircleRadius(4f);
                lineDataSet2.setDrawCircleHole(false);
                lineDataSet2.setValueTextColor(color2);
                lineDataSet2.setDrawHighlightIndicators(false);

                int color3 = getContext().getColor(R.color.char_line_3);

                LineDataSet lineDataSet3 = new LineDataSet(line3Entry, dataSet3Name);
                lineDataSet3.setCircleColor(color3);
                lineDataSet3.setColor(color3);
                lineDataSet3.setLineWidth(2f);
                lineDataSet3.setDrawValues(false);
                lineDataSet3.setCircleRadius(4f);
                lineDataSet3.setDrawCircleHole(false);
                lineDataSet3.setValueTextColor(color3);
                lineDataSet3.setDrawHighlightIndicators(false);

                lineDataSetList.add(lineDataSet2);
                lineDataSetList.add(lineDataSet3);

            }

            LineData lineData = new LineData(lineDataSetList);

            MyMarkerView markerView = new MyMarkerView(getContext(), R.layout.layout_chart_marker);
            linechart.setMarker(markerView);

            linechart.setData(lineData);

            linechart.invalidate();
            linechart.setVisibleXRange(0, 6);
            linechart.animate();
            linechart.setScaleEnabled(false);
            linechart.setDoubleTapToZoomEnabled(false);
            linechart.setPinchZoom(false);
            linechart.setNoDataTextColor(getActivity().getColor(R.color.colorBlack));

            Legend legend = linechart.getLegend();

            legend.setFormSize(10f);
            legend.setForm(Legend.LegendForm.LINE);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setTextColor(Color.BLACK);
            legend.setXEntrySpace(10f);

            linechart.setExtraOffsets(0, 0, 0, 10);

            toolbar.getMenu().findItem(R.id.print_menu).setEnabled(true);
            toolbar.getMenu().findItem(R.id.print_menu).getIcon().setTint(getActivity().getColor(R.color.colorWhite));
            linechart.setVisibility(View.VISIBLE);

            linechart.setOnChartGestureListener(new OnChartGestureListener() {
                @Override
                public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

                }

                @Override
                public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

                    if (linechart.getHighestVisibleX() >= responseModelList.size() - 1) {
                        if (next != null) {
                            page = page + 1;
                            makeApiCall(true);
                        }
                    }
                }

                @Override
                public void onChartLongPressed(MotionEvent me) {

                }

                @Override
                public void onChartDoubleTapped(MotionEvent me) {

                }

                @Override
                public void onChartSingleTapped(MotionEvent me) {

                }

                @Override
                public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                }

                @Override
                public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

                }

                @Override
                public void onChartTranslate(MotionEvent me, float dX, float dY) {

                }
            });

        } else {
            toolbar.getMenu().findItem(R.id.print_menu).setEnabled(false);
            toolbar.getMenu().findItem(R.id.print_menu).getIcon().setTint(getActivity().getColor(R.color.colorGrey_light));
            linechart.setVisibility(View.GONE);
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
        View view = inflater.inflate(R.layout.fragment_vital_detail_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        vitalDetailCrv = (CustomRecyclerView) view.findViewById(R.id.vital_detail_crv);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        addFab = (FloatingActionButton) view.findViewById(R.id.add_fab);
        linechart = (LineChart) view.findViewById(R.id.linechart);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        userDetailCl = (ConstraintLayout) view.findViewById(R.id.user_detail_cl);
        itemCiv = (CircleImageView) view.findViewById(R.id.item_civ);
        itemTitleTv = (TextView) view.findViewById(R.id.item_title_tv);
        itemSubTitleTv = (TextView) view.findViewById(R.id.item_sub_title_tv);
        infoIv = (ImageView) view.findViewById(R.id.info_iv);
        swipeLayout = (CustomSwipeRefreshLayout) view.findViewById(R.id.swipe_layout);

        toolbar.inflateMenu(R.menu.orders_detail_menu);
        toolbar.getMenu().removeItem(R.id.send_fax_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.print_menu:
                        Utils.showMonitoringFilter(null, getActivity(), new OnListItemSelectInterface() {
                            @Override
                            public void onListItemSelected(int position, Bundle bundle) {
                                openPDFFor(bundle);
                            }
                        });

                        break;
                }
                return true;
            }
        });

        backIv.setOnClickListener(this);
        addFab.setOnClickListener(this);

        vitalDetailCrv.getSwipeLayout().setEnabled(false);

        swipeLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                makeApiCall(false);
            }
        });

        vitalDetailCrv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeApiCall(true);
            }
        });

        vitalDetailCrv.setErrorModel(this, vitalsApiViewModel.getErrorModelLiveData());

        if (getArguments() != null) {

            isGetType = getArguments().getBoolean(ArgumentKeys.IS_GET_TYPE, false);
            isFromHome = getArguments().getBoolean(Constants.IS_FROM_HOME);

            isAbnormalVitalView = getArguments().getBoolean(ArgumentKeys.VIEW_ABNORMAL_VITAL);

            if (isAbnormalVitalView) {
                userGuid = getArguments().getString(ArgumentKeys.USER_GUID);
                doctorGuid = getArguments().getString(ArgumentKeys.DOCTOR_GUID);
            }

            if (!isFromHome) {
                commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
                if (commonUserApiResponseModel != null) {
                    userGuid = commonUserApiResponseModel.getUser_guid();
                }
            }

            doctorModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);
            if (doctorModel != null) {
                doctorGuid = doctorModel.getUser_guid();
            }

            if (isGetType) {
                int id = getArguments().getInt(ArgumentKeys.ORDER_ID);
                vitalsApiViewModel.getVitalDetail(userGuid, (UserType.isUserAssistant() ? doctorGuid : null), new ArrayList<>(Arrays.asList(id)), true);
            } else {
                selectedItem = getArguments().getString(ArgumentKeys.MEASUREMENT_TYPE);
                initializeSelectedItemView(selectedItem);
            }
        }

    }

    private void openPDFFor(Bundle bundle) {

        filter = bundle.getString(Constants.SELECTED_ITEM);
        startDate = bundle.getString(ArgumentKeys.START_DATE);
        endDate = bundle.getString(ArgumentKeys.END_DATE);

        String selectedItem = bundle.getString(Constants.SELECTED_ITEM);
        startDate = null;
        endDate = null;

        if (selectedItem != null) {
            if (selectedItem.equals(getString(R.string.last_week))) {
                filter = VitalReportApiViewModel.LAST_WEEK;
            } else if (selectedItem.equals(getString(R.string.all))) {
                filter = VitalReportApiViewModel.ALL;
            } else {
                filter = null;
                startDate = bundle.getString(ArgumentKeys.START_DATE);
                endDate = bundle.getString(ArgumentKeys.END_DATE);

            }
        }

        vitalsApiViewModel.getVitalPdf(getCurrentVitalType(), filter, startDate, endDate, userGuid, doctorGuid, true);

    }

    private void initializeSelectedItemView(String selectedItem) {
        if (selectedItem != null) {
            setEmptyState();

            if (selectedItem.equals(SupportedMeasurementType.stethoscope)) {
                toolbar.getMenu().clear();
                addFab.hide();
            }
            isOpenVitalMessage = true;
            vitalsDetailListAdapter = new NewVitalsDetailListAdapter(getActivity(), isOpenVitalMessage);
            vitalDetailCrv.getRecyclerView().setAdapter(vitalsDetailListAdapter);


            vitalDetailCrv.setOnPaginateInterface(new OnPaginateInterface() {
                @Override
                public void onPaginate() {
                    vitalDetailCrv.setScrollable(false);
                    page = page + 1;
                    vitalDetailCrv.showProgressBar();
                    makeApiCall(false);
                }
            });
        }
    }

    private void makeApiCall(boolean isShowProgress) {
        if (!isApiRequested) {
            isApiRequested = true;
            if (selectedItem != null) {
                if (!isFromHome) {
                    if (commonUserApiResponseModel != null) {
                        vitalsApiViewModel.getUserVitals(getCurrentVitalType(), commonUserApiResponseModel.getUser_guid(), doctorGuid, isShowProgress, page);
                    }
                } else {
                    vitalsApiViewModel.getVitals(getCurrentVitalType(), page, isShowProgress);
                }
            }
        }
    }

    private String getCurrentVitalType() {
        String type = selectedItem;
        if (selectedItem.equals(SupportedMeasurementType.bp)) {
            type = type + "," + SupportedMeasurementType.heartRate;
        }

        return type;
    }

    private void setEmptyState() {
        String emptyStateType = "";

        setTitle(getString(SupportedMeasurementType.getTitle(selectedItem)));
        switch (selectedItem) {
            case SupportedMeasurementType.bp:
                emptyStateType = EmptyViewConstants.EMPTY_BP;
                break;
            case SupportedMeasurementType.gulcose:
                emptyStateType = EmptyViewConstants.EMPTY_GULCOSE;
                break;
            case SupportedMeasurementType.stethoscope:
                emptyStateType = EmptyViewConstants.EMPTY_STETHOSCOPE;
                break;
            case SupportedMeasurementType.heartRate:
                emptyStateType = EmptyViewConstants.EMPTY_HEART_RATE;
                break;
            case SupportedMeasurementType.pulseOximeter:
                emptyStateType = EmptyViewConstants.EMPTY_PULSE;
                break;
            case SupportedMeasurementType.temperature:
                emptyStateType = EmptyViewConstants.EMPTY_TEMPERATURE;
                break;
            case SupportedMeasurementType.weight:
                emptyStateType = EmptyViewConstants.EMPTY_WEIGHT;
                break;
            case SupportedMeasurementType.height:
                emptyStateType = EmptyViewConstants.EMPTY_HEIGHT;
                break;
        }
        vitalDetailCrv.setEmptyState(emptyStateType);
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

                if (CallManager.shared.isActiveCallPresent()) {
                    Toast.makeText(getActivity(), getString(R.string.live_call_going_error), Toast.LENGTH_LONG).show();
                    return;
                }

                addFab.setClickable(false);
                checkWhoAmI();
                break;
            case R.id.info_iv:
                showUserDetailView();
                break;
        }
    }

    private void checkWhoAmI() {
        if (UserType.isUserPatient()) {
            proceedAdd(selectedItem);
        } else {
            whoAmIApiViewModel.checkWhoAmI(doctorGuid);
        }
    }

    private void proceedAdd(String selectedItem) {
        Bundle bundle = getArguments();

        if (bundle == null) {
            bundle = new Bundle();
        }

        if (isAbnormalVitalView) {
            bundle.putSerializable(Constants.USER_DETAIL, commonUserApiResponseModel);
        }

        Intent intent = new Intent(getActivity(), VitalCreationActivity.class);
        intent.putExtra(ArgumentKeys.SELECTED_VITAL_TYPE, selectedItem);
        if (!UserType.isUserPatient()) {
            intent.putExtra(Constants.USER_DETAIL, bundle.getSerializable(Constants.USER_DETAIL));
        }

        if (UserType.isUserAssistant()) {
            intent.putExtra(Constants.DOCTOR_ID, doctorGuid);
        }

        getActivity().startActivity(intent);
    }

    private void showUserDetailView() {
        DoctorPatientDetailViewFragment doctorPatientDetailViewFragment = new DoctorPatientDetailViewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.USER_DETAIL, commonUserApiResponseModel);
        bundle.putSerializable(Constants.DOCTOR_DETAIL, doctorModel);
        bundle.putString(Constants.VIEW_TYPE, Constants.VIEW_ASSOCIATION_DETAIL);

        doctorPatientDetailViewFragment.setArguments(bundle);
        showSubFragmentInterface.onShowFragment(doctorPatientDetailViewFragment);
    }

    @Override
    public void onResume() {
        super.onResume();
        addFab.setClickable(true);
        if (isAbnormalVitalView && commonUserApiResponseModel == null) {
            getUserDetail();
        } else if (!isGetType) {
            makeApiCall(true);
        }
    }

    private void getUserDetail() {
        Set<String> guidSet = new HashSet<>();
        guidSet.add(userGuid);
        guidSet.add(doctorGuid);

        getUsersApiViewModel.getUserByGuid(guidSet);
    }

    private class MyMarkerView extends MarkerView {
        private TextView valueTv;
        private TextView dateTv;
        private ImageView topArrow, bottomArrow;


        public MyMarkerView(Context context, int layout_chart_marker) {
            super(context, layout_chart_marker);
            valueTv = (TextView) findViewById(R.id.value_tv);
            dateTv = (TextView) findViewById(R.id.date_tv);
            topArrow = (ImageView) findViewById(R.id.top_arrow);
            bottomArrow = (ImageView) findViewById(R.id.bottom_arrow);
        }

        @Override
        public void draw(Canvas canvas, float posX, float posY) {

            float x = -getWidth() / 2;
            float y = -getHeight() - 24;

            topArrow.setVisibility(GONE);
            bottomArrow.setVisibility(VISIBLE);

            if (posY < canvas.getHeight() / 2) {
                y = 24;
                topArrow.setVisibility(VISIBLE);
                bottomArrow.setVisibility(GONE);
            }

            setOffset(new MPPointF(x, y));
            super.draw(canvas, posX, posY);
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {

            linechart.moveViewToX(e.getX() - 3);

            String value = "";
            String unit;

            if (selectedItem.equals(SupportedMeasurementType.bp)) {
                String systole = null;
                String diastole = null;
                String heartRate = null;

                unit = SupportedMeasurementType.getVitalUnit(SupportedMeasurementType.bp);
                if (line1Entry.contains(e) || line2Entry.contains(e)) {
                    for (int i = 0; i < line1Entry.size(); i++) {
                        if (line1Entry.get(i).equalTo(e)) {
                            systole = VitalsConstant.SYSTOLE + " : " + (int) line1Entry.get(i).getY() + " " + unit;
                            break;
                        }
                    }
                    for (int i = 0; i < line2Entry.size(); i++) {
                        if (line2Entry.get(i).equalTo(e)) {
                            diastole = VitalsConstant.DIASTOLE + " : " + (int) line2Entry.get(i).getY() + " " + unit;
                            break;
                        }
                    }
                }

                if (line3Entry.contains(e)) {
                    unit = SupportedMeasurementType.getVitalUnit(SupportedMeasurementType.heartRate);
                    for (int i = 0; i < line3Entry.size(); i++) {
                        if (line3Entry.get(i).equalTo(e)) {
                            heartRate = String.valueOf((int) line3Entry.get(i).getY()) + " " + unit;
                            break;
                        }
                    }
                }

                if (systole != null) {
                    value = value.concat(systole);
                }
                if (diastole != null) {
                    if (!value.isEmpty()) {
                        value = value.concat("\n");
                    }
                    value = value.concat(diastole);
                }
                if (heartRate != null) {
                    if (!value.isEmpty()) {
                        value = value.concat("\n");
                    }
                    value = value.concat(heartRate);
                }

            } else {
                unit = SupportedMeasurementType.getVitalUnit(selectedItem);

                for (int i = 0; i < line1Entry.size(); i++) {
                    if (line1Entry.get(i).equalTo(e)) {
                        value = String.valueOf((int) line1Entry.get(i).getY()) + " " + unit;
                        break;
                    }
                }
            }
            valueTv.setText(value);

            String date = Utils.getDayMonthTime(xaxisLables.get(e.getX()));

            if (!e.getData().toString().equals(VitalsConstant.VITAL_MODE_DEVICE)) {
                dateTv.setText(date + "\n( " + getString(R.string.manual) + " )");
            } else {
                dateTv.setText(date);
            }


            super.refreshContent(e, highlight);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.hideOverlay();
    }

}
