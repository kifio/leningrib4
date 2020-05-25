package kifio.leningrib;

import android.text.TextUtils;
import android.util.Base64;
import com.android.billingclient.util.BillingHelper;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Security-related methods. For a secure implementation, all of this code should be implemented on
 * a server that communicates with the application on the device.
 */
public class Security {

    private static final String VERIFICATION_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2FFKMV8jr7GjsAvjNRL/R96A7HUy6GHuSiDaW3D4XPO8jVI8xABYYRAIHz2PvOQvNpNJCrwNtIgVP9V+09yqGJe0Ij/EtaORHghTuiHh39rcX5sWgMBOlF7+DrUo9WYBlq8HVghFVnCeoDF0VGOgrybC20+I7EUTwzPms3xNywHf4LhQBclJTmT2yD1dpG4Qbp9JHjM1rmzn74qYbfM1yfpk7DFGF2gsWXXvcpNyqwk1QZvVjmEmPT0v28pd4fRMrZ9vrtejn/9W4MU5bJApo/Uus7NqKXJfqPQhQVRoAHOe6m/r3xs0PvFNXsIwn5oWEgs9FezCRAuci4c8n4YQ5wIDAQAB";
    private static final String TAG = "IABUtil/Security";

    private static final String KEY_FACTORY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

    /**
     * Verifies that the data was signed with the given signature, and returns the verified
     * purchase.
     *
     * @param signedData      the signed JSON string (signed, not encrypted)
     * @param signature       the signature for the data, signed with the private key
     * @throws IOException if encoding algorithm is not supported or key specification
     *                     is invalid
     */
    public static boolean verifyPurchase(String signedData,
                                         String signature) throws IOException {
        if (TextUtils.isEmpty(signedData) || TextUtils.isEmpty(VERIFICATION_KEY)
                || TextUtils.isEmpty(signature)) {
            BillingHelper.logWarn(TAG, "Purchase verification failed: missing data.");
            return false;
        }

        PublicKey key = generatePublicKey(VERIFICATION_KEY);
        return verify(key, signedData, signature);
    }

    /**
     * Generates a PublicKey instance from a string containing the Base64-encoded public key.
     *
     * @param encodedPublicKey Base64-encoded public key
     * @throws IOException if encoding algorithm is not supported or key specification
     *                     is invalid
     */
    public static PublicKey generatePublicKey(String encodedPublicKey) throws IOException {
        try {
            byte[] decodedKey = Base64.decode(encodedPublicKey, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
            return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
        } catch (NoSuchAlgorithmException e) {
            // "RSA" is guaranteed to be available.
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            String msg = "Invalid key specification: " + e;
            BillingHelper.logWarn(TAG, msg);
            throw new IOException(msg);
        }
    }

    /**
     * Verifies that the signature from the server matches the computed signature on the data.
     * Returns true if the data is correctly signed.
     *
     * @param publicKey  public key associated with the developer account
     * @param signedData signed data from server
     * @param signature  server signature
     * @return true if the data and signature match
     */
    public static boolean verify(PublicKey publicKey, String signedData, String signature) {
        byte[] signatureBytes;
        try {
            signatureBytes = Base64.decode(signature, Base64.DEFAULT);
        } catch (IllegalArgumentException e) {
            BillingHelper.logWarn(TAG, "Base64 decoding failed.");
            return false;
        }
        try {
            Signature signatureAlgorithm = Signature.getInstance(SIGNATURE_ALGORITHM);
            signatureAlgorithm.initVerify(publicKey);
            signatureAlgorithm.update(signedData.getBytes());
            if (!signatureAlgorithm.verify(signatureBytes)) {
                BillingHelper.logWarn(TAG, "Signature verification failed.");
                return false;
            }
            return true;
        } catch (NoSuchAlgorithmException e) {
            // "RSA" is guaranteed to be available.
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            BillingHelper.logWarn(TAG, "Invalid key specification.");
        } catch (SignatureException e) {
            BillingHelper.logWarn(TAG, "Signature exception.");
        }
        return false;
    }
}
