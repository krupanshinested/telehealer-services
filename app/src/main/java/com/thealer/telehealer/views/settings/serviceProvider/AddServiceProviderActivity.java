package com.thealer.telehealer.views.settings.serviceProvider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.views.common.OnCloseActionInterface;

public class AddServiceProviderActivity extends AppCompatActivity {

    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private TextView nextTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service_provider);
        initView();
    }

    private void initView() {

        appbarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backIv = (ImageView) findViewById(R.id.back_iv);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        nextTv = (TextView) findViewById(R.id.next_tv);

        appbarLayout.setVisibility(View.VISIBLE);
        appbarLayout.setBackgroundColor(android.R.color.transparent);
        backIv.setColorFilter(this.getColor(R.color.app_gradient_start));
        nextTv.setTextColor(this.getColor(R.color.app_gradient_start));
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        nextTv.setText(this.getString(R.string.save));
        nextTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

}