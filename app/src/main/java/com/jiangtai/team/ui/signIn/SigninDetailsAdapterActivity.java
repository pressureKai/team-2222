package com.jiangtai.team.ui.signIn;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.jiangtai.team.R;

import com.jiangtai.team.ui.signIn.viewpager.But1Fragment;
import com.jiangtai.team.ui.signIn.viewpager.But2Fragment;
import com.jiangtai.team.ui.signIn.viewpager.ButFragment;
import com.jiangtai.team.ui.signIn.viewpager.CustomViewPager;


import java.util.ArrayList;
import java.util.List;

public class SigninDetailsAdapterActivity extends AppCompatActivity {

    private CustomViewPager viewpager;
    private RadioGroup group;
    private RadioButton but;
    private RadioButton but1;
    private RadioButton but2;
    private List<Fragment> list;
    private RelativeLayout kq_return_clear;
    private LinearLayout kq_return_ll1, kq_return_ll2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_xingqing);
        viewpager = findViewById(R.id.viewpager);
        kq_return_clear = findViewById(R.id.kq_return_clear);

        group = findViewById(R.id.group);
        but = findViewById(R.id.but);
        but1 = findViewById(R.id.but1);
        but2 = findViewById(R.id.but2);
        kq_return_ll1 = findViewById(R.id.kq_return_ll1);
        kq_return_ll2 = findViewById(R.id.kq_return_ll2);
        getWindow().setStatusBarColor((Color.TRANSPARENT));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        ButFragment butFragment = new ButFragment();
        But1Fragment but1Fragment = new But1Fragment();
        But2Fragment but2Fragment = new But2Fragment();

        list = new ArrayList<>();
        list.add(butFragment);
        list.add(but1Fragment);
        list.add(but2Fragment);
        kq_return_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        kq_return_ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        viewpager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return list.get(i);
            }

            @Override
            public int getCount() {
                return list.size();
            }
        });
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        group.check(R.id.but);
                        but.setBackgroundColor(Color.parseColor("#234C7C"));
                        but1.setBackgroundColor(Color.parseColor("#0C1F3D"));
                        but2.setBackgroundColor(Color.parseColor("#0C1F3D"));
                        break;
                    case 1:
                        group.check(R.id.but1);
                        but1.setBackgroundColor(Color.parseColor("#234C7C"));
                        but.setBackgroundColor(Color.parseColor("#0C1F3D"));
                        but2.setBackgroundColor(Color.parseColor("#0C1F3D"));
                        break;
                    case 2:
                        group.check(R.id.but2);
                        but2.setBackgroundColor(Color.parseColor("#234C7C"));
                        but1.setBackgroundColor(Color.parseColor("#0C1F3D"));
                        but.setBackgroundColor(Color.parseColor("#0C1F3D"));
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.but:
                        viewpager.setCurrentItem(0);
                        but.setBackgroundColor(Color.parseColor("#234C7C"));
                        but1.setBackgroundColor(Color.parseColor("#0C1F3D"));
                        but2.setBackgroundColor(Color.parseColor("#0C1F3D"));
                        break;
                    case R.id.but1:
                        viewpager.setCurrentItem(1);
                        but1.setBackgroundColor(Color.parseColor("#234C7C"));
                        but.setBackgroundColor(Color.parseColor("#0C1F3D"));
                        but2.setBackgroundColor(Color.parseColor("#0C1F3D"));
                        break;
                    case R.id.but2:
                        viewpager.setCurrentItem(2);
                        but2.setBackgroundColor(Color.parseColor("#234C7C"));
                        but1.setBackgroundColor(Color.parseColor("#0C1F3D"));
                        but.setBackgroundColor(Color.parseColor("#0C1F3D"));

                        break;

                }
            }
        });
        viewpager.setCurrentItem(0);
        but.setChecked(true);

    }
}