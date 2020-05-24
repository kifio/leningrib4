package kifio.leningrib.platform;

import org.jetbrains.annotations.Nullable;

public interface StoreInterface {

    public void setup(OnBillingInitializedListener listener);
    public void dispose();

    // Load and cache purchases.
    // If loading failed, try to use cached values.
    // If listener is null, cache, but do not update UI.
    public void loadPurchases(@Nullable OnPurchasesLoadedListener listener);

    public void launchBillingFlow(int index);
}
