package com.vagad.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;

import com.vagad.R;
import com.vagad.base.BaseActivity;
import com.vagad.dashboard.fragments.CategoryNewsFragment;
import com.vagad.dashboard.fragments.MoreNewsFragment;

/**
 * Created by Admin on 25-Jun-17.
 */

public class MoreNewsActivity extends BaseActivity {

    private CategoryNewsFragment categoryNewsFragment = new CategoryNewsFragment();
    private MoreNewsFragment moreNewsFragment = new MoreNewsFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        setupExplodeWindowAnimations(Gravity.RIGHT);
        setContentView(R.layout.layout_frame);

        getSupportFragmentManager().beginTransaction().
                replace(R.id.container, categoryNewsFragment, categoryNewsFragment.getTag()).commit();
    }
}
