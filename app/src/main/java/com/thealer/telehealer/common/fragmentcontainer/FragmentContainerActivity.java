package com.thealer.telehealer.common.fragmentcontainer;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.thealer.telehealer.R;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;

public class FragmentContainerActivity extends BaseActivity implements ChangeTitleInterface, OnCloseActionInterface, AttachObserverInterface {

    public static final String EXTRA_FRAGMENT = "FragmentClassNamae";
    public static final String EXTRA_BUNDLE = "fragmentArgs";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_SHOW_TOOLBAR = "show_toolbar";

    private TextView toolbarTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        String name = getIntent().getStringExtra(EXTRA_FRAGMENT);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        Bundle bundle = getIntent().getBundleExtra(EXTRA_BUNDLE);
        boolean isShowToolbar = getIntent().getBooleanExtra(EXTRA_SHOW_TOOLBAR, true);
        if (!isShowToolbar)
            findViewById(R.id.appbar).setVisibility(View.GONE);
        else {
            toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
            findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        try {
            Class className = Class.forName(name);
            BaseFragment object = (BaseFragment) className.newInstance();
            object.setArguments(bundle);
            makeFragmentVisible(object, title);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

    }

    public void makeFragmentVisible(Fragment fragmentToShow, String title) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            fragmentManager.popBackStack();
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragmentContainer, fragmentToShow, title);
        ft.addToBackStack(title);
        ft.commit();
    }


    @Override
    public void onTitleChange(String title) {
        if (title != null)
            toolbarTitle.setText(title);
    }

    @Override
    public void onClose(boolean isRefreshRequired) {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
