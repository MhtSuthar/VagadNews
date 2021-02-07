package com.vagad.utils.rating;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;

import com.vagad.R;
import com.vagad.storage.SharedPreferenceUtil;

public class RateItDialogFragment extends DialogFragment {

    private static final int LAUNCHES_UNTIL_PROMPT = 3;
    private static final int DAYS_UNTIL_PROMPT = 3;
    private static final String LAST_PROMPT = "LAST_PROMPT";
    private static final String LAUNCHES = "LAUNCHES";
    private static final String DISABLED = "DISABLED";
    private static final String TAG = "RateItDialogFragment";

    public static void show(Context context, FragmentManager fragmentManager) {
        boolean shouldShow = false;
        long currentTime = System.currentTimeMillis();
        long lastPromptTime = SharedPreferenceUtil.getLong(LAST_PROMPT, 0);
        if (lastPromptTime == 0) {
            lastPromptTime = currentTime;
        }

        if (!SharedPreferenceUtil.getBoolean(DISABLED, false)) {
            int launches = SharedPreferenceUtil.getInt(LAUNCHES, 0) + 1;
            if (launches > LAUNCHES_UNTIL_PROMPT) {
                shouldShow = true;
            }
            SharedPreferenceUtil.putValue(LAUNCHES, launches);
        }

        if (shouldShow) {
            SharedPreferenceUtil.putValue(LAUNCHES, 0);
            SharedPreferenceUtil.save();
            new RateItDialogFragment().show(fragmentManager, null);
        } else {
            SharedPreferenceUtil.save();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.rate_title)
                .setMessage(R.string.rate_message)
                .setPositiveButton(R.string.rate_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName())));
                        SharedPreferenceUtil.putValue(DISABLED, true);
                        SharedPreferenceUtil.save();
                        dismiss();
                    }
                })
                .setNeutralButton(R.string.rate_remind_later, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.rate_never, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferenceUtil.putValue(DISABLED, true);
                        SharedPreferenceUtil.save();
                        dismiss();
                    }
                }).create();
    }
}