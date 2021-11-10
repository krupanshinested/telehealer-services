package com.thealer.telehealer.apilayer.baseapimodel;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.auth0.android.jwt.JWT;
import com.google.gson.Gson;
import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.api.ApiInterface;
import com.thealer.telehealer.apilayer.manager.RetrofitManager;
import com.thealer.telehealer.apilayer.manager.helper.NoConnectivityException;
import com.thealer.telehealer.apilayer.models.signin.SigninApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.pubNub.PubnubUtil;
import com.thealer.telehealer.views.base.BaseViewInterface;
import com.thealer.telehealer.views.common.AppUpdateActivity;
import com.thealer.telehealer.views.common.QuickLoginBroadcastReceiver;
import com.thealer.telehealer.views.quickLogin.QuickLoginActivity;
import com.thealer.telehealer.views.signin.SigninActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.zelory.compressor.Compressor;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.TeleHealerApplication.application;

/**
 * Created by Aswin on 08,October,2018
 */
public class BaseApiViewModel extends AndroidViewModel implements LifecycleOwner {

    protected final String TAG = getClass().getSimpleName();
    public static final int NETWORK_ERROR_CODE = -10;

    private static List<BaseViewInterface> baseViewInterfaceList = new ArrayList<>();
    private static boolean isRefreshToken = false;
    private static boolean isQuickLoginReceiverEnabled = false;

