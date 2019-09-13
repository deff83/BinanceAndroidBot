package com.example.myapplication.customObjects;

public class PerCentPrices {
    private  String name;
    private double percent;
    private  int period;

    public PerCentPrices(String name, double percent, int period) {
        this.name = name;
        this.percent = percent;
        this.period = period;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}

