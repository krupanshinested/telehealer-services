package com.thealer.telehealer.views.guestlogin.screens;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.pubNub.PubNubNotificationPayload;
import com.thealer.telehealer.common.pubNub.models.PatientInvite;
import com.thealer.telehealer.views.base.BaseActivity;

public class GuestLoginScreensActivity extends BaseActivity {
    private PatientInvite patientInvite;
    private String screenType;
    private Fragment fragment=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_login_screens);

        patientInvite = (PatientInvite) getIntent().getSerializableExtra(ArgumentKeys.GUEST_INFO);
        screenType=getIntent().getStringExtra(ArgumentKeys.GUEST_SCREENTYPE);

        if (screenType.equalsIgnoreCase(ArgumentKeys.WAITING_SCREEN)){
            fragment=new WaitingScreenFragment();
        }
        setFragment(fragment);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        intent.putExtra(ArgumentKeys.GUEST_INFO,getIntent().getSerializableExtra(ArgumentKeys.GUEST_INFO));
        intent.putExtra(ArgumentKeys.GUEST_SCREENTYPE,getIntent().getStringExtra(ArgumentKeys.GUEST_SCREENTYPE));
        super.onNewIntent(intent);
        Log.d("GuestLoginScreens","onNewIntent");
    }

    public void setFragment(Fragment fragment) {
        Bundle bundle=new Bundle();
        if (fragment != null) {
            try {
                bundle.putSerializable(ArgumentKeys.GUEST_INFO, patientInvite);
                fragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.fragment_remove_animation, R.anim.fragment_remove_exit)
                        .replace(R.id.guestlogin_fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}