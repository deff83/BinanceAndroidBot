package com.example.myapplication.bot;

import java.util.Locale;

public class MaxVol {
    private double maxVol = 10.0;

    public double getMaxVol() {
        return maxVol;
    }

    public void setMaxVol(double maxVol) {
        this.maxVol = maxVol;
    }
    public String getStrVol(){
        return String.format(Locale.ROOT,"%.8f", maxVol);
    }
}
