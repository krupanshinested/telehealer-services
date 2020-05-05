package com.thealer.telehealer.views.signup;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.lifecycle.ViewModelProviders;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;

/**
 * Created by Aswin on 12,October,2018
 */
public class AgreementFragment extends SignupBaseFragment implements DoCurrentTransactionInterface {
    private CustomButton acceptBtn;
    private String url;
    private WebView webView;
    private TextView pageHintTv,headerTV;
    private OnActionCompleteInterface onActionCompleteInterface;
    private OnViewChangeInterface onViewChangeInterface;
    private String header,pageHint;
    private boolean isNoticetoConsumer;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            header = getArguments().getString(ArgumentKeys.HEADER);
            pageHint = getArguments().getString(ArgumentKeys.PAGEHINT);
            url = getArguments().getString(ArgumentKeys.URL);
            isNoticetoConsumer = getArguments().getBoolean(ArgumentKeys.IS_NOTICE_TO_CONSUMER,false);
        }
        View view = inflater.inflate(R.layout.fragment_termsandconditions, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
    }

    private void initView(View view) {
        headerTV = (TextView) view.findViewById(R.id.textView);
        acceptBtn = (CustomButton) view.findViewById(R.id.accept_btn);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(ArgumentKeys.HEADER, getResources().getString(R.string.notice_to_consumer));
                bundle.putString(ArgumentKeys.PAGEHINT, getResources().getString(R.string.notice_to_consumer_info));
                bundle.putString(ArgumentKeys.URL, getResources().getString(R.string.notice_to_consumers_url));
                bundle.putBoolean(ArgumentKeys.IS_NOTICE_TO_CONSUMER, true);

                if (isNoticetoConsumer) {
                    CreateUserRequestModel createUserRequestModel = ViewModelProviders.of(getActivity()).get(CreateUserRequestModel.class);
                    postPatientDetail(createUserRequestModel);
                } else {
                    onActionCompleteInterface.onCompletionResult(null, true, bundle);
                }
            }
        });
        pageHintTv = (TextView) view.findViewById(R.id.page_hint_tv);
        headerTV.setText(header);
        pageHintTv.setText(pageHint);
        if (isDeviceXLarge() && isModeLandscape())
            pageHintTv.setVisibility(View.GONE);
        else
            pageHintTv.setVisibility(View.VISIBLE);

        webView = (WebView) view.findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        webView.loadUrl(url);

        onViewChangeInterface.hideOrShowNext(false);

    }

    @Override
    public void doCurrentTransaction() {
    }
}
