package com.thealer.telehealer.apilayer.baseapimodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.auth0.android.jwt.JWT;
import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.api.ApiInterface;
import com.thealer.telehealer.apilayer.manager.RetrofitManager;
import com.thealer.telehealer.apilayer.models.signin.SigninApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.pubNub.PubnubUtil;
import com.thealer.telehealer.views.base.BaseViewInterface;
import com.thealer.telehealer.views.common.AppUpdateActivity;
import com.thealer.telehealer.views.common.QuickLoginBroadcastReceiver;
import com.thealer.telehealer.views.quickLogin.QuickLoginActivity;
import com.thealer.telehealer.views.signin.SigninActivity;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

/**
 * Created by Aswin on 08,October,2018
 */
public class BaseApiViewModel extends AndroidViewModel implements LifecycleOwner {

    protected final String TAG = getClass().getSimpleName();

    private static List<BaseViewInterface> baseViewInterfaceList = new ArrayList<>();
    private static boolean isRefreshToken = false;
    private static boolean isQuickLoginReceiverEnabled = false;

    private QuickLoginBroadcastReceiver quickLoginBroadcastReceiver = new QuickLoginBroadcastReceiver() {
        @Override
        public void onQuickLogin(int status) {
            if (isQuickLoginReceiverEnabled) {
                Log.e(TAG, "onQuickLogin: ");
                if (status == ArgumentKeys.AUTH_FAILED) {
                    getApplication().getApplicationContext().startActivity(new Intent(getApplication().getApplicationContext(), SigninActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    isRefreshToken = false;
                    isQuickLoginReceiverEnabled = false;

                    EventRecorder.recordUserSession("Quick_Login_Failed");
                } else {
                    EventRecorder.recordUserSession("Quick_Login_Success");
                    makeRefreshTokenApiCall();
                }
            }
        }
    };

    private void updateListnerStatus() {
        for (int i = 0; i < baseViewInterfaceList.size(); i++) {
            baseViewInterfaceList.get(i).onStatus(true);
            if (i == baseViewInterfaceList.size()) {
                isRefreshToken = false;
                isQuickLoginReceiverEnabled = false;
                baseViewInterfaceList.clear();
            }
        }
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

    public ApiInterface getAuthApiService() {
        return RetrofitManager.getInstance(getApplication()).getAuthApiService();
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
     * TODO: This method will trigger an asyn task to check the auth validation in back and return the response through interface
     *
     * @param baseViewInterface
     */

    public void fetchToken(BaseViewInterface baseViewInterface) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (appPreference.getString(PreferenceConstants.USER_AUTH_TOKEN).isEmpty()) {
                    baseViewInterface.onStatus(true);
                } else {
                    JWT jwt = new JWT(appPreference.getString(PreferenceConstants.USER_AUTH_TOKEN));

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

                    if (isAuthExpired()) {
                        baseViewInterfaceList.add(baseViewInterface);
                        if (!isRefreshToken) {
                            isRefreshToken = true;
                            isQuickLoginReceiverEnabled = true;
                            getApplication().getApplicationContext().startActivity(new Intent(getApplication().getApplicationContext(), QuickLoginActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    } else {
                        if (jwt.isExpired(30 * 60)) {
                            baseViewInterfaceList.add(baseViewInterface);
                            isQuickLoginReceiverEnabled = true;
                            makeRefreshTokenApiCall();
                        } else {
                            baseViewInterface.onStatus(true);
                        }
                    }
                }
            }
        };

        new Handler().post(runnable);

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

    private void makeRefreshTokenApiCall() {
        isRefreshToken = true;
        getAuthApiService()
                .refreshToken(appPreference.getString(PreferenceConstants.USER_REFRESH_TOKEN))
                .compose(applySchedulers())
                .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                    @Override
                    public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                        isRefreshToken = false;
                        SigninApiResponseModel signinApiResponseModel = (SigninApiResponseModel) baseApiResponseModel;
                        if (signinApiResponseModel.isSuccess()) {
                            appPreference.setString(PreferenceConstants.USER_AUTH_TOKEN, signinApiResponseModel.getToken());
                            updateListnerStatus();
                        }
                    }
                });
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
        try {
            HttpException httpException = (HttpException) e;

            ResponseBody errorResponse = ((HttpException) e).response().errorBody();
            String response = new JSONObject(errorResponse.string()).toString();
            ErrorModel errorModel = new Gson().fromJson(response, ErrorModel.class);

            Log.e(TAG, "onError: " + new Gson().toJson(httpException));
            Log.e(TAG, "onError: " + response);

            errorModel.setStatusCode(httpException.code());
            errorModel.setResponse(response);

            switch (httpException.code()) {
                case 400:
                    errorModelLiveData.setValue(errorModel);
                    break;
                case 401:
                    //If server returns 401 then it means, the auth token whatever used for the api call is invalid,
                    // so we need to loggout the user and put to login screen
                    errorModelLiveData.setValue(errorModel);
                    goToSigninActivity();
                    break;
                case 403:
                    errorModelLiveData.setValue(errorModel);
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
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    break;
                default:
                    errorModelLiveData.setValue(errorModel);
                    break;
            }

            isLoadingLiveData.setValue(false);

        } catch (Exception e1) {
            e1.printStackTrace();
            isLoadingLiveData.setValue(false);
            errorModelLiveData.setValue(new ErrorModel(-1, "Oops something went wrong", e1.getMessage()));
        }

        isRefreshToken = false;
        isQuickLoginReceiverEnabled = false;

    }

    private void goToSigninActivity() {
        String email = UserDetailPreferenceManager.getEmail();
        appPreference.deletePreference();
        UserDetailPreferenceManager.setEmail(email);
        PubnubUtil.shared.unsubscribe();

        EventRecorder.updateUserId(null);

        getApplication().startActivity(new Intent(getApplication(), SigninActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public MultipartBody.Part getMultipartFile(String name, String image_path) {
        if (image_path != null) {
            try {
                File file = new File(image_path);
                return MultipartBody.Part.createFormData(name, file.getName(),
                        RequestBody.create(MediaType.parse("image/*"), file));

            } catch (Exception e) {
                return null;
            }
        } else
            return null;
    }

    public int getProgress(boolean isProgressVisibile) {
        int progress = Constants.SHOW_NOTHING;
        if (isProgressVisibile) {
            progress = Constants.SHOW_PROGRESS;
        }
        return progress;
    }


}
