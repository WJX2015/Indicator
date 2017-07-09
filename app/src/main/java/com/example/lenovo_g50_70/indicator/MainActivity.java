package com.example.lenovo_g50_70.indicator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ViewPagerIndicator mIndicator;

    private List<String> mTitles = Arrays.asList("短信1", "收藏2", "推荐3", "短信4", "收藏5", "推荐6", "短信7", "收藏8", "推荐9");
    private List<VpSimpleFragment> mFragments = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initViews();
        initDatas();

        //动态添加tab
        //mIndicator.setVisibleTabCount(3);
        //mIndicator.setTabItem(mTitles);

        mViewPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mViewPager, 0);
    }

    /**
     * 初始化数据
     */
    private void initDatas() {
        for (String title : mTitles) {
            VpSimpleFragment vpf = VpSimpleFragment.newInstance(title);
            mFragments.add(vpf);
        }
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mIndicator = (ViewPagerIndicator) findViewById(R.id.indicator);
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        };
    }
}
