package com.vagad.base;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.vagad.model.BusListModel;
import com.vagad.model.RSSItem;
import com.vagad.storage.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.vagad.utils.Constants.ARR_TIME;
import static com.vagad.utils.Constants.CETEGORY_OF_SERVICE;
import static com.vagad.utils.Constants.DEP_TIME;
import static com.vagad.utils.Constants.NAME_OF_ROUTE;
import static com.vagad.utils.Constants.ROUTE_KMS;
import static com.vagad.utils.Constants.TRIP_NO;
import static com.vagad.utils.Constants.VIA;


/**
 * Created by ubuntu on 15/9/16.
 */
public class VagadApp extends Application {

    private static FirebaseAnalytics mFirebaseAnalytics;
    private static final String TAG = "VagadApp";

    public List<RSSItem> getmNewsList() {
        return mNewsList;
    }

    public void setmNewsList(List<RSSItem> mNewsList) {
        this.mNewsList = mNewsList;
    }

    public List<RSSItem> mNewsList;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        SharedPreferenceUtil.init(getApplicationContext());
        setListBus(getBusList());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    List<BusListModel> getBusList(){
        List<BusListModel> mList = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(readJSONFromAsset());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                BusListModel busListModel = new BusListModel();
                busListModel.TRIP_NO = object.getString(TRIP_NO);
                busListModel.CETEGORY_OF_SERVICE = object.getString(CETEGORY_OF_SERVICE);
                busListModel.ARR_TIME = convertTime(object.getString(ARR_TIME));
                busListModel.DEP_TIME = convertTime(object.getString(DEP_TIME));
                busListModel.ROUTE_KMS = object.getString(ROUTE_KMS);
                busListModel.VIA = object.getString(VIA);
                busListModel.NAME_OF_ROUTE = object.getString(NAME_OF_ROUTE);
                mList.add(busListModel);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mList;
    }

    private String convertTime(String time) {
        String mTime = "";
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
            final Date dateObj = sdf.parse(time);
            System.out.println(dateObj);
            mTime = new SimpleDateFormat("hh:mm a").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return mTime;
    }

    String readJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("route/BusRoute.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private List<BusListModel> mListBus;

    public List<BusListModel> getListBus() {
        return mListBus;
    }

    public void setListBus(List<BusListModel> mListBus) {
        this.mListBus = mListBus;
    }

    public static FirebaseAnalytics getFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }

}
