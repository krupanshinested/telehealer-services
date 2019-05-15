package com.thealer.telehealer.common.Signal.SignalUtil;


import android.text.TextUtils;
import android.util.Base64;

import com.thealer.telehealer.common.Signal.SignalModels.SignalKey;

import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.ecc.Curve;
import org.whispersystems.libsignal.ecc.ECKeyPair;
import org.whispersystems.libsignal.ecc.ECPrivateKey;
import org.whispersystems.libsignal.ecc.ECPublicKey;
import org.whispersystems.libsignal.state.PreKeyBundle;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SignalProtocolStore;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.state.impl.InMemorySignalProtocolStore;

public class Entity {
    private SignalProtocolStore store;
    private final PreKeyBundle preKey;
    private final SignalProtocolAddress address;

    public Entity(SignalKey entityKey) throws InvalidKeyException {
        ECPublicKey identity_public_key = SignalEncodeDecode.getPublickey(entityKey.getIdentity_key().getPubKey());
        ECPublicKey pre_public_key = SignalEncodeDecode.getPublickey(entityKey.getPre_key().getPubKey());
        ECPublicKey signed_pre_public_key = SignalEncodeDecode.getPublickey(entityKey.getSigned_pre_key().getPubKey());

        IdentityKey identityKey = new IdentityKey(identity_public_key);
        this.address = new SignalProtocolAddress(entityKey.getUser_guid(), entityKey.getDevice_id());

        byte[] signedPreKeySignature = Base64.decode(entityKey.getSigned_pre_key().getSignature(), Base64.DEFAULT);

        this.preKey = new PreKeyBundle((int) entityKey.getRegistration_id(),
                (int) entityKey.getDevice_id(), /* device ID */
                (int) entityKey.getPre_key().getKeyId(), /* pre key ID */
                pre_public_key,
                (int) entityKey.getSigned_pre_key().getKeyId(), /* signed pre key ID */
                signed_pre_public_key,
                signedPreKeySignature,
                new IdentityKey(identity_public_key));

        try {
            ECPrivateKey identity_pri_key = SignalEncodeDecode.getPrivateKey(entityKey.getIdentity_key().getPrivKey());
            IdentityKeyPair identityKeyPair = new IdentityKeyPair(identityKey, identity_pri_key);
            this.store = new InMemorySignalProtocolStore(identityKeyPair, entityKey.getRegistration_id());

            ECPrivateKey pre_pri_key = SignalEncodeDecode.getPrivateKey(entityKey.getPre_key().getPrivKey());

            ECPrivateKey signed_pre_pri_key = SignalEncodeDecode.getPrivateKey(entityKey.getSigned_pre_key().getPrivKey());

            ECKeyPair preKeyPair = new ECKeyPair(pre_public_key, pre_pri_key);
            ECKeyPair signedPreKeyPair = new ECKeyPair(signed_pre_public_key, signed_pre_pri_key);

            long timestamp = System.currentTimeMillis();

            PreKeyRecord preKeyRecord = new PreKeyRecord(preKey.getPreKeyId(), preKeyPair);
            SignedPreKeyRecord signedPreKeyRecord = new SignedPreKeyRecord(
                    entityKey.getSigned_pre_key().getKeyId(), timestamp, signedPreKeyPair, signedPreKeySignature);

            store.storePreKey(entityKey.getPre_key().getKeyId(), preKeyRecord);
            store.storeSignedPreKey(entityKey.getSigned_pre_key().getKeyId(), signedPreKeyRecord);
        } catch (Exception e) {
            this.store = null;
        }

    }

    public SignalProtocolStore getStore() {
        return store;
    }

    public PreKeyBundle getPreKey() {
        return preKey;
    }

    public SignalProtocolAddress getAddress() {
        return address;
    }
}
