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
        items.add(new StoreItem(
                0, "0.00Р", "3 бутылки водки. Отвлекает лесников."
        ));
        items.add(new StoreItem(
                1, "0.00Р", "5 бутылок водки. Отвлекает лесников."
        ));
        items.add(new StoreItem(
                2, "0.00Р", "Пачка жвачек. Снимает эфекты от грибов."
        ));
        items.add(new StoreItem(
                3, "0.00Р", "2 пачки жвачек. Снимает эфекты от грибов."
        ));
        listener.onPurchasesLoaded(items);
    }
}
