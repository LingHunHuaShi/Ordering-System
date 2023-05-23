package com.zzh.orderingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivityCustomer extends AppCompatActivity {

    private ViewPager mViewPager;
    private BottomNavigationView mBottomNavigationView;
    private MyFragmentStatePagerAdapter mFAdapter;
    private List<Fragment> mFragmentList;
    public ArrayList<foods> cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = findViewById(R.id.viewPager);
        mBottomNavigationView = findViewById(R.id.bottomNav);

        mFragmentList = new ArrayList<>();
        Fragment menu = new FragmentMenuCustomer();
        Fragment order = new FragmentOrderCustomer();
        Fragment cart = new FragmentCartCostomer();
        mFragmentList.add(menu);
        mFragmentList.add(cart);
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
                        mBottomNavigationView.setSelectedItemId(R.id.menu_cart);
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
                else if (id == R.id.menu_cart) {
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

