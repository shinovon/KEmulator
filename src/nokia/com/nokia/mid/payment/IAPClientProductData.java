package com.nokia.mid.payment;

public class IAPClientProductData {
    public static final int OTHER_DRM = 0;
    public static final int NOKIA_DRM = 1;
    private String price;
    private String longDesc;
    private String shortDesc;
    private String title;
    private String productId;

    IAPClientProductData(String pid, String title, String sdesc, String ldesc, String price) {
        this.productId = pid;
        this.title = title;
        this.shortDesc = sdesc;
        this.longDesc = ldesc;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public String getTitle() {
        return title;
    }

    public String getShortDescription() {
        return shortDesc;
    }

    public String getLongDescription() {
        return longDesc;
    }

    public String getPrice() {
        return price;
    }

    public int getDrmProtection() {
        return 0;
    }
}
