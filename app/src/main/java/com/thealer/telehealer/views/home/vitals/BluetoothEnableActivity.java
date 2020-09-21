package com.thealer.telehealer.views.home.vitals;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.views.base.BaseActivity;

/**
 * Created by rsekar on 12/5/18.
 */

public class BluetoothEnableActivity extends BaseActivity {

    public static final int REQUEST_ID = 849;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetoot_enable_activity);

        LocalBroadcastManager.getInstance(this).registerReceiver(bluetoothEnableReceiver, new IntentFilter(getString(R.string.bluetooth_enabled)));

        findViewById(R.id.close_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent data = new Intent();
                data.putExtra(ArgumentKeys.RESULT,false);
                setResult(Activity.RESULT_OK,data);

                finish();
            }
        });

        findViewById(R.id.enable_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter != null) {
                    bluetoothAdapter.enable();
                }

                didEnabled();
            }
        });
    }

    private void didEnabled() {
        Intent data = new Intent();
        data.putExtra(ArgumentKeys.RESULT,true);
        setResult(Activity.RESULT_OK,data);

        finish();
    }

    private BroadcastReceiver bluetoothEnableReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            didEnabled();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public  void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(bluetoothEnableReceiver);
    }
}
