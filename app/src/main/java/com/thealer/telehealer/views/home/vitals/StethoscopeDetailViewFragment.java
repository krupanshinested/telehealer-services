package com.thealer.telehealer.views.home.vitals;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.vitals.StethBean;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.home.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 25,March,2019
 */
public class StethoscopeDetailViewFragment extends BaseFragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;

    private OnCloseActionInterface onCloseActionInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vital_detail, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);

        toolbarTitle.setText(getString(R.string.stethIO));
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseActionInterface.onClose(false);
            }
        });

        if (getArguments() != null) {
            VitalsApiResponseModel vitalsApiResponseModel = (VitalsApiResponseModel) getArguments().getSerializable(ArgumentKeys.VITAL_DETAIL);

            if (vitalsApiResponseModel != null) {
                List<Fragment> fragmentList = new ArrayList<>();
                List<String> titleList = new ArrayList<>();

                StethBean stethBean = vitalsApiResponseModel.getStethBean();
                for (int i = 0; i < stethBean.getSegments().size(); i++) {

                    StethoscopeSegmentDetailFragment stethoscopeSegmentDetailFragment = new StethoscopeSegmentDetailFragment();

                    Bundle bundle = new Bundle();

                    bundle.putSerializable(ArgumentKeys.SEGMENT_DETAIL, stethBean.getSegments().get(i));
                    bundle.putInt(ArgumentKeys.SEGMENT, i);
                    bundle.putString(ArgumentKeys.SELECTED_DATE, vitalsApiResponseModel.getCreated_at());

                    stethoscopeSegmentDetailFragment.setArguments(bundle);

                    fragmentList.add(stethoscopeSegmentDetailFragment);
                    titleList.add("Segment " + i);
                }

                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), fragmentList, titleList);
                viewPager.setAdapter(viewPagerAdapter);

                tabLayout.setupWithViewPager(viewPager);
            }
        }
    }
}
