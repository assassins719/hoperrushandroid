package com.hoperrush.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.hoperrush.R;
import com.hoperrush.hockeyapp.ActionBarActivityHockeyApp;

import java.util.ArrayList;
import java.util.List;

public class Viewprofilefragment extends ActionBarActivityHockeyApp {


    private TabLayout tabLayout;
    private ViewPager profile_viewPager;
    private RelativeLayout layout_profile_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_partner_profile_page);
        profile_viewPager = (ViewPager) findViewById(R.id.profilepage_viewpager);
        layout_profile_back = (RelativeLayout) findViewById(R.id.layout_back_profilepage);
        setupViewPager(profile_viewPager);

        tabLayout = (TabLayout) findViewById(R.id.profilepage_tabs);
        tabLayout.setupWithViewPager(profile_viewPager);

        layout_profile_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TaskerProfileView(), "Tasker Profile");
        adapter.addFragment(new TaskerReviewclass(), "Tasker Review");
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
       /* if (!socketHandler.getSocketManager().isConnected){
            socketHandler.getSocketManager().connect();
        }*/
    }


}
