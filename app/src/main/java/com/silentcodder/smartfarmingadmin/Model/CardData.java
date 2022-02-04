package com.silentcodder.smartfarmingadmin.Model;

public class CardData extends CardId {
    long ItemCount;
    String ProductId;
    long TotalPrice;
    String TotalPrices;
    Long TimeStamp;
    String MainPrice;

    public CardData() {
    }

    public CardData(long itemCount, String productId, long totalPrice, String totalPrices, Long timeStamp, String mainPrice) {
        ItemCount = itemCount;
        ProductId = productId;
        TotalPrice = totalPrice;
        TotalPrices = totalPrices;
        TimeStamp = timeStamp;
        MainPrice = mainPrice;
    }

    public long getItemCount() {
        return ItemCount;
    }

    public void setItemCount(long itemCount) {
        ItemCount = itemCount;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public long getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        TotalPrice = totalPrice;
    }

    public String getTotalPrices() {
        return TotalPrices;
    }

    public void setTotalPrices(String totalPrices) {
        TotalPrices = totalPrices;
    }

    public Long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getMainPrice() {
        return MainPrice;
    }

    public void setMainPrice(String mainPrice) {
        MainPrice = mainPrice;
    }
}
