package com.thealer.telehealer.views.common;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.views.base.BaseFragment;

/**
 * Created by rsekar on 11/15/18.
 */

public class WebViewFragment extends BaseFragment {

    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private WebView webView;

    private OnCloseActionInterface onCloseActionInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        webView = (WebView) view.findViewById(R.id.webView);

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseActionInterface.onClose(false);
            }
        });

        String url = null;

        if (getArguments() != null) {
            String viewTitle = getArguments().getString(ArgumentKeys.VIEW_TITLE);
            url = getArguments().getString(ArgumentKeys.WEB_VIEW_URL);

            toolbarTitle.setText(viewTitle);
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        webView.loadUrl(url);
    }
}
