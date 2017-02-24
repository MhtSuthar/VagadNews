package com.vagad.prestart.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vagad.R;
import com.vagad.base.BaseFragment;

/**
 * Created by Admin on 24-Feb-17.
 */

public class HelpPagerFragment extends BaseFragment {

    private ImageView imgHelp;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_pager, container, false);
    }
}
