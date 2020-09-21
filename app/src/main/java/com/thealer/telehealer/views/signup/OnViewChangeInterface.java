package com.thealer.telehealer.views.signup;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;

/**
 * Created by Aswin on 11,October,2018
 */
public interface OnViewChangeInterface {

    void enableNext(boolean enabled);

    void hideOrShowNext(boolean hideOrShow);

    void hideOrShowClose(boolean hideOrShow);

    void hideOrShowToolbarTile(boolean hideOrShow);

    void hideOrShowBackIv(boolean hideOrShow);

    void attachObserver(BaseApiViewModel baseApiViewModel);

    void updateNextTitle(String nextTitle);

    void updateTitle(String title);
}
