package com.thealer.telehealer.views.home.vitals;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
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
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomExpandableListView;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.CustomDialogs.ItemPickerDialog;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.PdfViewerFragment;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import iHealth.pairing.VitalCreationActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

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
    private CommonUserApiResponseModel commonUserApiResponseModel;
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
    private Toolbar toolbar;
    private ArrayList<VitalsApiResponseModel> vitalsApiResponseModelArrayList;

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
                if (baseApiResponseModels != null) {

                    vitalsApiResponseModelArrayList = (ArrayList<VitalsApiResponseModel>) (Object) baseApiResponseModels;

                    updateList(vitalsApiResponseModelArrayList);

                    updateChart(vitalsApiResponseModelArrayList);
                }
            }
        });
    }

    private void updateChart(ArrayList<VitalsApiResponseModel> vitalsApiResponseModelArrayList) {
        if (vitalsApiResponseModelArrayList.size() > 0) {

            linechart.setTouchEnabled(true);
            linechart.getDescription().setEnabled(false);
            linechart.setDrawGridBackground(false);

            line1Entry = new ArrayList<>();
            xaxisLables = new HashMap<>();
            List<ILineDataSet> lineDataSetList = new ArrayList<>();
            line2Entry = new ArrayList<>();
            line1Entry.add(new Entry(0, 0));
            line2Entry.add(new Entry(0, 0));

            String dataSet1Name = getString(SupportedMeasurementType.getTitle(selectedItem));

            float maxVlaue = 0;
            float minValue = 0;


            XAxis xAxis = linechart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setSpaceMin(1f);
            xAxis.setAxisMinimum(1f);
            xAxis.setSpaceMax(1f);
            xAxis.setDrawAxisLine(false);
            xAxis.setTextColor(Color.WHITE);
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
            yAxis.setTextColor(Color.WHITE);
            yAxis.setDrawAxisLine(false);
            linechart.getAxisLeft().setEnabled(true);
            linechart.getAxisRight().setEnabled(false);

            yAxis.setAxisMinimum(0f);


            for (int i = 0; i < vitalsApiResponseModelArrayList.size(); i++) {

                String value = vitalsApiResponseModelArrayList.get(i).getValue()
                        .replace(SupportedMeasurementType.getVitalUnit(selectedItem), "").trim();

                if (selectedItem.equals(SupportedMeasurementType.bp)) {
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

                    line1Entry.add(new Entry(i + 1, Float.parseFloat(values[0])));
                    line2Entry.add(new Entry(i + 1, Float.parseFloat(values[1])));
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
                    line1Entry.add(new Entry(i + 1, Float.parseFloat(value)));
                }

                xaxisLables.put(Float.valueOf(i + 1), vitalsApiResponseModelArrayList.get(i).getCreated_at());
            }

            yAxis.calculate(maxVlaue, minValue);


            if (selectedItem.equals(SupportedMeasurementType.bp)) {
                dataSet1Name = VitalsConstant.INPUT_SYSTOLE;
                String dataSet2Name = VitalsConstant.INPUT_DIASTOLE;
                LineDataSet lineDataSet2 = new LineDataSet(line2Entry, dataSet2Name);
                lineDataSet2.setCircleColor(Color.BLACK);
                lineDataSet2.setColor(Color.BLACK);
                lineDataSet2.setLineWidth(2f);
                lineDataSet2.setDrawValues(false);
                lineDataSet2.setCircleRadius(4f);
                lineDataSet2.setDrawCircleHole(false);
                lineDataSet2.setValueTextColor(Color.WHITE);
                lineDataSet2.setDrawHighlightIndicators(false);

                lineDataSetList.add(lineDataSet2);
            }

            LineDataSet lineDataSet1 = new LineDataSet(line1Entry, dataSet1Name);
            lineDataSet1.setCircleColor(Color.WHITE);
            lineDataSet1.setColor(Color.WHITE);
            lineDataSet1.setLineWidth(2f);
            lineDataSet1.setCircleRadius(4f);
            lineDataSet1.setDrawValues(false);
            lineDataSet1.setDrawCircleHole(false);
            lineDataSet1.setDrawHighlightIndicators(false);

            lineDataSetList.add(lineDataSet1);

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
            linechart.setNoDataTextColor(getActivity().getColor(R.color.colorWhite));
            linechart.getLegend().setTextColor(Color.WHITE);

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

        toolbar.inflateMenu(R.menu.orders_detail_menu);
        toolbar.getMenu().removeItem(R.id.send_fax_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.print_menu:
                        ItemPickerDialog itemPickerDialog = new ItemPickerDialog(getActivity(), "Choose the time period", VitalsConstant.vitalPrintOptions, new PickerListener() {
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

        String emptyStateType = "";

        if (getArguments() != null) {

            selectedItem = getArguments().getString(ArgumentKeys.MEASUREMENT_TYPE);

            isFromHome = getArguments().getBoolean(Constants.IS_FROM_HOME);

            if (!isFromHome) {
                commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
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

        VitalPdfGenerator vitalPdfGenerator = new VitalPdfGenerator(getActivity());
        String htmlContent = vitalPdfGenerator.generatePdfFor(pdfList, commonUserApiResponseModel);

        PdfViewerFragment pdfViewerFragment = new PdfViewerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ArgumentKeys.HTML_FILE, htmlContent);
        bundle.putString(ArgumentKeys.PDF_TITLE, "VitalsFile");
        pdfViewerFragment.setArguments(bundle);
        showSubFragmentInterface.onShowFragment(pdfViewerFragment);
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
    }

    private void expandListView() {
        for (int i = 0; i < headerList.size(); i++) {
            expandableListView.expandGroup(i);
        }
    }

    private void makeApiCall() {

        if (selectedItem != null) {
            if (!isFromHome) {
                vitalsApiViewModel.getUserVitals(selectedItem, commonUserApiResponseModel.getUser_guid());
            } else {
                vitalsApiViewModel.getVitals(selectedItem);
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
                addFab.setClickable(false);

                if (UserType.isUserPatient()) {
                    Intent intent = new Intent(getActivity(), VitalCreationActivity.class);
                    intent.putExtra(ArgumentKeys.MEASUREMENT_TYPE, selectedItem);
                    getActivity().startActivity(intent);
                } else {
                    VitalCreateNewFragment vitalCreateNewFragment = new VitalCreateNewFragment();
                    Bundle bundle = getArguments();
                    if (bundle == null) {
                        bundle = new Bundle();
                    }
                    bundle.putString(ArgumentKeys.MEASUREMENT_TYPE, selectedItem);
                    bundle.putBoolean(ArgumentKeys.USE_OWN_TOOLBAR, true);
                    vitalCreateNewFragment.setArguments(bundle);
                    showSubFragmentInterface.onShowFragment(vitalCreateNewFragment);
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        addFab.setClickable(true);
        makeApiCall();
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

            String value;
            String unit = SupportedMeasurementType.getVitalUnit(selectedItem);

            if (selectedItem.equals(SupportedMeasurementType.bp)) {
                String systole = "Systole : " + (int) line1Entry.get((int) e.getX()).getY() + " " + unit;
                String diastole = "Diastole : " + (int) line2Entry.get((int) e.getX()).getY() + " " + unit;

                value = systole + "\n" + diastole;

            } else {
                value = String.valueOf(line1Entry.get((int) e.getX()).getY()) + " " + unit;
            }
            valueTv.setText(value);
            dateTv.setText(Utils.getDayMonthTime(xaxisLables.get(e.getX())));
            super.refreshContent(e, highlight);
        }
    }
}
