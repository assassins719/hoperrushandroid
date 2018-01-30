package com.hoperrush.app;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.hoperrush.R;
import com.hoperrush.fragment.MyTaskTransaction;
import com.hoperrush.fragment.MyWalletTransaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionTabActivity extends FragmentActivity {

    private ViewPager myViewpager;
    private RelativeLayout myBackLAY;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_tab);

        classAndWidgetInitialize();

        myBackLAY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }

    private void classAndWidgetInitialize() {
        myViewpager = (ViewPager) findViewById(R.id.screen_transaction_tab_viewpager);
        myBackLAY = (RelativeLayout) findViewById(R.id.layout_back_transaction_tab);
        setupViewPager(myViewpager);
        tabLayout = (TabLayout) findViewById(R.id.screen_transaction_tab_tabs);
        tabLayout.setupWithViewPager(myViewpager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MyTaskTransaction(), "Task Transaction");
        adapter.addFragment(new MyWalletTransaction(), "Wallet Transaction");
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

}
