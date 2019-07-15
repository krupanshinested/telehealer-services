package com.thealer.telehealer.views.call;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.views.common.ContentActivity;
import com.thealer.telehealer.views.home.chat.ChatActivity;

/**
 * Created by Aswin on 09,August,2019
 */
public class CallMessageActivity extends ContentActivity {
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.action_btn) {
            Bundle bundle = new Bundle();
            bundle.putString(ArgumentKeys.USER_GUID, getIntent().getStringExtra(ArgumentKeys.USER_GUID));
            bundle.putString(ArgumentKeys.DOCTOR_GUID, getIntent().getStringExtra(ArgumentKeys.DOCTOR_GUID));

            Intent intent = new Intent(this, ChatActivity.class).putExtras(bundle);
            startActivity(intent);
            finish();
        } else {
            super.onClick(v);
        }
    }
}
