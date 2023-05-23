package com.zzh.orderingsystem;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;


public class FragmentCartCostomer extends Fragment {
    private MyViewModel viewModel;
    private Map<String, Integer> cart;
    private ArrayList<foods> mFoodList;
    private ArrayList<Pair<String, Integer>> cartList;
    private ListView listView;
    private TextView tvTotalPeice;

    public FragmentCartCostomer() {
        // Required empty public constructor
    }
    public static FragmentCartCostomer newInstance(String param1, String param2) {
        FragmentCartCostomer fragment = new FragmentCartCostomer();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_cart_costomer, container, false);
        viewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory()).get(MyViewModel.class);
        cart = viewModel.raw_cart;
        tvTotalPeice = view.findViewById(R.id.tvTotalPrice);

        cartList = new ArrayList<>();
        if(!cart.isEmpty()) {
            for (String k : cart.keySet()) {
                cartList.add(new Pair<>(k, cart.get(k)));
            }
        }
        MyCartAdapter adapter = new MyCartAdapter(cartList, getContext());
        listView = view.findViewById(R.id.lvCart);
        listView.setAdapter(adapter);
        tvTotalPeice.setText("￥"+calcPrice(adapter));

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_cart);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cart = viewModel.getCart().getValue();

                cartList = new ArrayList<>();
                if(!cart.isEmpty()) {
                    for (String k : cart.keySet()) {
                        cartList.add(new Pair<>(k, cart.get(k)));
                    }
                }
                MyCartAdapter adapter = new MyCartAdapter(cartList, getContext());
                listView.setAdapter(adapter);
                tvTotalPeice.setText("￥"+calcPrice(adapter));
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                cart = viewModel.raw_cart;
                TextView tv = view.findViewById(R.id.tvCartName);
                String name = tv.getText().toString();
                cart.remove(name);
                cartList = new ArrayList<>();
                if(!cart.isEmpty()) {
                    for (String k : cart.keySet()) {
                        cartList.add(new Pair<>(k, cart.get(k)));
                    }
                }
                MyCartAdapter adapter = new MyCartAdapter(cartList, getContext());
                listView.setAdapter(adapter);
                tvTotalPeice.setText("￥"+calcPrice(adapter));

                return true;
            }
        });
        return view;
    }

    private String calcPrice(MyCartAdapter adapter) {
        ArrayList<Pair<String, Integer>> cartList = adapter.foodList;
        Double ret = 0.0;
        OrderSys db_sys = new OrderSys(getContext());
        SQLiteDatabase db = db_sys.getReadableDatabase();
        String[] column = {"price"};

        for(Pair<String, Integer> item:cartList){
            String[] args = {item.first};
            Cursor cur = db.query("FOODS", column, "food_name = ?", args,null, null, null);
            Double price = 0.0;
            if(cur.moveToFirst())
                price = cur.getDouble(cur.getColumnIndexOrThrow("price"));
            ret += price;
        }
        return ret.toString();
    }
}