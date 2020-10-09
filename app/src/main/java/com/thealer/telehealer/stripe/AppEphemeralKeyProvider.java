package com.thealer.telehealer.stripe;

import android.util.Log;

import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;
import com.thealer.telehealer.apilayer.api.ApiInterface;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AppEphemeralKeyProvider implements EphemeralKeyProvider {

    private ApiInterface apiInterface;

    private final CompositeDisposable compositeDisposable =
            new CompositeDisposable();

    public AppEphemeralKeyProvider(ApiInterface apiInterface) {
        this.apiInterface = apiInterface;
    }


    @Override
    public void createEphemeralKey(@NotNull String apiVersion, @NotNull EphemeralKeyUpdateListener ephemeralKeyUpdateListener) {
        compositeDisposable.add(
                apiInterface.getEphemeralKey(apiVersion).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> {
                                    try {
                                        final String rawKey = response.string();
                                        ephemeralKeyUpdateListener.onKeyUpdate(rawKey);
                                    } catch (IOException ignored) {
                                    }
                                }));

    }
}
