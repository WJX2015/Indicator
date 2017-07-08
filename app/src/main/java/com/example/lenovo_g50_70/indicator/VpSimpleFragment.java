package com.example.lenovo_g50_70.indicator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by wjx on 2017/7/8.
 */

public class VpSimpleFragment extends Fragment {

    private String mTitle;
    public static final String BUNDLE_TITLE = "title";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString(BUNDLE_TITLE);
        }

        TextView textView = new TextView(getActivity());
        textView.setText(mTitle);
        textView.setGravity(Gravity.CENTER);

        return textView;
    }

    /**
     * 创建一个新的实例
     *
     * @param title
     * @return
     */
    public static VpSimpleFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TITLE, title);
        VpSimpleFragment fragment = new VpSimpleFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}
