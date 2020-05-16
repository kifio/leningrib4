package kifio.leningrib;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import java.util.Collections;

import kifio.leningrib.platform.OnPurchasesLoadedListener;
import kifio.leningrib.platform.StoreInterface;
import kifio.leningrib.platform.items.StoreItem;

public class Store implements StoreInterface {

    @Override
    public void loadPurchases(final OnPurchasesLoadedListener listener) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                listener.onPurchasesLoaded(Collections.singletonList(new StoreItem(
                        0, "0.00Р>", "Бабкин самогон. Очень популярен у лесников."
                )));
            }
        }, 200L);
    }
}
