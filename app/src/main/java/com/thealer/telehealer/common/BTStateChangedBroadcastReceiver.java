package com.thealer.telehealer.common;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.thealer.telehealer.R;

/**
 * Created by rsekar on 12/5/18.
 */

public class BTStateChangedBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                -1);

        switch (state) {
            case BluetoothAdapter.STATE_CONNECTED:
                break;
            case BluetoothAdapter.STATE_CONNECTING:
                break;
            case BluetoothAdapter.STATE_DISCONNECTED:
                break;
            case BluetoothAdapter.STATE_DISCONNECTING:
                break;
            case BluetoothAdapter.STATE_OFF:
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(context.getString(R.string.bluetooth_disabled)));
                break;
            case BluetoothAdapter.STATE_ON:
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(context.getString(R.string.bluetooth_enabled)));
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                break;
        }
    }

}