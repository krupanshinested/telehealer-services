package com.thealer.telehealer.views.home.vitals;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.github.mikephil.charting.utils.MPPointF;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.getUsers.GetUsersApiViewModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomExpandableListView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.OpenTok.TokBox;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.CustomDialogs.ItemPickerDialog;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OverlayViewConstants;
import com.thealer.telehealer.views.common.PdfViewerFragment;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.DoctorPatientDetailViewFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import iHealth.pairing.VitalCreationActivity;
import me.toptas.fancyshowcase.listener.DismissListener;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 21,November,2018
 */
public class VitalsDetailListFragment extends BaseFragment implements View.OnClickListener {
    private CustomExpandableListView vitalDetailCelv;
    private ImageView backIv;
    private TextView toolbarTitle;
    private FloatingActionButton addFab;
    private String selectedItem;

    private OnCloseActionInterface onCloseActionInterface;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private CommonUserApiResponseModel commonUserApiResponseModel, doctorModel;
    private boolean isFromHome;
    private VitalsApiViewModel vitalsApiViewModel;
    private VitalsDetailListAdapter vitalsDetailListAdapter;
    private List<String> headerList = new ArrayList<>();
    private HashMap<String, List<VitalsApiResponseModel>> childList = new HashMap<>();
    private ExpandableListView expandableListView;
    private AttachObserverInterface attachObserverInterface;
    private LineChart linechart;
    private HashMap<Float, String> xaxisLables;
    private ArrayList<Entry> line1Entry;
    private ArrayList<Entry> line2Entry;
    private ArrayList<Entry> line3Entry;
    private Toolbar toolbar;
    private ArrayList<VitalsApiResponseModel> vitalsApiResponseModelArrayList;
    private GetUsersApiViewModel getUsersApiViewModel;
    private boolean isAbnormalVitalView = false;
    private String userGuid, doctorGuid;
    private ConstraintLayout userDetailCl;
    private CircleImageView itemCiv;
    private TextView itemTitleTv;
    private TextView itemSubTitleTv;
    private ImageView infoIv;
    private CustomSwipeRefreshLayout swipeLayout;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        vitalsApiViewModel = ViewModelProviders.of(this).get(VitalsApiViewModel.class);
        attachObserverInterface.attachObserver(vitalsApiViewModel);
        vitalsApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                swipeLayout.setRefreshing(false);
                if (baseApiResponseModels != null) {

                    vitalsApiResponseModelArrayList = (ArrayList<VitalsApiResponseModel>) (Object) baseApiResponseModels;

                    if (UserType.isUserPatient() && vitalsApiResponseModelArrayList.size() == 0) {
                        if (!appPreference.getBoolean(selectedItem)) {
                            boolean isShow = true;
                            String message = "";
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

                    updateList(vitalsApiResponseModelArrayList);

                    if (!selectedItem.equals(SupportedMeasurementType.stethoscope))
                        updateChart(vitalsApiResponseModelArrayList);

                    if (isAbnormalVitalView) {
                        setUserDetailView();
                    }
                }
            }
        });

        getUsersApiViewModel = ViewModelProviders.of(this).get(GetUsersApiViewModel.class);
        attachObserverInterface.attachObserver(getUsersApiViewModel);

        getUsersApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
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
        Utils.setImageWithGlide(getActivity().getApplicationContext(), itemCiv, commonUserApiResponseModel.getUser_avatar(), getActivity().getDrawable(R.drawable.profile_placeholder), true);
        infoIv.setOnClickListener(this);
        userDetailCl.setVisibility(View.VISIBLE);
    }

    private void updateChart(ArrayList<VitalsApiResponseModel> vitalsApiResponseModelArrayList) {
        if (vitalsApiResponseModelArrayList.size() > 0) {

            linechart.setTouchEnabled(true);
            linechart.getDescription().setEnabled(false);
            linechart.setDrawGridBackground(false);

            xaxisLables = new HashMap<>();
            List<ILineDataSet> lineDataSetList = new ArrayList<>();
            line1Entry = new ArrayList<>();
            line2Entry = new ArrayList<>();
            line3Entry = new ArrayList<>();

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


            for (int i = 0; i < vitalsApiResponseModelArrayList.size(); i++) {

                String value = vitalsApiResponseModelArrayList.get(i).getValue().toString()
                        .replace(SupportedMeasurementType.getVitalUnit(selectedItem), "").trim();

                String type = vitalsApiResponseModelArrayList.get(i).getType();

                if (selectedItem.equals(SupportedMeasurementType.bp)) {

                    switch (type) {
                        case SupportedMeasurementType.bp:
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

                            line1Entry.add(new Entry(i + 1, Float.parseFloat(values[0]), vitalsApiResponseModelArrayList.get(i).getMode()));
                            line2Entry.add(new Entry(i + 1, Float.parseFloat(values[1]), vitalsApiResponseModelArrayList.get(i).getMode()));
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
                                line3Entry.add(new Entry(i + 1, Float.parseFloat(value), vitalsApiResponseModelArrayList.get(i).getMode()));
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
                    line1Entry.add(new Entry(i + 1, Float.parseFloat(value), vitalsApiResponseModelArrayList.get(i).getMode()));
                }

                xaxisLables.put(Float.valueOf(i + 1), vitalsApiResponseModelArrayList.get(i).getCreated_at());
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
        vitalDetailCelv = (CustomExpandableListView) view.findViewById(R.id.vital_detail_celv);
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
                        ItemPickerDialog itemPickerDialog = new ItemPickerDialog(getActivity(), getString(R.string.choose_time_period),
                                VitalsConstant.vitalPrintOptions, new PickerListener() {
                            @Override
                            public void didSelectedItem(int position) {
                                generatePdfListItems(VitalsConstant.vitalPrintOptions.get(position));
                            }

                            @Override
                            public void didCancelled() {

                            }
                        });
                        itemPickerDialog.setCancelable(false);
                        itemPickerDialog.show();
                        break;
                }
                return true;
            }
        });

        backIv.setOnClickListener(this);
        addFab.setOnClickListener(this);

        vitalDetailCelv.getSwipeLayout().setEnabled(false);

        swipeLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                makeApiCall(false);
            }
        });

        if (UserType.isUserAssistant()) {
            addFab.hide();
        }

        vitalDetailCelv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeApiCall(true);
            }
        });

        vitalDetailCelv.setErrorModel(this, vitalsApiViewModel.getErrorModelLiveData());

        if (getArguments() != null) {

            selectedItem = getArguments().getString(ArgumentKeys.MEASUREMENT_TYPE);

            if (selectedItem.equals(SupportedMeasurementType.stethoscope)) {
                toolbar.getMenu().clear();
                addFab.hide();
            }

            isFromHome = getArguments().getBoolean(Constants.IS_FROM_HOME);

            isAbnormalVitalView = getArguments().getBoolean(ArgumentKeys.VIEW_ABNORMAL_VITAL);

            if (isAbnormalVitalView) {
                userGuid = getArguments().getString(ArgumentKeys.USER_GUID);
                doctorGuid = getArguments().getString(ArgumentKeys.DOCTOR_GUID);
            }

            if (!isFromHome) {
                commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            }

            doctorModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);
            if (doctorModel != null) {
                doctorGuid = doctorModel.getUser_guid();
            }

            if (selectedItem != null) {
                setEmptyState();

                vitalsDetailListAdapter = new VitalsDetailListAdapter(getActivity(), headerList, childList, selectedItem);

                expandableListView = vitalDetailCelv.getExpandableView();

                expandableListView.setAdapter(vitalsDetailListAdapter);

                vitalDetailCelv.setOnPaginateInterface(new OnPaginateInterface() {
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

    private void generatePdfListItems(String timePeriod) {
        Calendar calendar = Calendar.getInstance();

        List<VitalsApiResponseModel> pdfList = new ArrayList<>();

        if (timePeriod.equals(VitalsConstant.PRINT_ALL)) {
            pdfList.addAll(vitalsApiResponseModelArrayList);
        } else {
            switch (timePeriod) {
                case VitalsConstant.PRINT_1_WEEK:
                    calendar.add(Calendar.DAY_OF_MONTH, -7);
                    break;
                case VitalsConstant.PRINT_2_WEEK:
                    calendar.add(Calendar.DAY_OF_MONTH, -14);
                    break;
                case VitalsConstant.PRINT_1_MONTH:
                    calendar.add(Calendar.MONTH, -1);
                    break;
            }
            for (int i = 0; i < vitalsApiResponseModelArrayList.size(); i++) {
                if (Utils.getDateFromString(vitalsApiResponseModelArrayList.get(i).getCreated_at()).compareTo(calendar.getTime()) >= 0) {
                    pdfList.add(vitalsApiResponseModelArrayList.get(i));
                }
            }
        }

        if (!pdfList.isEmpty()) {
            VitalPdfGenerator vitalPdfGenerator = new VitalPdfGenerator(getActivity());

            boolean isVitalReport = false;
            if (selectedItem.equals(SupportedMeasurementType.bp))
                isVitalReport = true;

            String htmlContent = vitalPdfGenerator.generatePdfFor(pdfList, commonUserApiResponseModel, isVitalReport);

            PdfViewerFragment pdfViewerFragment = new PdfViewerFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ArgumentKeys.HTML_FILE, htmlContent);
            bundle.putString(ArgumentKeys.PDF_TITLE, "VitalsFile");
            pdfViewerFragment.setArguments(bundle);
            showSubFragmentInterface.onShowFragment(pdfViewerFragment);
        } else {
            Utils.showAlertDialog(getActivity(), getString(R.string.alert), "No data available for " + timePeriod, getString(R.string.ok), null,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, null);
        }
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
            vitalDetailCelv.hideEmptyState();
            expandListView();
        } else {
            vitalDetailCelv.showEmptyState();
        }

        vitalDetailCelv.setVisibility(View.VISIBLE);
    }

    private void expandListView() {
        for (int i = 0; i < headerList.size(); i++) {
            expandableListView.expandGroup(i);
        }
    }

    private void makeApiCall(boolean isShowProgress) {
        if (selectedItem != null) {
            String type = selectedItem;
            if (selectedItem.equals(SupportedMeasurementType.bp)) {
                type = type + "," + SupportedMeasurementType.heartRate;
            }
            if (!isFromHome) {
                vitalsApiViewModel.getUserVitals(type, commonUserApiResponseModel.getUser_guid(), doctorGuid, isShowProgress);
            } else {
                vitalsApiViewModel.getVitals(type, isShowProgress);
            }
        }

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
        }
        vitalDetailCelv.setEmptyState(emptyStateType);
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

                if (TokBox.shared.isActiveCallPreset()) {
                    Toast.makeText(getActivity(), getString(R.string.live_call_going_error), Toast.LENGTH_LONG).show();
                    return;
                }

                addFab.setClickable(false);

                proceedAdd(selectedItem);
                break;
            case R.id.info_iv:
                showUserDetailView();
                break;
        }
    }


    private void proceedAdd(String selectedItem) {
        if (UserType.isUserPatient()) {
            Intent intent = new Intent(getActivity(), VitalCreationActivity.class);
            intent.putExtra(ArgumentKeys.SELECTED_VITAL_TYPE, selectedItem);
            getActivity().startActivity(intent);
        } else {
            VitalCreateNewFragment vitalCreateNewFragment = new VitalCreateNewFragment();
            Bundle bundle = getArguments();
            if (bundle == null) {
                bundle = new Bundle();
            }
            if (isAbnormalVitalView) {
                bundle.putSerializable(Constants.USER_DETAIL, commonUserApiResponseModel);
            }
            bundle.putString(ArgumentKeys.SELECTED_VITAL_TYPE, selectedItem);
            bundle.putBoolean(ArgumentKeys.USE_OWN_TOOLBAR, true);
            vitalCreateNewFragment.setArguments(bundle);
            showSubFragmentInterface.onShowFragment(vitalCreateNewFragment);
        }
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
        } else {
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
                            systole = "Systole : " + (int) line1Entry.get(i).getY() + " " + unit;
                            break;
                        }
                    }
                    for (int i = 0; i < line2Entry.size(); i++) {
                        if (line2Entry.get(i).equalTo(e)) {
                            diastole = "Diastole : " + (int) line2Entry.get(i).getY() + " " + unit;
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
