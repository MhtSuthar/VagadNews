package com.vagad.prestart.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vagad.R;
import com.vagad.base.BaseFragment;
import com.vagad.prestart.HelpActivity;

/**
 * Created by Admin on 24-Feb-17.
 */

public class HelpPagerFragment extends BaseFragment {

    private ImageView imgHelp;
    private int[] mImages = new int[]{R.drawable.splash_bg, R.drawable.help_two, R.drawable.help_three, R.drawable.help_four};
    private String[] mDesc = new String[]{"Simple UI and easy to use only one finger to access.", "All Dungarpur or Banswara bus route available only on in this app.",
            "Add your news to Favourites and Read anytime", "No worry about your Internet connection, use offline"};
    private String[] mTitle = new String[]{"Easy To Use", "Bus Details", "Favourite", "Offline"};
    private TextView txtDesc, txtTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgHelp = (ImageView) view.findViewById(R.id.imgHelp);
        txtDesc = (TextView) view.findViewById(R.id.txtDesc);
        txtTitle = (TextView) view.findViewById(R.id.txtTitle);

        imgHelp.setImageResource(mImages[getArguments().getInt(HelpActivity.Position)]);
        txtDesc.setText(mDesc[getArguments().getInt(HelpActivity.Position)]);
        txtTitle.setText(mTitle[getArguments().getInt(HelpActivity.Position)]);
    }
}
