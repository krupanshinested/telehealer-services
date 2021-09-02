package com.thealer.telehealer.common.biometric;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.CancellationSignal;
import android.os.Handler;
import android.widget.Toast;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseActivity;

/**
 * Created by Aswin on 31,October,2018
 */
public class BioMetricAuth {


    public static void showBioMetricAuth(Context context, BiometricInterface biometricInterface) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //Check if the min sdk is > 23
                if (BioMetricUtils.isSdkVersionSupported()) {

                    //Check for hardware support and check fingerprint registered

                    if (BioMetricUtils.isHardwareSupported(context) && BioMetricUtils.isFingerprintAvailable(context)) {

                        //If the current sdk is > P user Biometric prompt else show custom biometric prompt

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                            BiometricPrompt.CryptoObject biometricCryptoObject = new BiometricPrompt.CryptoObject(BioMetricUtils.getCipher());


                            CancellationSignal cancellationSignal = new CancellationSignal();

                            BiometricPrompt.AuthenticationCallback authenticationCallback = new BiometricPrompt.AuthenticationCallback() {
                                @Override
                                public void onAuthenticationError(int errorCode, CharSequence errString) {
                                    super.onAuthenticationError(errorCode, errString);
                                    biometricInterface.onBioMetricActionComplete(errString.toString(), Constants.BIOMETRIC_ERROR);
                                }

                                @Override
                                public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                                    super.onAuthenticationHelp(helpCode, helpString);
                                    biometricInterface.onBioMetricActionComplete(helpString.toString(), helpCode);
                                }

                                @Override
                                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                                    super.onAuthenticationSucceeded(result);
                                    biometricInterface.onBioMetricActionComplete(context.getString(R.string.BIOMETRIC_SUCCESS), Constants.BIOMETRIC_SUCCESS);
                                }

                                @Override
                                public void onAuthenticationFailed() {
                                    super.onAuthenticationFailed();
                                    Constants.Fail_Count++;
                                    if (Constants.Fail_Count > 2) {
                                        biometricInterface.onBioMetricActionComplete(context.getString(R.string.BIOMETRIC_FAILED), Constants.BIOMETRIC_FAILED);
                                    } else {
                                        String attemptsRemaining = (Constants.TotalCount - Constants.Fail_Count) + "";
                                        Toast.makeText(context, context.getString(R.string.wrong_fingure, attemptsRemaining), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            };

                            BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(context)
                                    .setTitle(context.getString(R.string.biometric_title))
                                    .setSubtitle(context.getString(R.string.biometric_subtitle))
                                    .setDescription(context.getString(R.string.biometric_description))
                                    .setNegativeButton(context.getString(R.string.cancel),
                                            context.getMainExecutor(),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    biometricInterface.onBioMetricActionComplete(null, Constants.BIOMETRIC_CANCEL);
                                                }
                                            })
                                    .build();

                            biometricPrompt.authenticate(biometricCryptoObject,
                                    cancellationSignal,
                                    context.getMainExecutor(),
                                    authenticationCallback);

                        } else {
                            showCustomBiometricAuth(context);

                        }
                    }

                }
            }
        };
        new Handler().postDelayed(runnable, 1000);
    }

    private static void showCustomBiometricAuth(Context context) {
        try {
            BioMetricCustomAuth bioMetricCustomAuth = new BioMetricCustomAuth();
            bioMetricCustomAuth.setCancelable(false);

            bioMetricCustomAuth.show(((BaseActivity) context).getSupportFragmentManager(), BioMetricCustomAuth.class.getSimpleName());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
