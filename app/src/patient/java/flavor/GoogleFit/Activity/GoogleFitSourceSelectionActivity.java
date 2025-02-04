package flavor.GoogleFit.Activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.Util.Vital.BulkVitalUtil;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import flavor.GoogleFit.Adapter.GoogleFitSourceAdapter;
import flavor.GoogleFit.GoogleFitDefaults;
import flavor.GoogleFit.GoogleFitManager;
import flavor.GoogleFit.Interface.GoogleFitResultFetcher;
import flavor.GoogleFit.Interface.GoogleFitSourceInterface;
import flavor.GoogleFit.Models.GoogleFitData;
import flavor.GoogleFit.Models.GoogleFitSource;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

public class GoogleFitSourceSelectionActivity extends BaseActivity implements View.OnClickListener, GoogleFitResultFetcher {

    private Toolbar toolbar;
    private ImageView closeButton,backIv;
    private TextView toolbarTitle,message_tv;
    private Button submit_btn;
    private CustomRecyclerView container;

    private ArrayList<GoogleFitSource> sources = new ArrayList<>();
    private ArrayList<GoogleFitSource> previousFetchedSources = new ArrayList<>();
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

        Collections.sort(sources);

        if (data == null) {
            data = new ArrayList<>();
        }

        initView();
        fillWithData();
        updateSubmitButton();
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

    private void updateSubmitButton() {
        ArrayList<GoogleFitSource> previousFetchedSources = this.previousFetchedSources;

        if (previousFetchedSources.size() != sources.size()) {
            submit_btn.setEnabled(true);
            Log.d("Googlefit","updateSubmitButton true");
            return;
        } else {
            Collections.sort(previousFetchedSources);
            for (int i = 0;i<previousFetchedSources.size();i++) {
                if (previousFetchedSources.get(i).getBundleId().equals(sources.get(i).getBundleId()) && previousFetchedSources.get(i).isSelected() != sources.get(i).isSelected()) {
                    submit_btn.setEnabled(true);
                    Log.d("Googlefit","updateSubmitButton true");
                    return;
                }
            }
        }

        submit_btn.setEnabled(false);
        Log.d("Googlefit","updateSubmitButton false");
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
        googleFitSourceAdapter.setFitSourceInterface(new GoogleFitSourceInterface() {
            @Override
            public void didSourceChanged() {
                updateSubmitButton();
            }
        });
        previousFetchedSources = GoogleFitDefaults.getPreviousFetchedSources();
        GoogleFitDefaults.setPreviousFetchedSources(sources);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_iv:
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

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        BulkVitalUtil.getInstance().uploadAllVitals(1,vitals,null,null,null);
                    }
                });

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
