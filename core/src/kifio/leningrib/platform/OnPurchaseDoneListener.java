package kifio.leningrib.platform;

public interface OnPurchaseDoneListener {
    void onPurchaseDone(String sku, String token, String payload);
}
