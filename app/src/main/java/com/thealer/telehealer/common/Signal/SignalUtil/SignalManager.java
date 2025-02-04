package com.thealer.telehealer.common.Signal.SignalUtil;

import android.util.Base64;

import androidx.annotation.Nullable;

import com.thealer.telehealer.common.Signal.SignalModels.IdentityKey;
import com.thealer.telehealer.common.Signal.SignalModels.PreKey;
import com.thealer.telehealer.common.Signal.SignalModels.SignalKey;
import com.thealer.telehealer.common.Signal.SignalModels.SignalKeyPostModel;
import com.thealer.telehealer.common.Signal.SignalModels.SignedPreKey;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;

import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.ecc.ECKeyPair;
import org.whispersystems.libsignal.ecc.ECPrivateKey;
import org.whispersystems.libsignal.ecc.ECPublicKey;
import org.whispersystems.libsignal.protocol.PreKeySignalMessage;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.util.KeyHelper;

public class SignalManager {

    @Nullable
    public static String encryptMessage(String message, SignalKey myKey, SignalKey otherKey) {
        if (message == null)
            return "";
        try {
            Entity myEntity = new Entity(myKey);
            Entity otherEntity = new Entity(otherKey);
            Session aliceToBobSession = new Session(myEntity.getStore(), otherEntity.getPreKey(), otherEntity.getAddress());
            PreKeySignalMessage toBobMessages = aliceToBobSession.encrypt(message);
            return Base64.encodeToString(toBobMessages.serialize(), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decryptMessage(String message, SignalKey myKey, SignalKey otherKey) {
        if (message == null)
            return "";
        try {
            Entity myEntity = new Entity(myKey);
            Entity otherEntity = new Entity(otherKey);

            Session bobToAliceSession = new Session(myEntity.getStore(), otherEntity.getPreKey(), otherEntity.getAddress());
            PreKeySignalMessage preKeySignalMessage = new PreKeySignalMessage(Base64.decode(message, Base64.DEFAULT));
            return bobToAliceSession.decrypt(preKeySignalMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SignalKeyPostModel generateNewKeys() throws InvalidKeyException {
        SignalKeyPostModel key = new SignalKeyPostModel();

        IdentityKeyPair identityKeyPair = KeyHelper.generateIdentityKeyPair();

        PreKeyRecord preKeyRecord = KeyHelper.generatePreKeys(0, 1).get(0);

        ECKeyPair ec_key_pair = preKeyRecord.getKeyPair();
        ECPrivateKey pre_key_private = ec_key_pair.getPrivateKey();
        ECPublicKey pre_key_public = ec_key_pair.getPublicKey();
        SignedPreKeyRecord signed_pre_key = KeyHelper.generateSignedPreKey(identityKeyPair, 0);

        ECKeyPair ec_signed_key_pair = signed_pre_key.getKeyPair();
        ECPrivateKey signed_pre_key_private = ec_signed_key_pair.getPrivateKey();
        ECPublicKey signed_pre_key_public = ec_signed_key_pair.getPublicKey();

        PreKey preKey = new PreKey();
        preKey.setPubKey(Utils.trimAndRemoveNewLineCharacters(SignalEncodeDecode.publicKey(pre_key_public)));
        preKey.setPrivKey(Utils.trimAndRemoveNewLineCharacters(SignalEncodeDecode.privateKey(pre_key_private)));
        preKey.setKeyId(preKeyRecord.getId());

        // Signed pre key
        SignedPreKey signedPreKey = new SignedPreKey();
        signedPreKey.setPrivKey(Utils.trimAndRemoveNewLineCharacters(SignalEncodeDecode.privateKey(signed_pre_key_private)));
        signedPreKey.setPubKey(Utils.trimAndRemoveNewLineCharacters(SignalEncodeDecode.publicKey(signed_pre_key_public)));
        signedPreKey.setSignature(Utils.trimAndRemoveNewLineCharacters(SignalEncodeDecode.signedPreKeySignature(signed_pre_key)));
        signedPreKey.setKeyId(signed_pre_key.getId());

        IdentityKey identityKey = new IdentityKey();
        identityKey.setPrivKey(Utils.trimAndRemoveNewLineCharacters(SignalEncodeDecode.privateKey(identityKeyPair.getPrivateKey())));
        identityKey.setPubKey(Utils.trimAndRemoveNewLineCharacters(SignalEncodeDecode.publicKey(identityKeyPair.getPublicKey())));

        key.setPreKey(preKey);
        key.setSignedPreKey(signedPreKey);
        key.setIdentityKey(identityKey);
        key.setRegistrationId(generateRegistrationId());
        key.setDevice_id(1);
        key.setUser_guid(UserDetailPreferenceManager.getWhoAmIResponse().getUser_guid());
        return key;
    }

    private static int generateRegistrationId() {
        return KeyHelper.generateRegistrationId(true);
    }

}
