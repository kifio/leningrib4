package kifio.leningrib;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kifio.leningrib.platform.OnBillingInitializedListener;
import kifio.leningrib.platform.OnPurchasesLoadedListener;
import kifio.leningrib.platform.StoreInterface;
import kifio.leningrib.platform.items.StoreItem;

public class Store implements StoreInterface {

    private static final String VERIFICATION_HASH = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2FFKMV8jr7GjsAvjNRL/R96A7HUy6GHuSiDaW3D4XPO8jVI8xABYYRAIHz2PvOQvNpNJCrwNtIgVP9V+09yqGJe0Ij/EtaORHghTuiHh39rcX5sWgMBOlF7+DrUo9WYBlq8HVghFVnCeoDF0VGOgrybC20+I7EUTwzPms3xNywHf4LhQBclJTmT2yD1dpG4Qbp9JHjM1rmzn74qYbfM1yfpk7DFGF2gsWXXvcpNyqwk1QZvVjmEmPT0v28pd4fRMrZ9vrtejn/9W4MU5bJApo/Uus7NqKXJfqPQhQVRoAHOe6m/r3xs0PvFNXsIwn5oWEgs9FezCRAuci4c8n4YQ5wIDAQAB";

    private BillingClient billingClient;
    private Activity ctx;
    private boolean hasBillingConnection = false;
    private List<String> skuList = new ArrayList<>();
    private List<SkuDetails> skuDetailsList = new ArrayList<>();


    Store(Activity ctx) {
        this.ctx = ctx;
        skuList.add("3_vodka_bottles");
        skuList.add("5_vodka_bottles");
        skuList.add("gum_1");
        skuList.add("gum_2");
    }

    @Override
    public void setup(final OnBillingInitializedListener listener) {

        if (billingClient == null) {
            hasBillingConnection = false;
            billingClient = BillingClient.newBuilder(ctx).setListener(new PurchasesUpdatedListener() {
                @Override
                public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
                    if (billingResult.getResponseCode() == BillingResponse.OK
                            && purchases != null) {
                        for (Purchase purchase : purchases) {
                            handlePurchase(purchase);
                        }
                    } else if (billingResult.getResponseCode() == BillingResponse.USER_CANCELED) {
                        // Handle an error caused by a user cancelling the purchase flow.
                    } else {
                        // Handle any other error codes.
                    }
                }
            }).enablePendingPurchases().build();
        } else if (billingClient.isReady()) {
            listener.onBillingInitialized();
            return;
        }

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == com.android.billingclient.api.BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    hasBillingConnection = true;
                    listener.onBillingInitialized();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                if (!hasBillingConnection) {
                    Toast.makeText(ctx, "Billing client was'n connected", Toast.LENGTH_SHORT).show();
                }
                hasBillingConnection = false;
            }
        });
    }

    @Override
    public void dispose() {
        billingClient.endConnection();
        billingClient = null;
        hasBillingConnection = false;
    }

    @Override
    public void loadPurchases(final OnPurchasesLoadedListener listener) {
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            Store.this.skuDetailsList.clear();
                            Store.this.skuDetailsList.addAll(skuDetailsList);
                        }

                        if (listener != null) {
                            updateSkuDetails(listener);
                        }
                    }
                });
    }

    private void updateSkuDetails(OnPurchasesLoadedListener listener) {
        ArrayList<StoreItem> items = new ArrayList<>();
        for (SkuDetails details : skuDetailsList) {
            String description = details.getTitle() + " " + details.getDescription();
            description = description.replace(" (Leningrib)" , "");
            items.add(new StoreItem(
                    skuList.indexOf(details.getSku()), details.getPrice(), description
            ));
        }
        listener.onPurchasesLoaded(items);
    }

    @Override
    public void launchBillingFlow(int index) {
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(this.skuDetailsList.get(index))
                .build();
        int responseCode = billingClient.launchBillingFlow(flowParams);
    }
}
