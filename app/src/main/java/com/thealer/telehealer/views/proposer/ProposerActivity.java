package com.thealer.telehealer.views.proposer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.views.base.BaseActivity;

import Flavor.GoogleFit.GoogleFitManager;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 09,November,2018
 */
public class ProposerActivity extends BaseActivity {
    private ImageView closeIv;
    private ImageView proposerIv;
    private TextView titleTv;
    private TextView messageTv;
    private CustomButton allowBtn;
    private int permissionFor;
    private boolean isPermissionDenied = false;
    @Nullable
    private GoogleFitManager googleFitManager = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestFullScreenMode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposer);
        initView();
    }

    private void initView() {
        closeIv = (ImageView) findViewById(R.id.close_iv);
        proposerIv = (ImageView) findViewById(R.id.proposer_iv);
        titleTv = (TextView) findViewById(R.id.title_tv);
        messageTv = (TextView) findViewById(R.id.message_tv);
        allowBtn = (CustomButton) findViewById(R.id.allow_btn);

        if (getIntent() != null && getIntent().getExtras() != null) {
            titleTv.setText(getIntent().getExtras().getString(PermissionConstants.PROPOSER_TITLE));
            messageTv.setText(getIntent().getExtras().getString(PermissionConstants.PROPOSER_MESSAGE));
            proposerIv.setImageDrawable(getDrawable(getIntent().getExtras().getInt(PermissionConstants.PROPOSER_IMAGE)));
            permissionFor = getIntent().getExtras().getInt(PermissionConstants.PERMISSION_FOR);

            isPermissionDenied = PermissionChecker.with(this).isPermissionDenied(permissionFor);

            if (isPermissionDenied) {
                allowBtn.setText(getString(R.string.go_to_settings));
            }
        }

        allowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPermissionDenied) {
                    goToSettings();
                } else {
                    requestPermission();
                }
            }
        });

        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
    }

    private void goToSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        close();
    }

    private void close() {
        sendProposerResultBroadCast(RESULT_CANCELED);
        setResult(RESULT_CANCELED);
        finish();
    }

    private void sendProposerResultBroadCast(int result) {
        Bundle bundle = new Bundle();
        bundle.putInt(ArgumentKeys.PROPOSER_RESULT_CODE, result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(getString(R.string.proposer_broadcastReceiver)).putExtras(bundle));
    }

    private void requestPermission() {
        PermissionChecker.with(ProposerActivity.this).requestPermission(permissionFor);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("ProposerActivity", "onRequestPermissionsResult "+requestCode);
        for (String permission :
                permissions) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                appPreference.setBoolean(permission, true);
            }
        }

        int resultCode = RESULT_OK;

        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                resultCode = RESULT_CANCELED;
                break;
            }
        }

        if (requestCode ==  GoogleFitManager.REQUEST_OAUTH_REQUEST_CODE) {
            Log.d("ProposerActivity", "googleFitManager req");
            googleFitManager = new GoogleFitManager(this);
            googleFitManager.requestPermission();
        } else {
            sendProposerResultBroadCast(resultCode);
            setResult(resultCode);
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ProposerActivity", "onActivityResult requestCode "+requestCode+" "+resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GoogleFitManager.REQUEST_OAUTH_REQUEST_CODE:
                int code = RESULT_OK;
                if (googleFitManager != null && googleFitManager.isPermitted()) {
                    code  = RESULT_OK;
                } else {
                    code  = RESULT_CANCELED;
                }
                sendProposerResultBroadCast(code);
                setResult(resultCode);
                finish();
        }
    }

}
