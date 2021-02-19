package com.thealer.telehealer.common.fragmentcontainer;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.thealer.telehealer.R;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.base.BaseFragment;

public class FragmentContainerActivity extends BaseActivity {

    public static final String EXTRA_FRAGMENT = "FragmentClassNamae";
    public static final String EXTRA_BUNDLE = "fragmentArgs";
    public static final String EXTRA_TITLE = "title";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        String name = getIntent().getStringExtra(EXTRA_FRAGMENT);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        Bundle bundle = getIntent().getBundleExtra(EXTRA_BUNDLE);

        try {
            Class className = Class.forName(name);
            BaseFragment object = (BaseFragment) className.newInstance();
            object.setArguments(bundle);
            makeFragmentVisible(object, title);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
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


}
