package com.vagad.base;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import com.vagad.R;
import com.vagad.storage.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ubuntu on 15/9/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    protected int FINISH_TIME = 400;
    protected int ANIM_TIME = 300;
    private Dialog dialog;

    protected boolean checkPermission(String strPermission, Context context){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int result = ContextCompat.checkSelfPermission(context, strPermission);
            if (result == PackageManager.PERMISSION_GRANTED){
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    protected void finishWithHandler(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, FINISH_TIME);
    }

    protected void statusBarColor(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(color);
        }
    }


    protected void closeKeyBoard(Activity context) {
        View view =  context.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected boolean isOnline(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     *
     * @param fragment Add And Replace
     */
    /*protected void addFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activityContent, fragment, fragment.getTag()).addToBackStack(fragment.getTag()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }

    protected void replaceFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activityContent, fragment, fragment.getTag()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }

    protected void replaceFragmentWithoutAnim(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activityContent, fragment, fragment.getTag()).commit();
    }*/

    /**
     *
     * @param gravity Activity Material Transition
     */

    protected void setupExplodeWindowAnimations(int gravity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Explode fade = new Explode();
            fade.setDuration(ANIM_TIME);
            getWindow().setEnterTransition(fade);

            Slide slide = new Slide(gravity);
            slide.setDuration(ANIM_TIME);
            getWindow().setReturnTransition(slide);
        }
    }

    protected void setupFadeWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.setDuration(ANIM_TIME);
            getWindow().setEnterTransition(fade);
        }
    }

    protected void setupSlideWindowAnimations(int startGravity, int endGravity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide(startGravity);
            slide.setDuration(ANIM_TIME);
            getWindow().setEnterTransition(slide);

            slide = new Slide(endGravity);
            slide.setDuration(ANIM_TIME);
            getWindow().setReturnTransition(slide);
        }
    }

    protected void setupExplodeAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Explode fade = new Explode();
            fade.setDuration(ANIM_TIME);
            getWindow().setEnterTransition(fade);

            Explode slide = new Explode();
            slide.setDuration(ANIM_TIME);
            getWindow().setReturnTransition(slide);
        }
    }

    protected void showToast(String msg){
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    protected void showSnackbar(View view, String msg){
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }

    protected void showErrorLog(String error){
        Log.e(TAG, ""+error);
    }

    protected void moveActivity(Intent intent, Activity context, boolean isFinish){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation(context).toBundle());
            } else {
                startActivity(intent);
            }
            if(isFinish)
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 500);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void startActivityForResults(Intent intent, Activity context, boolean isFinish, int requestCode){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivityForResult(intent, requestCode,
                        ActivityOptions.makeSceneTransitionAnimation(context).toBundle());
            } else {
                startActivityForResult(intent, requestCode);
            }
            if(isFinish)
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 500);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void showAlertDialog(String title, String msg){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    protected void showAlertDialog(final BaseFragment.OnDialogClick onDialogClick, String title, String msg, boolean isNegative){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(isNegative ? "Yes" : "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDialogClick.onPositiveBtnClick();
            }
        });
        if(isNegative)
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onDialogClick.onNegativeBtnClick();
                }
            });
        builder.show();
    }

    protected void showProgressDialog() {
        if (dialog != null) {
            if (dialog.isShowing())
                dialog.dismiss();
            dialog = null;
        }
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.layout_progress_dialog);
        dialog.show();
    }

    protected void stopProgressDialog() {
        if (dialog != null) {
            if (dialog.isShowing())
                dialog.dismiss();
            dialog = null;
        }
    }



   /* void setAlarmForAutoLogout(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, SharedPreferenceUtil.getInt(Constants.KEY_AUTO_LOGOUT_MIN, 30));
        AlarmManager alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
        alarmMgr.set(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(), alarmIntent);
    }*/

}
