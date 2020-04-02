package com.thealer.telehealer.views.signup.doctor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

/**
 * Created by Aswin on 29,October,2018
 */
public class DoctorRegistrationInfoFragment extends BaseFragment implements DoCurrentTransactionInterface {

    private TextView titleTv;
    private WebView releaseInfoWv;
    private TextView agreementTv;
    private CustomButton acceptBtn;
    private TextView contactTv;

    private OnActionCompleteInterface onActionCompleteInterface;
    private OnViewChangeInterface onViewChangeInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_registration_info, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        titleTv = (TextView) view.findViewById(R.id.title_tv);
        releaseInfoWv = (WebView) view.findViewById(R.id.release_info_wv);
        agreementTv = (TextView) view.findViewById(R.id.agreement_tv);
        acceptBtn = (CustomButton) view.findViewById(R.id.accept_btn);
        contactTv = (TextView) view.findViewById(R.id.contact_tv);

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(ArgumentKeys.HEADER, getResources().getString(R.string.terms_and_conditions));
                bundle.putString(ArgumentKeys.PAGEHINT, getResources().getString(R.string.terms_and_conditions_info));
                bundle.putString(ArgumentKeys.URL, getResources().getString(R.string.terms_and_conditions_url));

                onActionCompleteInterface.onCompletionResult(null, true, bundle);
            }
        });

        if (isDeviceXLarge() && isModeLandscape())
            agreementTv.setVisibility(View.GONE);
        else
            agreementTv.setVisibility(View.VISIBLE);


        contactTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.contact_url)));
                startActivity(intent);
            }
        });

        String url = getResources().getString(R.string.release_info_url);

        releaseInfoWv = (WebView) view.findViewById(R.id.release_info_wv);

        releaseInfoWv.getSettings().setJavaScriptEnabled(true);
        releaseInfoWv.setWebViewClient(new WebViewClient());
        releaseInfoWv.setWebChromeClient(new WebChromeClient());
        releaseInfoWv.loadUrl(url);

        onViewChangeInterface.hideOrShowNext(false);

    }

    @Override
    public void doCurrentTransaction() {

    }
}
