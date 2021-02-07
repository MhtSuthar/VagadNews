package com.vagad.busroute;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.vagad.R;
import com.vagad.base.BaseActivity;
import com.vagad.busroute.adapter.SearchRecyclerAdapter;
import com.vagad.model.BusListModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.vagad.utils.Constants.EXTRA_CITY_NAME;


/**
 * Created by Admin on 02-Jul-17.
 */

public class SearchCityActivity extends BaseActivity {

    RecyclerView mRecyclerView;
    EditText edtSearch;

    private SearchRecyclerAdapter searchRecyclerAdapter;
    private List<BusListModel> mListCity = new ArrayList<>();
    private List<BusListModel> mTempBusList;
    private static final String TAG = "SearchCityActivity";

    private final String TRIP_NO = "Trip_No";
    private final String NAME_OF_ROUTE = "NAME_OF_ROUTE";
    private final String VIA = "VIA";
    private final String DEP_TIME = "DEP_TIME";
    private final String ARR_TIME = "ARR_TIME";
    private final String ROUTE_KMS = "ROUTE_KMS";
    private final String CETEGORY_OF_SERVICE = "CETEGORY_OF_SERVICE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        edtSearch = (EditText) findViewById(R.id.edt_search);

        setRecyclerAdapter();

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onSearchContact(edtSearch.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void onSearchContact(String searchText) {
        Log.e(TAG, "onSearchContact: "+mListCity.size());
        if(searchText.equals("")){
            if(searchRecyclerAdapter != null){
                searchRecyclerAdapter.setSearchString(searchText);
                searchRecyclerAdapter.setSearchList(mListCity);
            }
        }else{
            if(searchRecyclerAdapter != null){
                List<BusListModel> mListFilterContact = new ArrayList<>();
                for (int i = 0; i < mListCity.size(); i++) {
                    if(mListCity.get(i).NAME_OF_ROUTE.toLowerCase().contains(searchText.toLowerCase())){
                        mListFilterContact.add(mListCity.get(i));
                    }
                }
                searchRecyclerAdapter.setSearchString(searchText);
                searchRecyclerAdapter.setSearchList(mListFilterContact);
            }
        }
    }


    private void setRecyclerAdapter() {
        mListCity.addAll(remove(getBusList()));

        mTempBusList = new ArrayList<>();
        mTempBusList.addAll(mListCity);
        searchRecyclerAdapter = new SearchRecyclerAdapter(this, mTempBusList, SearchCityActivity.this);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(searchRecyclerAdapter);
    }

    private List<BusListModel> remove(List<BusListModel> list) {
        List<BusListModel> mList = new ArrayList<>();
        Set<String> titles = new HashSet<>();

        for( BusListModel item : list ) {
            if(titles.add(item.NAME_OF_ROUTE)) {
                mList.add( item );
            }
        }
        return mList;
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
                busListModel.NAME_OF_ROUTE = object.getString(NAME_OF_ROUTE).contains("-") ? object.getString(NAME_OF_ROUTE).split("-")[0].trim().toString() : object.getString(NAME_OF_ROUTE).trim().toString();
                mList.add(busListModel);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.e(TAG, "getBusList: "+mList.size());
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

    public void onClickCity(String city) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CITY_NAME, city);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

   /* private List<ContactModel.Contact> removeDuplicateNumber(List<ContactModel.Contact> list1) {
        Map<String, ContactModel.Contact> cleanMap = new LinkedHashMap<String, ContactModel.Contact>();
        for (int i = 0; i < list1.size(); i++) {
            cleanMap.put(list1.get(i).getPhone(), list1.get(i));
        }
        List<ContactModel.Contact> list = new ArrayList<ContactModel.Contact>(cleanMap.values());
        return list;
    }*/


   /* public List<BusListModel> removeDuplicates(List<BusListModel> list){
        Set set = new TreeSet(new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                if(((BusListModel)o1).NAME_OF_ROUTE.equalsIgnoreCase(((BusListModel)o2).NAME_OF_ROUTE)){
                    return 0;
                }
                return 1;
            }
        });
        set.addAll(list);

        final ArrayList newList = new ArrayList(set);
        return newList;
    }*/
}
