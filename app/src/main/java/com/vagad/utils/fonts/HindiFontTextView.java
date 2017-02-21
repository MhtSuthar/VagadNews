package com.vagad.utils.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Andriod-176 on 10/21/2016.
 */

public class HindiFontTextView extends TextView {

    public HindiFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public HindiFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HindiFontTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/DroidHindi.ttf");
        setTypeface(tf);
    }
}

