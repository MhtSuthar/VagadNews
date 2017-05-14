package com.vagad.localnews;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vagad.R;
import com.vagad.base.BaseActivity;
import com.vagad.dashboard.FavListActivity;
import com.vagad.dashboard.NewsDetailActivity;
import com.vagad.dashboard.adapter.FavNewsRecyclerAdapter;
import com.vagad.localnews.adapter.ReportNewsRecyclerAdapter;
import com.vagad.model.NewsPostModel;
import com.vagad.model.RSSItem;
import com.vagad.utils.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
        //initSwipe();
        getValFromFirebase();
    }

    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder.getItemViewType() == 0) return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT){
                    //DElete
                    Toast.makeText(ReporterNewsListActivity.this, "Left "+position, Toast.LENGTH_SHORT).show();
                } else {
                   //Edit
                    Toast.makeText(ReporterNewsListActivity.this, "Right " + position, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    Paint p = new Paint();
                    if(dX > 0){
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_back);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_close);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void getValFromFirebase() {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_NEWS);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange: "+dataSnapshot.getKey()+"   "+dataSnapshot.getRef()+""+dataSnapshot.getChildren()+"   "+dataSnapshot.getChildrenCount());
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    NewsPostModel changedPost = messageSnapshot.getValue(NewsPostModel.class);
                    Log.e(TAG, "for : "+changedPost.nameReporter+"   "+changedPost.newsDesc+"   "+changedPost.newsTitle);
                    if(changedPost.isVisible)
                        mListNews.add(changedPost);
                }
                Collections.reverse(mListNews);
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
        Intent intent = new Intent(this, NewsDetailActivity.class);
        intent.putExtra(Constants.Bundle_Is_From_News_List, true);
        intent.putExtra(Constants.Bundle_Is_From_Local_News, true);
        intent.putExtra(Constants.Bundle_Feed_Item, getRssItem(mListNews.get(position - 1)));
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, imgNews, "profile");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startActivityForResult(intent, Constants.REQUEST_CODE_NEWS_DETAIL, options.toBundle());
        }else{
            startActivityForResult(intent, Constants.REQUEST_CODE_NEWS_DETAIL);
        }
    }

    private RSSItem getRssItem(NewsPostModel newsPostModel) {
        RSSItem rssItem = new RSSItem(newsPostModel.newsTitle, newsPostModel.nameReporter, newsPostModel.newsDesc, ""+newsPostModel.timestamp, "", newsPostModel.image, newsPostModel.mobileNo);
        return rssItem;
    }
}
