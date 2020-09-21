package com.thealer.telehealer.views.common;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

/**
 * Created by rsekar on 11/15/18.
 */

public class WebViewFragment extends BaseFragment {

    static String urlKey = "urlKey";
    static String titleKey = "titleKey";

    public String urlToLoad;
    public String title;

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);

        if (savedInstanceState != null) {
            title = savedInstanceState.getString(WebViewFragment.titleKey);
            urlToLoad = savedInstanceState.getString(WebViewFragment.urlKey);
        }

        WebView webView = view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        webView.loadUrl(urlToLoad);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(WebViewFragment.titleKey, title);
        outState.putString(WebViewFragment.urlKey, urlToLoad);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() instanceof OnViewChangeInterface) {
            ((OnViewChangeInterface) getActivity()).updateTitle(title);
        }
    }

}
