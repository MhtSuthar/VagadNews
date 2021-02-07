package com.vagad.dashboard;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.WindowManager;

import com.vagad.R;
import com.vagad.base.BaseActivity;
import com.vagad.base.VagadApp;
import com.vagad.dashboard.fragments.MoreNewsListFragment;
import com.vagad.model.RSSItem;
import com.vagad.utils.Constants;

import java.util.List;

/**
 * Created by Admin on 25-Jun-17.
 */

public class MoreNewsActivity extends BaseActivity {

    private MoreNewsListFragment entertainmentListFragment = new MoreNewsListFragment();
    private static final String TAG = "MoreNewsActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: MOre News ");
        fullScreen();
        setContentView(R.layout.layout_frame);

        Bundle bundle = new Bundle();
        if(getIntent().hasExtra(Constants.EXTRA_MORE_NEWS_TYPE)) {
            bundle.putString(Constants.EXTRA_MORE_NEWS_TYPE, "" + getIntent().getExtras().get(Constants.EXTRA_MORE_NEWS_TYPE));
        }else{
            List<RSSItem> mList = ((VagadApp)getApplication()).getmNewsList();
            Log.e(TAG, "onCreate: "+mList.size());
            entertainmentListFragment.setLatestNews(mList);
        }
        entertainmentListFragment.setArguments(bundle);

        //Bundle data = this.getIntent().getBundleExtra(Constants.Bundle_News);
        //ArrayList<RSSItem> mList = data.getParcelableArrayList(Constants.Bundle_Feed_List);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, entertainmentListFragment, entertainmentListFragment.getTag()).commit();
    }

    private void fullScreen() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
