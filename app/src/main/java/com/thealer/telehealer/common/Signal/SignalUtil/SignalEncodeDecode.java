package com.thealer.telehealer.common.Signal.SignalUtil;

import android.util.Base64;

import androidx.annotation.Nullable;

import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.ecc.Curve;
import org.whispersystems.libsignal.ecc.ECPrivateKey;
import org.whispersystems.libsignal.ecc.ECPublicKey;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;


public class SignalEncodeDecode {
    public static ECPublicKey getPublickey(String key) throws InvalidKeyException {
       return Curve.decodePoint(Base64.decode(key,Base64.DEFAULT),0);
    }

    public static ECPrivateKey getPrivateKey(String private_key) {
       return Curve.decodePrivatePoint(Base64.decode(private_key,Base64.DEFAULT));
    }

    @Nullable
    public static String privateKey(@Nullable ECPrivateKey ecPrivateKey) {
        if (ecPrivateKey == null) {
            return null;
        }
        return Base64.encodeToString(ecPrivateKey.serialize(),Base64.DEFAULT);
    }

    @Nullable
    public static String publicKey(@Nullable org.whispersystems.libsignal.IdentityKey ecPublicKey) {
        if (ecPublicKey == null) {
            return null;
        }
        return Base64.encodeToString(ecPublicKey.serialize(),Base64.DEFAULT);
    }

    @Nullable
    public static String publicKey(@Nullable ECPublicKey ecPublicKey) {
        if (ecPublicKey == null) {
            return null;
        }
        return Base64.encodeToString(ecPublicKey.serialize(),Base64.DEFAULT);
    }


    public static String signedPreKeySignature(SignedPreKeyRecord signed_pre_key) {
        return Base64.encodeToString(signed_pre_key.getSignature(),Base64.DEFAULT);
    }

}
