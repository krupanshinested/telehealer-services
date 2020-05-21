package com.thealer.telehealer.views.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Constants;

public class VitalThresholdDialogFragment extends VitalThresholdSuccessViewDialogFragment {

    private ImageView closeIV;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_threshold, container, false);
        initView(view);
        return view;
    }

    @Override
    protected void initView(View view) {
        closeIV = (ImageView) view.findViewById(R.id.close_iv);
        super.initView(view);

        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        stopLoaderAnimation(true);
        doneBtn.setVisibility(View.GONE);
        preloaderIv.setVisibility(View.GONE);
        loaderIv.setVisibility(View.VISIBLE);
        titleTv.setVisibility(View.VISIBLE);
    }

    @Override
    protected void animatePreLoader() {

    }
    @Override
    protected void onDataUpdated(Bundle bundle) {
        super.onDataUpdated(bundle);
        if (bundle != null) {
            title = bundle.getString(Constants.SUCCESS_VIEW_TITLE);
            titleTv.setText(title);
        }
        }
}