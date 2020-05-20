package kifio.leningrib;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.Collections;

import kifio.leningrib.platform.OnPurchasesLoadedListener;
import kifio.leningrib.platform.StoreInterface;
import kifio.leningrib.platform.items.StoreItem;

public class Store implements StoreInterface {

    @Override
    public void loadPurchases(final OnPurchasesLoadedListener listener) {
        ArrayList<StoreItem> items = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            items.add(new StoreItem(
                    i, "0.00Р", "Cамогон. Популярен у лесников."
            ));
        }
        listener.onPurchasesLoaded(items);
    }
}
