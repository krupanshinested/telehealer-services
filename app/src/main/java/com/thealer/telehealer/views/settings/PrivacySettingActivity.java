package com.thealer.telehealer.views.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.AppPreference;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.proposer.ProposerActivity;
import com.thealer.telehealer.views.settings.newDeviceSupport.NewDeviceSupportActivity;

public class PrivacySettingActivity extends BaseActivity implements View.OnClickListener {

    private ImageView backIv;
    private TextView toolbarTitle;
    private TextView photopermission,camerapermission,microphonepermission,notificationpermission,bluetoothpermission,locationpermission,contactpermission,healthpermission;
    private Switch photoswitch,cameraswitch,microphoneswitch,notificationswitch,locationswitch,bluetoothswitch,contactswitch,healthswitch;
    private final int photoRequest = 1;
    private final int cameraRequest = 2;
    private final int microRequest = 3;
    private final int notiRequest = 4;
    private final int bluetoothRequest = 5;
    private final int locRequest = 6;
    private final int contactRequest = 7;
    private final int healthRequest = 8;
    private AppPreference appPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_setting);
        appPreference = AppPreference.getInstance(PrivacySettingActivity.this);
        initView();
    }

    private void initView() {
        backIv = findViewById(R.id.back_iv);
        toolbarTitle = findViewById(R.id.toolbar_title);

        photopermission = findViewById(R.id.photo_permission);
        photoswitch = findViewById(R.id.photo_switch);
        camerapermission = findViewById(R.id.camera_permission);
        cameraswitch = findViewById(R.id.camera_switch);
        microphonepermission = findViewById(R.id.microphone_permission);
        microphoneswitch = findViewById(R.id.microphone_switch);
        notificationpermission = findViewById(R.id.notification_permission);
        notificationswitch = findViewById(R.id.notification_switch);
        bluetoothpermission = findViewById(R.id.bluetooth_permission);
        bluetoothswitch = findViewById(R.id.bluetooth_switch);
        locationpermission = findViewById(R.id.location_permission);
        locationswitch = findViewById(R.id.location_switch);
        contactpermission = findViewById(R.id.contact_permission);
        contactswitch = findViewById(R.id.contact_switch);
        healthpermission = findViewById(R.id.health_permission);
        healthswitch = findViewById(R.id.health_switch);

        toolbarTitle.setText(getString(R.string.privacy_setting));
        backIv.setOnClickListener(this);

        photoswitch.setOnClickListener(this);
        cameraswitch.setOnClickListener(this);
        microphoneswitch.setOnClickListener(this);
        notificationswitch.setOnClickListener(this);
        bluetoothswitch.setOnClickListener(this);
        locationswitch.setOnClickListener(this);
        contactswitch.setOnClickListener(this);
        healthswitch.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPermission();
    }

    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(PrivacySettingActivity.this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED) {
                photopermission.setText(getspannedText(getString(R.string.disallow_photo_permissions)));
                photopermission.setMovementMethod(LinkMovementMethod.getInstance());
                photopermission.setHighlightColor(Color.TRANSPARENT);
                photoswitch.setChecked(false);
            } else {
                photopermission.setText(getString(R.string.allow_photo_permissions));
                photoswitch.setChecked(true);
            }
        } else {
            if (ContextCompat.checkSelfPermission(PrivacySettingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                photopermission.setText(getspannedText(getString(R.string.disallow_photo_permissions)));
                photopermission.setMovementMethod(LinkMovementMethod.getInstance());
                photopermission.setHighlightColor(Color.TRANSPARENT);
                photoswitch.setChecked(false);
            } else {
                photopermission.setText(getString(R.string.allow_photo_permissions));
                photoswitch.setChecked(true);
            }
        }

        // Camera
        if (ContextCompat.checkSelfPermission(PrivacySettingActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            camerapermission.setText(getspannedText(getString(R.string.disallow_camera_permissions)));
            camerapermission.setMovementMethod(LinkMovementMethod.getInstance());
            camerapermission.setHighlightColor(Color.TRANSPARENT);
            cameraswitch.setChecked(false);
        } else {
            camerapermission.setText(getString(R.string.allow_camera_permissions));
            cameraswitch.setChecked(true);
        }

        // Microphone
        if (ContextCompat.checkSelfPermission(PrivacySettingActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
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
            if (ContextCompat.checkSelfPermission(PrivacySettingActivity.this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
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

        //Bluetooth
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            bluetoothswitch.setChecked(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_DENIED);
        }else {
            bluetoothswitch.setChecked(true);
            notificationswitch.setClickable(false);
        }

        //Location
        locationswitch.setChecked(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);

        //Contact
        contactswitch.setChecked(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_DENIED);

        //Health
        healthswitch.setChecked(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_DENIED);

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

    protected void goToSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void requestPermission(int permissionCamera, int requestCode) {
        String[] permission = PermissionChecker.with(PrivacySettingActivity.this).getPermissions(permissionCamera);
        ActivityCompat.requestPermissions(PrivacySettingActivity.this, permission, requestCode);
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
            case bluetoothRequest:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPermission();
                } else {
                    if (appPreference.getBoolean("isBluetoothPermissionAsk")) {
                        goToSettings();
                    }else {
                        getPermission();
                    }
                }
                appPreference.setBoolean("isBluetoothPermissionAsk", true);
                break;
            case locRequest:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPermission();
                } else {
                    if (appPreference.getBoolean("isLocationPermissionAsk")) {
                        goToSettings();
                    }else {
                        getPermission();
                    }
                }
                appPreference.setBoolean("isLocationPermissionAsk", true);
                break;
            case contactRequest:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPermission();
                } else {
                    if (appPreference.getBoolean("isContactPermissionAsk")) {
                        goToSettings();
                    }else {
                        getPermission();
                    }
                }
                appPreference.setBoolean("isContactPermissionAsk", true);
                break;
            case healthRequest:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPermission();
                } else {
                    if (appPreference.getBoolean("isHealthPermissionAsk")) {
                        goToSettings();
                    }else {
                        getPermission();
                    }
                }
                appPreference.setBoolean("isHealthPermissionAsk", true);
                break;


        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
            case R.id.photo_switch:
                if (!photoswitch.isChecked()) {
                    goToSettings();
                } else {
                    requestPermission(PermissionConstants.PERMISSION_GALLERY, photoRequest);
                }
                break;
            case R.id.camera_switch:
                if (!cameraswitch.isChecked()) {
                    goToSettings();
                } else {
                    requestPermission(PermissionConstants.PERMISSION_CAMERAS, cameraRequest);
                }
                break;
            case R.id.microphone_switch:
                if (!microphoneswitch.isChecked()) {
                    goToSettings();
                } else {
                    requestPermission(PermissionConstants.PERMISSION_MICROPHONE, microRequest);
                }
                break;
            case R.id.notification_switch:
                if (!notificationswitch.isChecked()) {
                    goToSettings();
                } else {
                    requestPermission(PermissionConstants.PERMISSION_NOTIFICATION, notiRequest);
                }
                break;
            case R.id.bluetooth_switch:
                if (!bluetoothswitch.isChecked()) {
                    goToSettings();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, bluetoothRequest);
                }
                break;
            case R.id.location_switch:
                if (!locationswitch.isChecked()) {
                    goToSettings();
                } else {
                    requestPermission(PermissionConstants.PERMISSION_LOCATION, locRequest);
                }
                break;
            case R.id.contact_switch:
                if (!contactswitch.isChecked()) {
                    goToSettings();
                } else {
                    requestPermission(PermissionConstants.PERMISSION_CONTACTS, contactRequest);
                }
                break;
            case R.id.health_switch:
                if (!healthswitch.isChecked()) {
                    goToSettings();
                } else {
                    requestPermission(PermissionConstants.PERMISSION_GOOGLE_FIT, healthRequest);
                }
                break;
        }
    }
}