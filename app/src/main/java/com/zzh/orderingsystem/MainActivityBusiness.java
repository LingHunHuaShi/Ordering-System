package com.zzh.orderingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivityBusiness extends AppCompatActivity {
    private ViewPager mViewPager;
    private BottomNavigationView mBottomNavigationView;
    private MyFragmentStatePagerAdapter mFAdapter;
    private List<Fragment> mFragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_business);
        mViewPager = findViewById(R.id.viewPager);
        mBottomNavigationView = findViewById(R.id.bottomNav);

        mFragmentList = new ArrayList<>();
        Fragment menu = new FragmentMenuBusiness();
        Fragment addMenu = new FragmentAddMenu();
        Fragment order = new FragmentOrderBusiness();
        mFragmentList.add(menu);
        mFragmentList.add(addMenu);
        mFragmentList.add(order);
        mFAdapter = new MyFragmentStatePagerAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mFAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(position) {
                    case 0:
                        mBottomNavigationView.setSelectedItemId(R.id.menu_menu);
                        break;
                    case 1:
                        mBottomNavigationView.setSelectedItemId(R.id.menu_add);
                        break;
                    case 2:
                        mBottomNavigationView.setSelectedItemId(R.id.menu_order);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id ==R.id.menu_menu) {
                    mViewPager.setCurrentItem(0);
                }
                else if (id == R.id.menu_add) {
                    mViewPager.setCurrentItem(1);
                }
                else {
                    mViewPager.setCurrentItem(2);
                }

                return true;
            }
        });
    }
}