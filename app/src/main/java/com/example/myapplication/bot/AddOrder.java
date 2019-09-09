package com.example.myapplication.bot;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Locale;

public class AddOrder {
    private String typeOrder;
    private double price;
    private double value;
    private String para;
    private String priceStr;

    public AddOrder(double price, double value, String typeOrder, String para) {
        this.price = price;
        this.value = value;
        this.typeOrder = typeOrder;
        this.para = para;
        this.priceStr = String.format(Locale.ROOT,"%.8f", price);
    }

    public String getPara() {
        return para;
    }

    public void setPara(String para) {
        this.para = para;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getTypeOrder() {
        return typeOrder;
    }

    public void setTypeOrder(String typeOrder) {
        this.typeOrder = typeOrder;
    }

    public String getPriceStr() {
        return priceStr;
    }

    public void setPriceStr(String priceStr) {
        this.priceStr = priceStr;
    }
}
