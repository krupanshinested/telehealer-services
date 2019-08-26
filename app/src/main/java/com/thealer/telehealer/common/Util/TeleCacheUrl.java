package com.thealer.telehealer.common.Util;

import android.text.TextUtils;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;

import java.net.URL;

public class TeleCacheUrl extends GlideUrl {
    public TeleCacheUrl(String url) {
        super(url);
    }

    public TeleCacheUrl(String url, Headers headers) {
        super(url, headers);
    }

    public TeleCacheUrl(URL url) {
        super(url);
    }

    public TeleCacheUrl(URL url, Headers headers) {
        super(url, headers);
    }

    @Override
    public String getCacheKey() {
        String url = toStringUrl();
        if (TextUtils.isEmpty(url)) {
            return "";
        } else if (url.contains("?path=/assets")) {
            return url;
        } else if (url.contains("?")) {
            return url.substring(0, url.lastIndexOf("?"));
        } else {
            return url;
        }
    }
}
