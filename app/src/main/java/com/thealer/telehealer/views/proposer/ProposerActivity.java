package com.thealer.telehealer.views.proposer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.AppPreference;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.views.base.BaseActivity;

import flavor.GoogleFit.GoogleFitManager;

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
    private TextView photopermision, camerapermission, microphonepermission, notificationpermission;
    private Switch photoswitch, cameraswitch, microphoneswitch, notificationswitch;
    private final int photoRequest = 1;
    private final int cameraRequest = 2;
    private final int microRequest = 3;
    private final int notiRequest = 4;
    private AppPreference appPreference;
    private LinearLayout photo,camera,microphone,notification;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestFullScreenMode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposer);
        appPreference = AppPreference.getInstance(ProposerActivity.this);
        initView();
    }

    private void initView() {
        closeIv = (ImageView) findViewById(R.id.close_iv);
        proposerIv = (ImageView) findViewById(R.id.proposer_iv);
        titleTv = (TextView) findViewById(R.id.title_tv);
        messageTv = (TextView) findViewById(R.id.message_tv);
        allowBtn = (CustomButton) findViewById(R.id.allow_btn);
        photo = (LinearLayout) findViewById(R.id.photo);
        photopermision = (TextView) findViewById(R.id.photo_permission);
        photoswitch = (Switch) findViewById(R.id.photo_switch);
        camera = (LinearLayout) findViewById(R.id.camera);
        camerapermission = (TextView) findViewById(R.id.camera_permission);
        cameraswitch = (Switch) findViewById(R.id.camera_switch);
        microphone = (LinearLayout) findViewById(R.id.microphone);
        microphonepermission = (TextView) findViewById(R.id.microphone_permission);
        microphoneswitch = (Switch) findViewById(R.id.microphone_switch);
        notification = (LinearLayout) findViewById(R.id.notification);
        notificationpermission = (TextView) findViewById(R.id.notification_permission);
        notificationswitch = (Switch) findViewById(R.id.notification_switch);
        allowBtn = (CustomButton) findViewById(R.id.allow_btn);

        if (getIntent() != null && getIntent().getExtras() != null) {
            titleTv.setText(getIntent().getExtras().getString(PermissionConstants.PROPOSER_TITLE));
            messageTv.setText(getIntent().getExtras().getString(PermissionConstants.PROPOSER_MESSAGE));
            proposerIv.setImageDrawable(getDrawable(getIntent().getExtras().getInt(PermissionConstants.PROPOSER_IMAGE)));
            permissionFor = getIntent().getExtras().getInt(PermissionConstants.PERMISSION_FOR);

            isPermissionDenied = PermissionChecker.with(this).isPermissionDenied(permissionFor);

//            if (isPermissionDenied) {
//                allowBtn.setText(getString(R.string.go_to_settings));
//            }
        }

        if (!PermissionChecker.with(ProposerActivity.this).isCamPhoMicNoti(permissionFor)){
            photo.setVisibility(View.GONE);
            camera.setVisibility(View.GONE);
            microphone.setVisibility(View.GONE);
            notification.setVisibility(View.GONE);
        }

        photoswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!photoswitch.isChecked()) {
                    goToSettings();
                } else {
                    requestPermission(PermissionConstants.PERMISSION_GALLERY, photoRequest);
                }
            }
        });

        cameraswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!cameraswitch.isChecked()) {
                    goToSettings();
                } else {
                    requestPermission(PermissionConstants.PERMISSION_CAMERAS, cameraRequest);
                }
            }
        });

        microphoneswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!microphoneswitch.isChecked()) {
                    goToSettings();
                } else {
                    requestPermission(PermissionConstants.PERMISSION_MICROPHONE, microRequest);
                }
            }
        });

        notificationswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!notificationswitch.isChecked()) {
                    goToSettings();
                } else {
                    requestPermission(PermissionConstants.PERMISSION_NOTIFICATION, notiRequest);
                }
            }
        });

        allowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionChecker.with(ProposerActivity.this).isCamPhoMicNoti(permissionFor)){
                    close();
                }else {
                    PermissionChecker.with(ProposerActivity.this).requestPermission(permissionFor);
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

    private void getPermission() {

        // Gallery
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(ProposerActivity.this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED) {
                photopermision.setText(getspannedText(getString(R.string.disallow_photo_permissions)));
                photopermision.setMovementMethod(LinkMovementMethod.getInstance());
                photopermision.setHighlightColor(Color.TRANSPARENT);
                photoswitch.setChecked(false);
            } else {
                photopermision.setText(getString(R.string.allow_photo_permissions));
                photoswitch.setChecked(true);
            }
        } else {
            if (ContextCompat.checkSelfPermission(ProposerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                photopermision.setText(getspannedText(getString(R.string.disallow_photo_permissions)));
                photopermision.setMovementMethod(LinkMovementMethod.getInstance());
                photopermision.setHighlightColor(Color.TRANSPARENT);
                photoswitch.setChecked(false);
            } else {
                photopermision.setText(getString(R.string.allow_photo_permissions));
                photoswitch.setChecked(true);
            }
        }

        // Camera
        if (ContextCompat.checkSelfPermission(ProposerActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            camerapermission.setText(getspannedText(getString(R.string.disallow_camera_permissions)));
            camerapermission.setMovementMethod(LinkMovementMethod.getInstance());
            camerapermission.setHighlightColor(Color.TRANSPARENT);
            cameraswitch.setChecked(false);
        } else {
            camerapermission.setText(getString(R.string.allow_camera_permissions));
            cameraswitch.setChecked(true);
        }

        // Microphone
        if (ContextCompat.checkSelfPermission(ProposerActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            microphonepermission.setText(getspannedText(getString(R.string.disallow_microphone_permissions)));
            microphonepermission.setMovementMethod(LinkMovementMethod.getInstance());
            microphonepermission.setHighlightColor(Color.TRANSPARENT);
            microphoneswitch.setChecked(false);
        } else {
            microphonepermission.setText(getString(R.string.allow_microphone_permissions));
            microphoneswitch.setChecked(true);
        }

        // Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(ProposerActivity.this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
                notificationpermission.setText(getspannedText(getString(R.string.disallow_notification_permissions)));
                notificationpermission.setMovementMethod(LinkMovementMethod.getInstance());
                notificationpermission.setHighlightColor(Color.TRANSPARENT);
                notificationswitch.setChecked(false);
            } else {
                notificationpermission.setText(getString(R.string.allow_notification_permissions));
                notificationswitch.setChecked(true);
            }
        } else {
            notificationpermission.setText(getString(R.string.allow_notification_permissions));
            notificationswitch.setChecked(true);
            notificationswitch.setClickable(false);
        }

    }

    private SpannableString getspannedText(String message) {
        SpannableString ss = new SpannableString(message);
        ClickableSpan clickableTerms = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // show toast here
                goToSettings();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);

            }
        };
        ss.setSpan(clickableTerms, message.length() - 9, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
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

    private void requestPermission(int permissionCamera, int requestCode) {
        String[] permission = PermissionChecker.with(ProposerActivity.this).getPermissions(permissionCamera);
        ActivityCompat.requestPermissions(ProposerActivity.this, permission, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case photoRequest:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                        getPermission();
                    } else {
                        if (appPreference.getBoolean("isPhotoPermissionAsk")) {
                            goToSettings();
                        }else {
                            getPermission();
                        }
                    }
                } else {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        getPermission();
                    } else {
                        if (appPreference.getBoolean("isPhotoPermissionAsk")) {
                            goToSettings();
                        }else {
                            getPermission();
                        }
                    }

                }
                appPreference.setBoolean("isPhotoPermissionAsk", true);
                break;

            case cameraRequest:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPermission();
                } else {
                    if (appPreference.getBoolean("isCameraPermissionAsk")) {
                        goToSettings();
                    }else {
                        getPermission();
                    }
                }
                appPreference.setBoolean("isCameraPermissionAsk", true);
                break;

            case microRequest:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPermission();
                } else {
                    if (appPreference.getBoolean("isMicrophonePermissionAsk")) {
                        goToSettings();
                    }else {
                        getPermission();
                    }
                }
                appPreference.setBoolean("isMicrophonePermissionAsk", true);
                break;
            case notiRequest:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPermission();
                } else {
                    if (appPreference.getBoolean("isNotificationPermissionAsk")) {
                        goToSettings();
                    }else {
                        getPermission();
                    }
                }
                appPreference.setBoolean("isNotificationPermissionAsk", true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ProposerActivity", "onActivityResult requestCode " + requestCode + " " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GoogleFitManager.REQUEST_OAUTH_REQUEST_CODE:
                int code = RESULT_OK;
                if (googleFitManager != null && googleFitManager.isPermitted()) {
                    code = RESULT_OK;
                } else {
                    code = RESULT_CANCELED;
                }
                sendProposerResultBroadCast(code);
                setResult(resultCode);
                finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPermission();
    }
}
