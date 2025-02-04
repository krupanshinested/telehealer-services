package com.thealer.telehealer.apilayer.manager;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.api.ApiInterface;
import com.thealer.telehealer.apilayer.manager.helper.ConnectivityInterceptor;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.PreferenceConstants;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.thealer.telehealer.TeleHealerApplication.appConfig;

/**
 * Created by Aswin on 08,October,2018
 */

public class RetrofitManager extends ContextWrapper {

    private static RetrofitManager mInstance;
    private Retrofit defaultRetrofit;
    private Retrofit authRetrofit;
    private Retrofit foodRetrofit;
    private Retrofit serviceRetrofit;


    private RetrofitManager(Context base) {
        super(base);
    }

    public static synchronized RetrofitManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RetrofitManager(context.getApplicationContext());
        }
        return mInstance;
    }

    public void logout() {
        authRetrofit = null;
    }

    private Retrofit getDefaultRetrofit() {
        if (defaultRetrofit == null) {
            createDefaultRetrofit();
        }
        return defaultRetrofit;
    }

    private Retrofit getServiceRetrofit() {
        if (serviceRetrofit == null) {
            createServiceRetrofit();
        }
        return serviceRetrofit;
    }

    private Retrofit getAuthRetrofit() {
        if (authRetrofit == null) {
            createAuthRetrofit();
        }
        return authRetrofit;
    }

    private Retrofit getFoodRetrofit() {
        if (foodRetrofit == null) {
            createFoodRetrofit();
        }
        return foodRetrofit;
    }

    private void createDefaultRetrofit() {

        OkHttpClient.Builder httpClient = getHttpBuilder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .header("Accept-Language", Locale.getDefault().getLanguage())
                        .header("User-Agent", "android")
                        .header("X-Install-Type", appConfig.getInstallType())
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);

            }
        });

        OkHttpClient client = httpClient.build();
        defaultRetrofit = new Retrofit.Builder().baseUrl(getBaseContext().getString(R.string.api_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
    }

    private void createAuthRetrofit() {
        OkHttpClient.Builder httpClient = getHttpBuilder();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            String token = TeleHealerApplication.appPreference.getString(PreferenceConstants.USER_AUTH_TOKEN);
            Log.d("TAG", "createAuthRetrofit: "+token);
            Request request = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("User-Agent", "android")
                    .header("X-Install-Type", appConfig.getInstallType())
                    .header("Accept-Language", Locale.getDefault().getLanguage())
                    .header(Constants.HEADER_AUTH_TOKEN, token)
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        });

        OkHttpClient client = httpClient.build();
        authRetrofit = new Retrofit.Builder().baseUrl(getBaseContext().getString(R.string.api_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
    }

    private void createServiceRetrofit() {

        OkHttpClient.Builder httpClient = getHttpBuilder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .header("User-Agent", "android")
                        .header("X-Install-Type", appConfig.getInstallType())
                        .header("Accept-Language", Locale.getDefault().getLanguage())
                        .header("x-api-key", getBaseContext().getString(R.string.service_api_key))
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);

            }
        });


        OkHttpClient client = httpClient.build();
        serviceRetrofit = new Retrofit.Builder().baseUrl(getBaseContext().getString(R.string.service_api_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
    }

    private void createFoodRetrofit() {

        OkHttpClient.Builder httpClient = getHttpBuilder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);

            }
        });

        OkHttpClient client = httpClient.build();
        foodRetrofit = new Retrofit.Builder().baseUrl(getBaseContext().getString(R.string.food_api_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
    }

    private OkHttpClient.Builder getHttpBuilder() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .addInterceptor(new ConnectivityInterceptor(getApplicationContext()));
        httpClient.connectTimeout(60, TimeUnit.SECONDS);
        httpClient.readTimeout(60, TimeUnit.SECONDS);
        httpClient.writeTimeout(60, TimeUnit.SECONDS);

        RetrofitLogger logging = new RetrofitLogger();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);

        return httpClient;
    }

    public void clearAll() {
        mInstance = null;
        defaultRetrofit = null;
        authRetrofit = null;
    }

    public ApiInterface getPublicApiService() {
        return getDefaultRetrofit().create(ApiInterface.class);
    }

    public ApiInterface getServiceApi() {
        return getServiceRetrofit().create(ApiInterface.class);
    }

    public ApiInterface getAuthApiService() {
        return getAuthRetrofit().create(ApiInterface.class);
    }

    public ApiInterface getFoodApiService() {
        return getFoodRetrofit().create(ApiInterface.class);
    }
}
