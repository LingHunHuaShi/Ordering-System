package com.zzh.orderingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    public Intent intentPre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = findViewById(R.id.viewPager);
        mBottomNavigationView = findViewById(R.id.bottomNav);

        intentPre = getIntent();
        int uuid = intentPre.getIntExtra("uuid", -1);
        Log.d("huashi from activity", "uuid:"+String.valueOf(uuid));
        mFragmentList = new ArrayList<>();
        Fragment menu = new FragmentMenuCustomer();
        Fragment order = FragmentOrderCustomer.newInstance(uuid);
        Fragment cart = FragmentCartCostomer.newInstance(uuid);

        Bundle bundle = new Bundle();
        bundle.putInt("uuid", uuid);
        order.setArguments(bundle);

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

