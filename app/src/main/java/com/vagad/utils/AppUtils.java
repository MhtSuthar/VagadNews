package com.vagad.utils;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;

import com.vagad.storage.SharedPreferenceUtil;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Admin on 19-Feb-17.
 */

public class AppUtils {

    private static final String TAG = "AppUtils";

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void getDisplayName(Context context) {
        Cursor c = context.getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        c.moveToFirst();
        Log.e(TAG, "Bae: " + c.getString(c.getColumnIndex("display_name")));
        c.close();
    }

    public static boolean isSameDomain(String url, String url1) {
        return getRootDomainUrl(url.toLowerCase()).equals(getRootDomainUrl(url1.toLowerCase()));
    }

    private static String getRootDomainUrl(String url) {
        String[] domainKeys = url.split("/")[2].split("\\.");
        int length = domainKeys.length;
        int dummy = domainKeys[0].equals("www") ? 1 : 0;
        if (length - dummy == 2)
            return domainKeys[length - 2] + "." + domainKeys[length - 1];
        else {
            if (domainKeys[length - 1].length() == 2) {
                return domainKeys[length - 3] + "." + domainKeys[length - 2] + "." + domainKeys[length - 1];
            } else {
                return domainKeys[length - 2] + "." + domainKeys[length - 1];
            }
        }
    }

    public static String getBase64Image(String path) {
        String base64Image = "";
        if (path != null && path != "") {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8; // shrink it down otherwise we will use stupid amounts of memory
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                byte[] bytes = baos.toByteArray();
                base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return base64Image;
    }

    public synchronized static String getUniqueId(Context context) {
        String uniqueID = SharedPreferenceUtil.getString(Constants.PREF_UNIQUE_ID, null);
        if (uniqueID == null) {
            uniqueID = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            SharedPreferenceUtil.putValue(Constants.PREF_UNIQUE_ID, uniqueID);
            SharedPreferenceUtil.save();
        }
        Log.e(TAG, "getUniqueId: "+uniqueID);
        return uniqueID;
    }


}