    private QuickLoginBroadcastReceiver quickLoginBroadcastReceiver = new QuickLoginBroadcastReceiver() {
        @Override
        public void onQuickLogin(int status) {
            if (!appPreference.getBoolean(PreferenceConstants.IS_AUTH_PENDING)) {
                if (isQuickLoginReceiverEnabled) {
                    Log.e(TAG, "onQuickLogin: enabled");
                    if (status == ArgumentKeys.AUTH_FAILED || status == ArgumentKeys.AUTH_CANCELLED) {
                        getApplication().getApplicationContext().startActivity(new Intent(getApplication().getApplicationContext(), SigninActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        isRefreshToken = false;
                        isQuickLoginReceiverEnabled = false;
                        EventRecorder.recordUserSession("Quick_Login_Failed");
                        Log.e(TAG, "onQuickLogin: failed");
                    } else {
                        EventRecorder.recordUserSession("Quick_Login_Success");
                        Log.e(TAG, "onQuickLogin: success");
                        if (!isRefreshToken)
                            makeRefreshTokenApiCall();
                    }
                } else {
                    Log.e(TAG, "onQuickLogin: not enabled");
                }
            }
        }
    };


    /**
     * This method will trigger an asyn task to check the auth validation in back and return the response through interface
     *
     * @param baseViewInterface
     */

    public void fetchToken(BaseViewInterface baseViewInterface) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (appPreference.getString(PreferenceConstants.USER_AUTH_TOKEN).isEmpty()) {
                    Log.e(TAG, "run: auth empty");
                    baseViewInterface.onStatus(true);
                } else {


                    /**
                     * if auth token expired
                     *      show quick login screen
                     *      if quick login success
                     *          proceed api call
                     *      else
                     *          go to login screen
                     * else
                     *      if  auth is > 30 min
                     *          refresh token api call
                     *              on success
                     *                  proceed api call
                     *      else
                     *          proceed api call
                     */
                    Utils.checkIdealTime(getApplication());
                    baseViewInterfaceList.add(baseViewInterface);
                    baseViewInterface.onStatus(true);
                    Log.e(TAG, "run: list size " + baseViewInterfaceList.size());
                }
            }
        };
        new Handler().post(runnable);

    }

    private void handleUnAuth(ErrorModel errorModel) {
        isRefreshToken = false;
        if (!isQuickLoginReceiverEnabled) {
            Log.e(TAG, "run: show quick login " + appPreference.getInt(Constants.QUICK_LOGIN_TYPE));
            isQuickLoginReceiverEnabled = true;
            if (appPreference.getInt(Constants.QUICK_LOGIN_TYPE) == Constants.QUICK_LOGIN_TYPE_NONE ||
                    appPreference.getInt(Constants.QUICK_LOGIN_TYPE) == Constants.QUICK_LOGIN_TYPE_PASSWORD) {
                isQuickLoginReceiverEnabled = false;
                goToSigninActivity();
            } else {
                if (!Utils.isInternetEnabled(application)) {
                    updateListnerStatus();
                    isLoadingLiveData.setValue(false);
                    errorModelLiveData.setValue(new ErrorModel(NETWORK_ERROR_CODE, "No Internet connection", "No Internet connection"));
                } else {
                    try {
                        if (!Constants.DisplayQuickLogin) {
                            Constants.DisplayQuickLogin = true;
                            getApplication().getApplicationContext().startActivity(new Intent( getApplication().getApplicationContext(), QuickLoginActivity.class).putExtra(ArgumentKeys.IS_REFRESH_TOKEN, true));
                        }
                    } catch (Exception e) {
                        getApplication().getApplicationContext().startActivity(new Intent( getApplication().getApplicationContext(), QuickLoginActivity.class).putExtra(ArgumentKeys.IS_REFRESH_TOKEN, true).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                }
            }
        }else{
            if(errorModel.getMessage().equals(getApplication().getString(R.string.str_refresh_token_expired)) ||
                    errorModel.getMessage().equals(getApplication().getString(R.string.str_invalid_refresh_token)))
                goToSigninActivity();
        }
    }


    private void makeRefreshTokenApiCall() {
        Log.e(TAG, "makeRefreshTokenApiCall: api called");
        isRefreshToken = true;
        getAuthApiService()
                .refreshToken(appPreference.getString(PreferenceConstants.USER_REFRESH_TOKEN), false, BuildConfig.VERSION_NAME, true)
                .compose(applySchedulers())
                .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                    @Override
                    public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                        Log.e(TAG, "onSuccess: refreshed token");
                        Utils.updateLastLogin();
                        Utils.storeLastActiveTime();
                        SigninApiResponseModel signinApiResponseModel = (SigninApiResponseModel) baseApiResponseModel;
                        if (signinApiResponseModel.isSuccess()) {
                            appPreference.setString(PreferenceConstants.USER_AUTH_TOKEN, signinApiResponseModel.getToken());
                            updateListnerStatus();
                            new WhoAmIApiViewModel(application).assignWhoAmI();
                        }
                        EventRecorder.updateVersion();
                    }
                });
    }

    private void updateListnerStatus() {
        Log.e(TAG, "updateListnerStatus: list size " + baseViewInterfaceList.size());
        for (int i = 0; i < baseViewInterfaceList.size(); i++) {
            Log.e(TAG, "updateListnerStatus: list " + i);
            baseViewInterfaceList.get(i).onStatus(true);
        }
        baseViewInterfaceList.clear();

        Log.e(TAG, "updateListnerStatus: cleared");
        isRefreshToken = false;
        isQuickLoginReceiverEnabled = false;
    }

    final ObservableTransformer schedulersTransformer =
            observable -> observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

    protected MutableLiveData<ErrorModel> errorModelLiveData = new MutableLiveData<>();

    protected MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();

    public MutableLiveData<Integer> showScreen = new MutableLiveData<>();

    public MutableLiveData<BaseApiResponseModel> baseApiResponseModelMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<ArrayList<BaseApiResponseModel>> baseApiArrayListMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<ArrayList<BaseApiResponseModel>> getArrayListMutableLiveData() {
        return baseApiArrayListMutableLiveData;
    }

    public void setArrayListMutableLiveData(MutableLiveData<ArrayList<BaseApiResponseModel>> arrayListMutableLiveData) {
        this.baseApiArrayListMutableLiveData = arrayListMutableLiveData;
    }

    public MutableLiveData<BaseApiResponseModel> getBaseApiResponseModelMutableLiveData() {
        return baseApiResponseModelMutableLiveData;
    }

    public void setBaseApiResponseModelMutableLiveData(MutableLiveData<BaseApiResponseModel> baseApiResponseModelMutableLiveData) {
        this.baseApiResponseModelMutableLiveData = baseApiResponseModelMutableLiveData;
    }

    public boolean requestInProgress = false;

    public BaseApiViewModel(@NonNull Application application) {
        super(application);
        LocalBroadcastManager.getInstance(application.getApplicationContext()).registerReceiver(quickLoginBroadcastReceiver,
                new IntentFilter(application.getString(R.string.quick_login_broadcast_receiver)));
    }

    public <T> ObservableTransformer<T, T> applySchedulers() {
        return (ObservableTransformer<T, T>) schedulersTransformer;
    }

    public MutableLiveData<Integer> getShowScreen() {
        return showScreen;
    }

    public void setShowScreen(MutableLiveData<Integer> showScreen) {
        this.showScreen = showScreen;
    }

    public MutableLiveData<ErrorModel> getErrorModelLiveData() {
        return errorModelLiveData;
    }

    public MutableLiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public ApiInterface getPublicApiService() {
        return RetrofitManager.getInstance(getApplication()).getPublicApiService();
    }

    public ApiInterface getFoodApiService() {
        return RetrofitManager.getInstance(getApplication()).getFoodApiService();
    }

    public ApiInterface getAuthApiService() {
        return RetrofitManager.getInstance(getApplication()).getAuthApiService();
    }

    public ApiInterface getServiceApi() {
        return RetrofitManager.getInstance(getApplication()).getServiceApi();
    }

    public boolean isRequestInProgress() {
        return requestInProgress;
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }

    /**
     * This class will create and observer for api call response
     *
     * @param <O>
     */
    public abstract class RAObserver<O extends BaseApiResponseModel> implements Observer<O> {

        public int requestWithLoader;

        public RAObserver() {
            this.requestWithLoader = Constants.SHOW_NOTHING;
        }

        public RAObserver(int type) {
            this.requestWithLoader = type;
        }

        @Override
        public void onSubscribe(Disposable d) {
            showScreen.setValue(requestWithLoader);
            isLoadingLiveData.setValue(true);
        }

        @Override
        public void onNext(O response) {
            onSuccess(response);
            isLoadingLiveData.setValue(false);

        }

        @Override
        public void onError(Throwable e) {
            handleError(e);
        }

        @Override
        public void onComplete() {
            requestInProgress = false;
        }

        public abstract void onSuccess(O o);

    }


    public abstract class RAListObserver<O extends BaseApiResponseModel> implements Observer<ArrayList<O>> {
        public int requestWithLoader;

        public RAListObserver() {
            this.requestWithLoader = Constants.SHOW_NOTHING;
        }

        public RAListObserver(int type) {
            this.requestWithLoader = type;
        }

        @Override
        public void onSubscribe(Disposable d) {
            showScreen.setValue(requestWithLoader);
            isLoadingLiveData.setValue(true);
        }

        @Override
        public void onNext(ArrayList<O> os) {
            onSuccess(os);
            isLoadingLiveData.setValue(false);
        }

        @Override
        public void onError(Throwable e) {
            handleError(e);
        }

        @Override
        public void onComplete() {
            requestInProgress = false;
        }

        public abstract void onSuccess(ArrayList<O> data);

    }

    public void handleError(Throwable e) {
        Log.e(TAG, "onError: " + e.getMessage());
        if (e instanceof NoConnectivityException || e instanceof SocketTimeoutException) {
            isLoadingLiveData.setValue(false);
            errorModelLiveData.setValue(new ErrorModel(NETWORK_ERROR_CODE, e.getMessage(), e.getMessage()));
        } else if (e instanceof HttpException) {
            try {
                HttpException httpException = (HttpException) e;

                ResponseBody errorResponse = ((HttpException) e).response().errorBody();
                String response = new JSONObject(errorResponse.string()).toString();
                ErrorModel errorModel = new Gson().fromJson(response, ErrorModel.class);

                Log.e(TAG, "onError: " + new Gson().toJson(httpException));
                Log.e(TAG, "onError: " + response);

                errorModel.setStatusCode(httpException.code());
                errorModel.setResponse(response);
                boolean isE401 = false;
                switch (httpException.code()) {
                    case 400: {
                        errorModelLiveData.setValue(errorModel);
                    }
                    break;
                    case 401:
                        //If server returns 401 then it means, the auth token whatever used for the api call is invalid,
                        // so we need to loggout the user and put to login screen
                        isE401 = true;
                        if (!isRefreshToken) {
                            isQuickLoginReceiverEnabled = true;
                            makeRefreshTokenApiCall();
                        } else {
                            handleUnAuth(errorModel);
                            errorModelLiveData.setValue(errorModel);
                        }


                        break;
                    case 403:
                        errorModelLiveData.setValue(errorModel);
                        if (isRefreshToken) {
                            baseViewInterfaceList.clear();
                            goToSigninActivity();
                        }
                        break;
                    case 500:
                        //If server is down, then will get this error code,here we are checking wheteher there is an active
                        // internet connection if internet connection is good, then it post a notification to update the server status
                        // which store in appdelegate (which used for empty state.)
                        errorModelLiveData.setValue(errorModel);
                        break;
                    case 301:
                        //if client is using the old version that time server will return this error code,need to present the App update
                        // controller screen.
                        getApplication().startActivity(new Intent(getApplication(), AppUpdateActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).putExtra(AppUpdateActivity.EXTRA_IS_HARD_UPDATE, true));
                        break;
                    default:
                        errorModelLiveData.setValue(errorModel);
                        break;
                }
                if (!isE401)
                    isLoadingLiveData.setValue(false);

            } catch (Exception e1) {
                e1.printStackTrace();
                isLoadingLiveData.setValue(false);
                if (e1 instanceof HttpException)
                    errorModelLiveData.setValue(new ErrorModel(-1, e1.getMessage(), e1.getMessage()));
                else
                    errorModelLiveData.setValue(new ErrorModel(-1, getApplication().getString(R.string.something_went_wrong_try_again), getApplication().getString(R.string.something_went_wrong_try_again)));
            }
        } else if (e instanceof UnknownHostException) {
            isLoadingLiveData.setValue(false);
            errorModelLiveData.setValue(new ErrorModel(NETWORK_ERROR_CODE, getApplication().getResources().getString(R.string.network_error), getApplication().getResources().getString(R.string.please_check_your_network_connection)));
        } else {
            isLoadingLiveData.setValue(false);
            errorModelLiveData.setValue(new ErrorModel(-1, e.getMessage(), e.getMessage()));
        }
//        isQuickLoginReceiverEnabled = false;

    }

    public static Boolean isAuthExpired() {
        try {
            JWT jwt = new JWT(appPreference.getString(PreferenceConstants.USER_AUTH_TOKEN));
            Date date = new Date();
            return date.compareTo(jwt.getExpiresAt()) >= 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    private void goToSigninActivity() {
        if (baseViewInterfaceList.size() != 0) {
            baseViewInterfaceList.clear();
            UserDetailPreferenceManager.invalidateUser();
            PubnubUtil.shared.unsubscribe();

            EventRecorder.updateUserId(null);

            getApplication().startActivity(new Intent(getApplication(), SigninActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }

    public MultipartBody.Part getMultipartFile(String name, String image_path) {
        if (image_path != null) {
            try {
                File file = new File(image_path);
                Log.e(TAG, "getMultipartFile: " + file.length() + " " + getReadableFileSize(file.length()));

                file = getCompressedFile(file);

                Log.e(TAG, "endFile: " + file.length() + " " + getReadableFileSize(file.length()));

                return MultipartBody.Part.createFormData(name, file.getName(),
                        RequestBody.create(MediaType.parse("image/*"), file));

            } catch (Exception e) {
                return null;
            }
        } else
            return null;
    }

    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public File getCompressedFile(File actualFile) {
        long fileMaxSize = 2000000; // 2MB

        if (actualFile.length() <= fileMaxSize) {
            return actualFile;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(actualFile.getPath());
        int actualWidth = bitmap.getWidth();
        int actualHeight = bitmap.getHeight();
        double startPoint = 1;
        Log.e(TAG, "getCompressedFile: " + bitmap.getWidth() + " " + bitmap.getHeight());

        try {
            File file = null;

            while (startPoint > 0.1) {
                startPoint = startPoint - 0.1;
                int compressWidth = (int) (actualWidth * startPoint);
                int compressHeight = (int) (actualHeight * startPoint);

                Log.e(TAG, "getCompressedFile: " + startPoint + " " + compressWidth + " " + compressHeight);

                file = new Compressor(getApplication())
                        .setMaxWidth(compressWidth)
                        .setMaxHeight(compressHeight)
                        .compressToFile(actualFile);

                Log.e(TAG, "compressed file: " + file.length() + " " + getReadableFileSize(file.length()));
                if (file.length() <= fileMaxSize) {
                    return file;
                }

            }

            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public MultipartBody.Part getTxtMultipartFile(String name, String file_path) {
        if (file_path != null) {
            try {
                File file = new File(file_path);
                return MultipartBody.Part.createFormData(name, file.getName(),
                        RequestBody.create(MediaType.parse("text/plain"), file));

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public int getProgress(boolean isProgressVisibile) {
        int progress = Constants.SHOW_NOTHING;
        if (isProgressVisibile) {
            progress = Constants.SHOW_PROGRESS;
        }
        return progress;
    }


}
