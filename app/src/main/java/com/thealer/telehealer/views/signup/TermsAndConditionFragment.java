package com.thealer.telehealer.views.signup;

import android.content.Context;
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
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;

/**
 * Created by Aswin on 12,October,2018
 */
public class TermsAndConditionFragment extends BaseFragment implements DoCurrentTransactionInterface {
    private CustomButton acceptBtn;
    private String url;
    private WebView webView;
    private TextView pageHintTv;
    private OnActionCompleteInterface onActionCompleteInterface;
    private OnViewChangeInterface onViewChangeInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        acceptBtn = (CustomButton) view.findViewById(R.id.accept_btn);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionCompleteInterface.onCompletionResult(null, true, null);
            }
        });
        pageHintTv = (TextView) view.findViewById(R.id.page_hint_tv);

        if (isDeviceXLarge() && isModeLandscape())
            pageHintTv.setVisibility(View.GONE);
        else
            pageHintTv.setVisibility(View.VISIBLE);

        url = getResources().getString(R.string.terms_and_conditions_url);

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
