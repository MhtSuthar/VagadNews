package com.vagad.dashboard;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;

import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.vagad.R;
import com.vagad.base.BaseActivity;
import com.vagad.model.RSSItem;
import com.vagad.utils.Constants;
import com.vagad.utils.image.TouchImageView;

import java.io.IOException;

/**
 * Created by Admin on 29-Mar-17.
 */

public class PreviewImageActivity extends BaseActivity {

    private static final String TAG = "PreviewImageActivity";
    private RSSItem rssItem;
    private TouchImageView previewImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //statusBarColor(Color.BLACK);
        statusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        setContentView(R.layout.activity_preview_image);
        fullScreen();
        init();
    }

    private void fullScreen() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public static  byte[]  decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = Base64.decode(image, Base64.DEFAULT);
        return decodedByteArray;
    }

    private void init() {
        rssItem = getIntent().getParcelableExtra(Constants.Bundle_Feed_Item);
        previewImage = (TouchImageView) findViewById(R.id.previewImage);
        Log.e(TAG, "init: image "+Constants.mClickImagePath);
        if(rssItem.getImage().contains("png") || rssItem.getImage().contains("jpg"))
            Glide.with(this).load(rssItem.getImage()).placeholder(R.drawable.ic_placeholder).into(previewImage);
        else {
            try {
                Glide.with(this).load(decodeFromFirebaseBase64(Constants.mClickImagePath)).placeholder(R.drawable.ic_placeholder).into(previewImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }
}
