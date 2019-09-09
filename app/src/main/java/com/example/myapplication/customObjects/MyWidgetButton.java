package com.example.myapplication.customObjects;

import android.content.Context;


import androidx.annotation.Px;
import androidx.appcompat.widget.AppCompatButton;

public class MyWidgetButton extends AppCompatButton {
    public MyWidgetButton(Context context) {
        super(context);
    }

    @Override
    public void setMinHeight(@Px int minHeight) {
        super.setMinHeight(minHeight);
    }
    @Override
    public void setMaxHeight(@Px int maxHeight) {
        super.setMaxHeight(maxHeight);
    }



}
