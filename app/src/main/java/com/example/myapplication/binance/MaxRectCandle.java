package com.example.myapplication.binance;

public class MaxRectCandle {
    private double maxPrice;
    private double minPrice;

    public MaxRectCandle(double maxPrice, double minPrice) {
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

}
