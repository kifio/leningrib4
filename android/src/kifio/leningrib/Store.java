package kifio.leningrib;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kifio.leningrib.platform.OnBillingInitializedListener;
import kifio.leningrib.platform.OnPurchasesLoadedListener;
import kifio.leningrib.platform.OnStoreItemsLoadedListener;
import kifio.leningrib.platform.OnUpdatePurchasesListener;
import kifio.leningrib.platform.StoreInterface;
import kifio.leningrib.platform.items.StoreItem;

import static com.android.billingclient.api.BillingClient.BillingResponseCode.OK;

public class Store implements StoreInterface {

    private BillingClient billingClient;
    private Activity ctx;
    private boolean hasBillingConnection = false;
    private List<SkuDetails> skuDetailsList = new ArrayList<>();

    Store(Activity ctx) {
        this.ctx = ctx;
    }

    @Override
    public void loadPurchases(OnPurchasesLoadedListener listener) {
        Purchase.PurchasesResult purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
        ArrayList<String> skuList = new ArrayList<>();
        for (Purchase p : purchasesResult.getPurchasesList()) {
            skuList.add(p.getSku());
        }
        listener.onPurchasesLoaded(skuList);
    }

    @Override
    public void setup(final OnBillingInitializedListener listener,
                      final OnUpdatePurchasesListener updatePurchasesListener) {
        if (billingClient == null) {
            hasBillingConnection = false;
            billingClient = BillingClient.newBuilder(ctx).setListener(new PurchasesUpdatedListener() {
                @Override
                public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
                    if (billingResult.getResponseCode() == OK
                            && list != null) {
                        for (Purchase purchase : list) {
                            if (verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                                if (!purchase.isAcknowledged()) {
                                    acknowledgePurchase(purchase, updatePurchasesListener);
                                } else {
                                    updatePurchasesListener.onUpdatePurchases(purchase.getSku());
                                }
                            }
                        }
                    } else {
                        // Handle any other error codes.
                        Gdx.app.log("kifio", "Billing response code: " + billingResult.getResponseCode() + "\n" + billingResult.getDebugMessage());
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
                if (billingResult.getResponseCode() == OK) {
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
        if (billingClient != null) {
            billingClient.endConnection();
            billingClient = null;
        }
        hasBillingConnection = false;
    }

    @Override
    public void loadSku(final OnStoreItemsLoadedListener listener) {
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(Arrays.asList(SKU_LIST)).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == OK) {
                            Store.this.skuDetailsList.clear();
                            Store.this.skuDetailsList.addAll(skuDetailsList);
                        }

                        if (listener != null) {
                            updateSkuDetails(listener);
                        }
                    }
                });
    }

    private void updateSkuDetails(OnStoreItemsLoadedListener listener) {
        ArrayList<StoreItem> items = new ArrayList<>();
        List<String> skuList = Arrays.asList(SKU_LIST);
        for (SkuDetails details : skuDetailsList) {
            String description = details.getTitle() + " " + details.getDescription();
            description = description.replace(" (Leningrib)", "");
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
        BillingResult result = billingClient.launchBillingFlow(ctx, flowParams);
        Gdx.app.log("kifio", "Billing response code: " + result.getResponseCode() + "\n" + result.getDebugMessage());
    }

    private void acknowledgePurchase(final Purchase purchase,
                                     final OnUpdatePurchasesListener listener) {

        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                listener.onUpdatePurchases(purchase.getSku());
            }
        });
    }

    private boolean verifyValidSignature(String signedData, String signature) {
        try {
            return Security.verifyPurchase(signedData, signature);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
