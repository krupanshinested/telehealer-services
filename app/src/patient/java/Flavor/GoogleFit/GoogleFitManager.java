package Flavor.GoogleFit;

import android.app.Activity;
import android.content.Context;

import Flavor.GoogleFit.Interface.GoogleFitResultFetcher;
import Flavor.GoogleFit.Models.GoogleFitData;
import Flavor.GoogleFit.Models.GoogleFitSource;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.HealthDataTypes;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.fragment.app.Fragment;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;

public class GoogleFitManager {
    private final String TAG = "GoogleFitManager";

    public static final int REQUEST_OAUTH_REQUEST_CODE = 1;

    @Nullable
    private Fragment fragment;

    @Nullable
    private Activity activity;
    private Context context;

    @Nullable
    private GoogleFitResultFetcher fetcher;

    private FitnessOptions.Builder fitnesBuilder = FitnessOptions.builder()
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
            .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
            .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_READ)
            .addDataType(HealthDataTypes.TYPE_BODY_TEMPERATURE, FitnessOptions.ACCESS_READ)
            .addDataType(HealthDataTypes.TYPE_OXYGEN_SATURATION, FitnessOptions.ACCESS_READ);

    public GoogleFitManager(Fragment fragment) {
        this.fragment = fragment;
        this.context = fragment.getActivity();
    }

    public GoogleFitManager(Activity activity) {
        this.activity = activity;
        this.context = activity;
    }

    public GoogleFitManager(Context context) {
        this.context = context;
    }

    public void read(GoogleFitResultFetcher fetcher) {
        Log.d(TAG, "read");
        this.fetcher = fetcher;

        if (checkIfHasPermission()) {
            Log.d(TAG, "account alread present");
            readData();
        } else {
            Log.e(TAG, "account not already present");
        }
    }

    public void didFetchedPermissionResult() {
        if (isPermitted()) {
            readData();
        }
    }

    public boolean isPermitted() {
        Log.d(TAG, "isPermitted");
        FitnessOptions fitnessOptions = fitnesBuilder.build();
        return GoogleSignIn.hasPermissions(GoogleSignIn.getAccountForExtension(context,fitnessOptions), fitnessOptions);
    }

    public Intent isChangeInSelectionSource(ArrayList<GoogleFitData> data, ArrayList<String> selectedBundleIds) {
        boolean changeInSelection = false;
        ArrayList<GoogleFitSource> addedSource = new ArrayList<>();
        for (GoogleFitData fitData : data) {

            if (!selectedBundleIds.contains(fitData.getSource().getBundleId())) {
                selectedBundleIds.add(fitData.getSource().getBundleId());
                addedSource.add(fitData.getSource());
                changeInSelection = true;
            }
        }

        Intent intent = new Intent();
        intent.putExtra(ArgumentKeys.GOOGLE_FIT_SOURCE, addedSource);
        intent.putExtra(ArgumentKeys.GOOGLE_FIT_CHANGE, changeInSelection);

        return intent;
    }

    public void requestPermission() {
        checkIfHasPermission();
    }

    private void readData() {
        Log.d(TAG, "readData");
        Task<DataReadResponse> task = readHistoryData();
        if (task == null) {
            Log.d(TAG, "readData null");
            return;
        }
        task.addOnCompleteListener(new OnCompleteListener<DataReadResponse>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<DataReadResponse> task) {
                // Log.d(TAG,task.getResult().getDataSet(DataType.TYPE_WEIGHT).toString());
            }
        });
    }

    private boolean checkIfHasPermission() {
        if (!isPermitted()) {
            FitnessOptions fitnessOptions = fitnesBuilder.build();
            if (fragment != null) {
                GoogleSignIn.requestPermissions(fragment, REQUEST_OAUTH_REQUEST_CODE,GoogleSignIn.getAccountForExtension(context,fitnessOptions), fitnessOptions);
            } else if (activity != null) {
                GoogleSignIn.requestPermissions(activity, REQUEST_OAUTH_REQUEST_CODE,GoogleSignIn.getAccountForExtension(context,fitnessOptions), fitnessOptions);
            }
            return false;
        } else {
            return true;
        }
    }

    @Nullable
    private Task<DataReadResponse> readHistoryData() {
        FitnessOptions fitnessOptions = fitnesBuilder.build();
        GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(context,fitnessOptions);

        if (account == null) {
            Log.d(TAG, "account null");
            return null;
        }

        // Begin by creating the query.
        DataReadRequest readRequest = getReadRequest();

        // Invoke the History API to fetch the data with the query
        return Fitness.getHistoryClient(context, account)
                .readData(readRequest)
                .addOnSuccessListener(
                        new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse dataReadResponse) {
                                printData(dataReadResponse);
                                ArrayList<GoogleFitData> data = getValidFitData(dataReadResponse);
                                if (fetcher != null)
                                    fetcher.didFinishFetch(data);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (fetcher != null)
                                    fetcher.didFailedToFetch(e);
                                Log.e(TAG, "There was a problem reading the data.", e);
                            }
                        });
    }

    private DataReadRequest getReadRequest() {
        Date preFetchDate = GoogleFitDefaults.getPreviousFetchedData();

        DataReadRequest.Builder builder = new DataReadRequest.Builder();

        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();

        long startTime;
        if (preFetchDate != null) {
            cal.setTime(preFetchDate);
            startTime = cal.getTimeInMillis();
        } else {
            cal.add(Calendar.YEAR, -1);
            startTime = cal.getTimeInMillis();
        }

        java.text.DateFormat dateFormat = getDateInstance();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

        builder.setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS);

        builder.read(DataType.TYPE_HEART_RATE_BPM)
                .read(DataType.TYPE_WEIGHT)
                .read(HealthDataTypes.TYPE_BLOOD_PRESSURE)
                .read(HealthDataTypes.TYPE_BLOOD_GLUCOSE)
                .read(HealthDataTypes.TYPE_BODY_TEMPERATURE)
                .read(HealthDataTypes.TYPE_OXYGEN_SATURATION);

        return builder.build();
    }

    private ArrayList<GoogleFitData> getValidFitData(DataReadResponse dataReadResult) {
        ArrayList<GoogleFitData> fitData = new ArrayList<>();

        if (dataReadResult.getBuckets().size() > 0) {
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    fitData.addAll(getFitData(dataSet));
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                fitData.addAll(getFitData(dataSet));
            }
        }

        return fitData;
    }

    private ArrayList<GoogleFitData> getFitData(DataSet dataSet) {
        ArrayList<GoogleFitData> fitData = new ArrayList<>();
        String appName = dataSet.getDataSource().getName();
        String bundleId = dataSet.getDataSource().getAppPackageName();
        for (DataPoint dp : dataSet.getDataPoints()) {

            if (TextUtils.isEmpty(appName)) {
                if (!TextUtils.isEmpty(dp.getDataSource().getName())) {
                    appName = dp.getDataSource().getName();
                } else if (!TextUtils.isEmpty(dp.getOriginalDataSource().getName())) {
                    appName = dp.getOriginalDataSource().getName();
                } else if (bundleId.equals("com.google.android.gms")) {
                    appName = "Google Fit";
                } else {
                    appName = bundleId;
                }
            }

            String type;
            if (dp.getDataType().getName().equals(DataType.TYPE_WEIGHT.getName())) {
                type = SupportedMeasurementType.weight;
            } else if (dp.getDataType().getName().equals(DataType.TYPE_HEART_RATE_BPM.getName())) {
                type = SupportedMeasurementType.heartRate;
            } else if (dp.getDataType().getName().equals(HealthDataTypes.TYPE_BLOOD_PRESSURE.getName())) {
                type = SupportedMeasurementType.bp;
            } else if (dp.getDataType().getName().equals(HealthDataTypes.TYPE_BLOOD_GLUCOSE.getName())) {
                type = SupportedMeasurementType.gulcose;
            } else if (dp.getDataType().getName().equals(HealthDataTypes.TYPE_OXYGEN_SATURATION.getName())) {
                type = SupportedMeasurementType.pulseOximeter;
            } else {
                type = SupportedMeasurementType.temperature;
            }


            Date startTime = new Date(dp.getTimestamp(TimeUnit.MILLISECONDS));

            String value = "";
            if (dp.getDataType().getFields().size() != 0) {
                if (!type.equals(SupportedMeasurementType.bp)) {
                    value = Math.round(dp.getValue(dp.getDataType().getFields().get(0)).asFloat()) + "";
                } else {
                    String bpsys = "", bpdia = "";

                    for (Field field : dp.getDataType().getFields()) {
                        if (field.getName().equals("blood_pressure_systolic")) {
                            bpsys = Math.round(dp.getValue(field).asFloat()) + "";
                        } else if (field.getName().equals("blood_pressure_diastolic")) {
                            bpdia = Math.round(dp.getValue(field).asFloat()) + "";
                        }
                    }

                    value = bpsys + "/" + bpdia;
                }
            }

            GoogleFitSource googleFitSource = new GoogleFitSource(appName, bundleId, false);
            GoogleFitData googleFitData = new GoogleFitData(googleFitSource, type, value, startTime);
            fitData.add(googleFitData);
        }
        return fitData;
    }

    private void printData(DataReadResponse dataReadResult) {
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
        }
    }

    private void dumpDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = getTimeInstance();

        Log.i(TAG, "App name: " + dataSet.getDataSource().getName());
        Log.i(TAG, "App name: " + dataSet.getDataSource().getStreamName());
        Log.i(TAG, "App Package: " + dataSet.getDataSource().getAppPackageName());
        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i(TAG, "Data point:");
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            Log.i(TAG, "\tDate: " + dateFormat.format(dp.getTimestamp(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
            }
        }
    }

}
