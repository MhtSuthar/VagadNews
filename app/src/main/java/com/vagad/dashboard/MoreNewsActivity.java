package com.vagad.dashboard;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.WindowManager;

import com.vagad.R;
import com.vagad.base.BaseActivity;
import com.vagad.dashboard.fragments.MoreNewsListFragment;
import com.vagad.utils.Constants;

/**
 * Created by Admin on 25-Jun-17.
 */

public class MoreNewsActivity extends BaseActivity {

    private MoreNewsListFragment entertainmentListFragment = new MoreNewsListFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.layout_frame);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_MORE_NEWS_TYPE, ""+getIntent().getExtras().get(Constants.EXTRA_MORE_NEWS_TYPE));
        entertainmentListFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().
                replace(R.id.container, entertainmentListFragment, entertainmentListFragment.getTag()).commit();
    }

    private void fullScreen() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
