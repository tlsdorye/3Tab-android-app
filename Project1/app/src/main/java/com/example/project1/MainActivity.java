package com.example.project1;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Tab2_Fragment.DeliveryData, Tab3_Fragment.Tab3DeliveryData{
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager= findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adatper = new SectionsPagerAdapter(getSupportFragmentManager());
        adatper.addFragment(new Tab1_Fragment(), "TAB1");
        adatper.addFragment(new Tab2_Fragment(),"TAB2");
        adatper.addFragment(new Tab3_Fragment(),"TAB3");
        viewPager.setAdapter(adatper);
    }

    public void deliver(ArrayList<HashMap<String,String>> data, int position){

        Intent intent = new Intent(MainActivity.this, Tab2_AlbumActivity.class);
        intent.putExtra("name",data.get(+position).get(Function.KEY_ALBUM));
        startActivity(intent);
    }

    public void deliver3(String videoId) {
        Intent intent = new Intent(MainActivity.this,tab3_VideoActivity.class);
        intent.putExtra("id",videoId);
        startActivity(intent);
    }
}

class SectionsPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
    public int getCount() {
        return mFragmentList.size();
    }
    public void addFragment(Fragment fragment, String title){
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }
}

