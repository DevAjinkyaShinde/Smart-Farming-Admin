package com.silentcodder.smartfarmingadmin.Model;

public class ProductData extends ProductId {
    String Category;
    String ProductName;
    String Description;
    String Unit;
    String Price;
    String ProductImgUrl;
    Long TimeStamp;

    public ProductData() {
    }

    public ProductData(String category, String productName, String description, String unit, String price, String productImgUrl, Long timeStamp) {
        Category = category;
        ProductName = productName;
        Description = description;
        Unit = unit;
        Price = price;
        ProductImgUrl = productImgUrl;
        TimeStamp = timeStamp;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getProductImgUrl() {
        return ProductImgUrl;
    }

    public void setProductImgUrl(String productImgUrl) {
        ProductImgUrl = productImgUrl;
    }

    public Long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        TimeStamp = timeStamp;
    }
}
