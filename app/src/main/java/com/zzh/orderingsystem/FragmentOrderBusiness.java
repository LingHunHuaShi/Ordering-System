package com.zzh.orderingsystem;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;

import java.util.ArrayList;

public class FragmentOrderBusiness extends Fragment {

    private int uuid;
    private ListView lvOrder;
    private OrderSys orderSys;
    private DBFunction db;
    private ArrayList<orders> orderList;

    public FragmentOrderBusiness() {
        // Required empty public constructor
    }


    public static FragmentOrderBusiness newInstance(int uuid) {
        FragmentOrderBusiness fragment = new FragmentOrderBusiness();
        Bundle args = new Bundle();
        args.putInt("uuid", uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uuid = getArguments().getInt("uuid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_customer, container, false);
        Log.d("huashi from frg order", "uuid:"+String.valueOf(uuid));
        lvOrder = view.findViewById(R.id.lvOrder);
        orderSys = new OrderSys(getContext());
        db = (DBFunction) orderSys;
        try {
            orderList = db.queryOrders();
            Log.d("huashi", "successful query");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        MyOrderAdapter adapter = new MyOrderAdapter(getContext(), orderList);
        Log.d("huashi", "order list size: "+String.valueOf(orderList.size()));
        lvOrder.setAdapter(adapter);

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_order);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    orderList = db.queryOrders();
                    Log.d("huashi", "successful query");
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                MyOrderAdapter adapter = new MyOrderAdapter(getContext(), orderList);
                Log.d("huashi", "order list size: "+String.valueOf(orderList.size()));
                lvOrder.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        lvOrder.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                int orderId = orderList.get(i).order_id;
                db.Delete_order(orderId);

                try {
                    orderList = db.queryOrders();
                    Log.d("huashi", "successful query");
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                MyOrderAdapter adapter = new MyOrderAdapter(getContext(), orderList);
                Log.d("huashi", "order list size: "+String.valueOf(orderList.size()));
                lvOrder.setAdapter(adapter);

                return true;
            }
        });

        return view;
    }
}