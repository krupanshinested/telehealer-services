package com.thealer.telehealer.common.biometric;

import android.content.Context;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Constants;

/**
 * Created by Aswin on 31,October,2018
 */
public class BiometricFingerPrint {

    private static BiometricFingerPrint biometricFingerPrint;

    public static BiometricFingerPrint initializeFingerPrint(Context context, BiometricInterface biometricInterface) {

        if (biometricFingerPrint == null)
            biometricFingerPrint = new BiometricFingerPrint();

        FingerprintManagerCompat.CryptoObject cryptoObject = new FingerprintManagerCompat.CryptoObject(BioMetricUtils.getCipher());
        CancellationSignal cancellationSignal = new CancellationSignal();

        FingerprintManagerCompat.from(context).authenticate(cryptoObject,
                0,
                cancellationSignal,
                new FingerprintManagerCompat.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errMsgId, CharSequence errString) {
                        super.onAuthenticationError(errMsgId, errString);
                        biometricInterface.onBioMetricActionComplete(errString.toString(), errMsgId);
                    }

                    @Override
                    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                        super.onAuthenticationHelp(helpMsgId, helpString);
                        biometricInterface.onBioMetricActionComplete(helpString.toString(), helpMsgId);
                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        biometricInterface.onBioMetricActionComplete(context.getString(R.string.BIOMETRIC_SUCCESS), Constants.BIOMETRIC_SUCCESS);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        biometricInterface.onBioMetricActionComplete(context.getString(R.string.BIOMETRIC_FAILED), Constants.BIOMETRIC_FAILED);
                    }
                }, null);

        return biometricFingerPrint;
    }
}

