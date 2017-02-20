package com.vagad.base;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.vagad.R;


/**
 * Created by ubuntu on 15/9/16.
 */
public abstract class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";
    protected int FINISH_TIME = 400;
    protected int ANIM_TIME = 300;
    private DisplayMetrics displayMetrics;
    private Dialog dialog;

    protected int getScreenWidth(){
        displayMetrics = this.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    protected int getScreenHeight(){
        displayMetrics = this.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    protected boolean checkPermission(String strPermission, Context context){
        int result = ContextCompat.checkSelfPermission(context, strPermission);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    protected void statusBarColor(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getActivity().getWindow().setStatusBarColor(color);
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
   /* protected void addFragment(Fragment fragment){
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.activityContent, fragment, fragment.getTag()).addToBackStack(fragment.getTag()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }

    protected void replaceFragment(Fragment fragment){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.activityContent, fragment, fragment.getTag()).addToBackStack(fragment.getTag()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }

    protected void addFragment(Fragment fragment, int containerId){
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(containerId, fragment, fragment.getTag()).addToBackStack(fragment.getTag()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }*/

    /**
     *
     * @param gravity Activity Material Transition
     */

    protected void setupExplodeWindowAnimations(int gravity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Explode fade = new Explode();
            fade.setDuration(ANIM_TIME);
            getActivity().getWindow().setEnterTransition(fade);

            Slide slide = new Slide(gravity);
            slide.setDuration(ANIM_TIME);
            getActivity().getWindow().setReturnTransition(slide);
        }
    }

    protected void setupSlideWindowAnimations(int startGravity, int endGravity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide(startGravity);
            slide.setDuration(ANIM_TIME);
            getActivity().getWindow().setEnterTransition(slide);

            slide = new Slide(endGravity);
            slide.setDuration(ANIM_TIME);
            getActivity().getWindow().setReturnTransition(slide);
        }
    }


    protected void showToast(String msg){
        Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG);
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
                        getActivity().finish();
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
                        getActivity().finish();
                    }
                }, 500);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void showAlertDialog(String title, String msg){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    protected void showAlertDialog(final OnDialogClick onDialogClick, String title, String msg, boolean isNegative){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
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

    public interface OnDialogClick {
        void onPositiveBtnClick();
        void onNegativeBtnClick();
    }

    protected void showProgressDialog() {
        if (dialog != null) {
            if (dialog.isShowing())
                dialog.dismiss();
            dialog = null;
        }
        dialog = new Dialog(getContext());
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



}
