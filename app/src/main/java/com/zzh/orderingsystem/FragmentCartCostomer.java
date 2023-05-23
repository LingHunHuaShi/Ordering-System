package com.zzh.orderingsystem;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;


public class FragmentCartCostomer extends Fragment {
    private int uuid;
    private MyViewModel viewModel;
    private Map<String, Integer> cart;
    private ArrayList<foods> mFoodList;
    private ArrayList<Pair<String, Integer>> cartList;
    private ListView listView;
    private TextView tvTotalPrice;
    private Button btnMakeOrder;
    private DBFunction db;
    private OrderSys orderSys;

    public FragmentCartCostomer() {
        // Required empty public constructor
    }
    public static FragmentCartCostomer newInstance(int uuid) {
        FragmentCartCostomer fragment = new FragmentCartCostomer();
        Bundle args = new Bundle();
        args.putInt("uuid", uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle!=null) {
            uuid = bundle.getInt("uuid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_cart_costomer, container, false);
        viewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory()).get(MyViewModel.class);
        cart = viewModel.raw_cart;
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);

        cartList = new ArrayList<>();
        if(!cart.isEmpty()) {
            for (String k : cart.keySet()) {
                cartList.add(new Pair<>(k, cart.get(k)));
            }
        }
        MyCartAdapter adapter = new MyCartAdapter(cartList, getContext());
        listView = view.findViewById(R.id.lvCart);
        listView.setAdapter(adapter);
        tvTotalPrice.setText("￥"+calcPrice(adapter));

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
                tvTotalPrice.setText("￥"+calcPrice(adapter));
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
                tvTotalPrice.setText("￥"+calcPrice(adapter));

                return true;
            }
        });
        btnMakeOrder = view.findViewById(R.id.btnMakeOrder);
        btnMakeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!cartList.isEmpty()) {
                    orderSys = new OrderSys(getContext());
                    db = (DBFunction) orderSys;
//                    ArrayList<users> user = db.queryByUUID(uuid);
                    String[] strs = {String.valueOf(uuid)};
                    Log.d("huashi from cart fragment", "uuid:"+String.valueOf(uuid));
                    SQLiteDatabase db2 = orderSys.getReadableDatabase();
                    int phone_num = -2222;
                    Cursor cur = db2.rawQuery("SELECT phone_num FROM USERS WHERE uuid = ?",strs);
                    if(cur.moveToFirst())
                        phone_num = cur.getInt(cur.getColumnIndexOrThrow("phone_num"));
                    cur.close();
                    Log.d("huashi", "phone number:"+String.valueOf(phone_num));

                    Double total = Double.parseDouble(tvTotalPrice.getText().toString().substring(1));

                    orders newOrder = new orders(-1, "", phone_num, cart,
                            false, total, uuid);
                    Log.d("huashi", "temp order created");
                    db.createOrders(newOrder);
                    Log.d("huashi", "order inserted");
                    viewModel.raw_cart.clear();
                    cart.clear();
                    cartList.clear();
                    MyCartAdapter adapter = new MyCartAdapter(cartList, getContext());
                    listView.setAdapter(adapter);
                    tvTotalPrice.setText("￥0.0");

                    Toast.makeText(getContext(), "订单已创建！", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getContext(), "购物车为空，请先添加商品。", Toast.LENGTH_SHORT).show();
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