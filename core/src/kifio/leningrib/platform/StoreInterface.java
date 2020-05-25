package kifio.leningrib.platform;

public interface StoreInterface {

    String[] SKU_LIST = {
            "3_vodka_bottles",
            "5_vodka_bottles",
            "gum_1",
            "gum_2"
    };

    void loadPurchases(OnPurchasesLoadedListener listener);

    void setup(OnBillingInitializedListener listener, OnUpdatePurchasesListener consumeListener);
    void dispose();

    // Load and cache purchases.
    // If loading failed, try to use cached values.
    // If listener is null, cache, but do not update UI.
    void loadSku(OnStoreItemsLoadedListener listener);

    void launchBillingFlow(int index);
}
