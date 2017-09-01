package com.vagad.music;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vagad.R;
import com.vagad.base.BaseActivity;
import com.vagad.dashboard.adapter.MoreNewsRecyclerAdapter;
import com.vagad.dashboard.fragments.MoreNewsListFragment;
import com.vagad.model.MusicModel;
import com.vagad.model.NewsPostModel;
import com.vagad.model.RSSItem;
import com.vagad.music.adapter.MusicListRecyclerAdapter;
import com.vagad.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Admin on 25-Jun-17.
 */

public class VagadMusicActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabase;
    private static final String TAG = "VagadMusicActivity";
    private List<MusicModel> mListNews = new ArrayList<>();
    private MusicListRecyclerAdapter mMusicListRecyclerAdapter;
    private MusicModel mMusicModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.fragment_fav_list);
        checkPermission();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        setAdapter();
        getValFromFirebase();
    }

    private void fullScreen() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void checkPermission() {
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, this) &&
                checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, this)) {
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, Constants.REQUEST_PERMISSION_WRITE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_PERMISSION_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadMusic();
            }
        }
    }


    private void getValFromFirebase() {
        if (isOnline(this)) {
            mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_MUSIC);
            mDatabase.addValueEventListener(valueEventListener);
        } else {
            showSnackbar(mRecyclerView, getString(R.string.no_internet));
        }
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                MusicModel changedPost = messageSnapshot.getValue(MusicModel.class);
                //changedPost.key = dataSnapshot.getKey();
                Log.e(TAG, "for : " + changedPost.name + " key   " + changedPost.key + "   " + changedPost.file_path);
                mListNews.add(changedPost);
            }
            Log.e(TAG, "mListNews Size " + mListNews.size());
            //Collections.reverse(mListNews);
            setAdapter();
            mDatabase.removeEventListener(this);
        }


        @Override
        public void onCancelled(DatabaseError databaseError) {
            System.out.println("The read failed: " + databaseError.getCode());
            //progressBar.setVisibility(View.GONE);
        }
    };

    private void setAdapter() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMusicListRecyclerAdapter = new MusicListRecyclerAdapter(mListNews, this, VagadMusicActivity.this);
        mRecyclerView.setAdapter(mMusicListRecyclerAdapter);
    }

    public void setOnItemClick(MusicModel musicModel) {
        showSnackbar(mRecyclerView, "click  " + musicModel.file_path);
        this.mMusicModel = musicModel;
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, this) &&
                checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, this)) {
            downloadMusic();

        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    , Constants.REQUEST_PERMISSION_WRITE_STORAGE);
        }
    }

    private void downloadMusic() {
        if(mMusicModel != null) {
            File direct = new File(Environment.getExternalStorageDirectory()
                    + "/VagadNews/Music");

            if (!direct.exists()) {
                direct.mkdirs();
            }

            DownloadManager mgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
            Log.e(TAG, "downloadMusic: "+mMusicModel.file_path);
            Uri downloadUri = Uri.parse(mMusicModel.file_path);
            DownloadManager.Request request = new DownloadManager.Request(
                    downloadUri);

            request.setTitle("Vagad Music")
                    .setDescription(mMusicModel.name)
                    .setDestinationInExternalPublicDir("/VagadNews/Music", mMusicModel.name.replaceAll(" ", "") + ".mp3");

            mgr.enqueue(request);
        }
    }
}
