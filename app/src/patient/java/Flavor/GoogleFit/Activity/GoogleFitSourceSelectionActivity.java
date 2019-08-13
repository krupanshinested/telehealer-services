package Flavor.GoogleFit.Activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.vitals.CreateVitalApiRequestModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Flavor.GoogleFit.Adapter.GoogleFitSourceAdapter;
import Flavor.GoogleFit.GoogleFitDefaults;
import Flavor.GoogleFit.GoogleFitManager;
import Flavor.GoogleFit.Interface.GoogleFitResultFetcher;
import Flavor.GoogleFit.Models.GoogleFitData;
import Flavor.GoogleFit.Models.GoogleFitSource;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GoogleFitSourceSelectionActivity extends BaseActivity implements View.OnClickListener, GoogleFitResultFetcher {

    private Toolbar toolbar;
    private ImageView closeButton,backIv;
    private TextView toolbarTitle,message_tv;
    private CustomButton submit_btn;
    private CustomRecyclerView container;

    private ArrayList<GoogleFitSource> sources;
    private ArrayList<GoogleFitData> data;

    private GoogleFitManager googleFitManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_fit_selection);

        googleFitManager = new GoogleFitManager(this);
        sources = (ArrayList<GoogleFitSource>) getIntent().getSerializableExtra(ArgumentKeys.GOOGLE_FIT_SOURCE);
        data = (ArrayList<GoogleFitData>) getIntent().getSerializableExtra(ArgumentKeys.GOOGLE_FIT_DATA);

        if (sources == null) {
            sources = new ArrayList<>();
        }

        if (data == null) {
            data = new ArrayList<>();
        }

        initView();
        fillWithData();
    }

    /*
     *
     * Initializing the views
     * */
    private  void initView() {
        toolbar = findViewById(R.id.appbar);
        closeButton = toolbar.findViewById(R.id.close_iv);
        backIv = toolbar.findViewById(R.id.back_iv);
        toolbar.findViewById(R.id.next_tv).setVisibility(View.GONE);
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title);

        submit_btn = findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(this);

        toolbar.setBackground(getDrawable(R.drawable.app_background_gradient));

        message_tv = findViewById(R.id.message_tv);

        toolbarTitle.setTextColor(getColor(R.color.colorWhite));
        toolbarTitle.setText(getString(R.string.manage_sources));

        closeButton.setImageTintList(ColorStateList.valueOf(getColor(R.color.colorWhite)));
        closeButton.setVisibility(View.VISIBLE);
        closeButton.setOnClickListener(this);

        backIv.setVisibility(View.GONE);

        container = findViewById(R.id.container);
        container.setScrollable(false);
        container.getSwipeLayout().setEnabled(false);
    }

    private void fillWithData() {
        if (sources.size() == 0) {
            submit_btn.setVisibility(View.GONE);
            message_tv.setVisibility(View.GONE);
        } else {
            submit_btn.setVisibility(View.VISIBLE);
            message_tv.setVisibility(View.VISIBLE);
        }

        if (sources.size() == 0) {
            showProgressDialog();
            googleFitManager.read(this);
        } else {
            assignAdapter();
        }
    }

    private void assignAdapter() {
        GoogleFitSourceAdapter googleFitSourceAdapter = new GoogleFitSourceAdapter(sources,this);
        container.setLayoutManager(new LinearLayoutManager(GoogleFitSourceSelectionActivity.this));
        container.getRecyclerView().setAdapter(googleFitSourceAdapter);
        container.setEmptyState(EmptyViewConstants.EMPTY_GOOGLE_FIT_SOURCE);
        container.updateView();
        GoogleFitDefaults.setPreviousFetchedSources(sources);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_btn:
                GoogleFitDefaults.setPreviousFetchedSources(sources);

                if (data == null || data.size() == 0) {
                    finish();
                    return;
                }

                ArrayList<String> selectedBundles = new ArrayList<>();
                for (GoogleFitSource source : sources) {
                    if (source.isSelected()) {
                        selectedBundles.add(source.getBundleId());
                    }
                }

                if (selectedBundles.size() == 0) {
                    finish();
                    return;
                }

                ArrayList<VitalsApiResponseModel> vitals = new ArrayList<>();
                for (GoogleFitData data : data) {
                    if (selectedBundles.contains(data.getSource().getBundleId())) {
                        DateFormat outputFormat = new SimpleDateFormat(Utils.UTCFormat);
                        outputFormat.setTimeZone(Utils.UtcTimezone);
                        String date = outputFormat.format(data.getDate());
                        vitals.add(new VitalsApiResponseModel(data.getType(), data.getValue(), data.getSource().getAppName(), VitalsConstant.VITAL_MODE_PATIENT, date, data.getSource().getBundleId()));
                    }
                }
                VitalsApiViewModel vitalsApiViewModel = new VitalsApiViewModel(getApplication());
                CreateVitalApiRequestModel createVitalApiRequestModel = new CreateVitalApiRequestModel(vitals);
                vitalsApiViewModel.createVital(createVitalApiRequestModel,null);
                finish();
                break;
            case R.id.close_iv:
                finish();
                break;
        }
    }

    //GoogleFitResultFetcher
    @Override
    public void didFinishFetch(ArrayList<GoogleFitData> fitData) {
        dismissProgressDialog();

        GoogleFitDefaults.setPreviousFetchedData(new Date());

        if (fitData.size() == 0) {
            return;
        }

        ArrayList<GoogleFitSource> selectedSource = new ArrayList<>();
        ArrayList<String> selectedBundleIds = new ArrayList<>();

        for (GoogleFitSource source : GoogleFitDefaults.getPreviousFetchedSources()) {
            if (source.isSelected()) {
                selectedBundleIds.add(source.getBundleId());
                selectedSource.add(source);
            }
        }

        this.data = fitData;
        Intent val = googleFitManager.isChangeInSelectionSource(fitData, selectedBundleIds);
        ArrayList<GoogleFitSource> newAddedSources = (ArrayList<GoogleFitSource>) val.getSerializableExtra(ArgumentKeys.GOOGLE_FIT_SOURCE);
        if (newAddedSources == null) {
            newAddedSources = new ArrayList<>();
        }
        selectedSource.addAll(newAddedSources);
        this.sources = selectedSource;
        assignAdapter();
    }

    @Override
    public void didFailedToFetch(Exception e) {
        dismissProgressDialog();
        assignAdapter();
    }
}
