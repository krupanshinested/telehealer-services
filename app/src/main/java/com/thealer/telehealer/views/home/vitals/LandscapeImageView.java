package com.thealer.telehealer.views.home.vitals;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.widget.ImageView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseActivity;

/**
 * Created by Aswin on 26,March,2019
 */
public class LandscapeImageView extends BaseActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landscape_imageview);
        initView();
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.imageView);
        if (getIntent() != null) {
            String image = getIntent().getStringExtra(ArgumentKeys.SHARED_IMAGE);
            Utils.setImageWithGlide(getApplicationContext(), imageView, image, null, true, true);
        }
    }
}
