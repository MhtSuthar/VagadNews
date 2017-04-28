package com.vagad.localnews;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vagad.R;
import com.vagad.base.BaseActivity;
import com.vagad.dashboard.FavListActivity;
import com.vagad.dashboard.adapter.FavNewsRecyclerAdapter;
import com.vagad.localnews.adapter.ReportNewsRecyclerAdapter;
import com.vagad.model.NewsPostModel;
import com.vagad.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 15-Feb-17.
 */

public class ReporterNewsListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ReportNewsRecyclerAdapter mReportNewsRecyclerAdapter;
    private static final String TAG = "ReporterNewsListActivity";
    private List<NewsPostModel> mListNews = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setupExplodeWindowAnimations(Gravity.BOTTOM);
        setContentView(R.layout.fragment_fav_list);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        setAdapter();
        getValFromFirebase();
    }

    private void getValFromFirebase() {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_NEWS);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange: "+dataSnapshot.getKey()+"   "+dataSnapshot.getRef()+""+dataSnapshot.getChildren()+"   "+dataSnapshot.getChildrenCount());
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    NewsPostModel changedPost = messageSnapshot.getValue(NewsPostModel.class);
                    Log.e(TAG, "for : "+changedPost.nameReporter);
                    mListNews.add(changedPost);
                }
                setAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void setAdapter() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReportNewsRecyclerAdapter = new ReportNewsRecyclerAdapter(mListNews, this, ReporterNewsListActivity.this);
        recyclerView.setAdapter(mReportNewsRecyclerAdapter);
    }

    private void fullScreen() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    public void setOnItemClick(int position, ImageView imgNews) {

    }
}
