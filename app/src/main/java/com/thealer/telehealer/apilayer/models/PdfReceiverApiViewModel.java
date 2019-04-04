package com.thealer.telehealer.apilayer.models;

import android.app.Application;
import android.support.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;

/**
 * Created by Aswin on 05,December,2018
 */
public class PdfReceiverApiViewModel extends BaseApiViewModel {

    public PdfReceiverApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void getPdfFile(String fileUrl, boolean isPdfDecrypt, FileSavedInterface fileSavedInterface) {

        File directory = getApplication().getApplicationContext().getExternalFilesDir("data/.files");

        if (directory != null) {
            if (!directory.exists()) {
                try {
                    directory.mkdirs();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String filePath = directory.getAbsolutePath().concat("tHealer.pdf");

            File file = new File(filePath);

            fetchToken(new BaseViewInterface() {
                @Override
                public void onStatus(boolean status) {

                    if (status) {
                        isLoadingLiveData.setValue(true);
                        getAuthApiService().getPdfFile(fileUrl, isPdfDecrypt)
                                .compose(applySchedulers())
                                .subscribe(new Observer<Response<ResponseBody>>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                    }

                                    @Override
                                    public void onNext(Response<ResponseBody> response) {
                                        try {
                                            BufferedSink sink = Okio.buffer(Okio.sink(file));
                                            if (response.body() != null) {
                                                sink.writeAll(response.body().source());
                                            }
                                            sink.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        handleError(e);

                                    }

                                    @Override
                                    public void onComplete() {
                                        fileSavedInterface.onFileSaved(file);
                                        isLoadingLiveData.setValue(false);
                                    }
                                });
                    }
                }
            });
        }
    }
}

