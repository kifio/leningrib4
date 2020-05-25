package kifio.leningrib.platform;

import java.util.List;

import kifio.leningrib.platform.items.StoreItem;

public interface OnStoreItemsLoadedListener {
    void onPurchasesLoaded(List<StoreItem> items);
}