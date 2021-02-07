package com.vagad.dashboard;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.vagad.R;
import com.vagad.base.BaseActivity;

/**
 * Created by Admin on 15-Feb-17.
 */

public class AboutUsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setupExplodeWindowAnimations(Gravity.BOTTOM);
        setContentView(R.layout.fragment_about_us);
        findViewById(R.id.fab_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCompat.IntentBuilder.from(AboutUsActivity.this)
                        .setType("message/rfc822")
                        .addEmailTo(getString(R.string.my_email))
                        .setSubject("Your Subject")
                        .setText("")
                        //.setHtmlText(body) //If you are using HTML in your body text
                        .setChooserTitle("Send Suggestions & Requirement")
                        .startChooser();
            }
        });
    }

    private void fullScreen() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


}
