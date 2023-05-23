package com.zzh.orderingsystem;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;



import java.util.ArrayList;
import java.util.List;


public class FragmentMenuCustomer extends Fragment {

    private OrderSys orderSys;
    private DBFunction db;
    private ArrayList<foods> main_food;
    private ArrayList<foods> snacks;
    private ArrayList<foods> drinks;

    private MyFoodAdapter adapter_main_food;
    private MyFoodAdapter adapter_snack;
    private MyFoodAdapter adapter_drink;
    private int currentPage = 0;
    private MyViewModel viewModel;


    public FragmentMenuCustomer() {
        // Required empty public constructor
    }


    public static FragmentMenuCustomer newInstance(String param1, String param2) {
        FragmentMenuCustomer fragment = new FragmentMenuCustomer();
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
        View view =  inflater.inflate(R.layout.fragment_menu_customer, container, false);
        orderSys = new OrderSys(getActivity());
        DBFunction db = (DBFunction) orderSys;

        ListView lvTag = view.findViewById(R.id.listViewTag);
        List<String> tags = new ArrayList<>();
        tags.add("主食");
        tags.add("饮料");
        tags.add("小食");
        tags.toArray();
        lvTag.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, tags));

        RecyclerView lvMenu = view.findViewById(R.id.recyclerViewMenu);
        main_food = db.queryFoodByCategory("主食");
        snacks = db.queryFoodByCategory("小食");
        drinks = db.queryFoodByCategory("饮料");


        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        lvMenu.setLayoutManager(manager);
        adapter_main_food = new MyFoodAdapter(main_food, getContext());
        adapter_snack = new MyFoodAdapter(snacks, getContext());
        adapter_drink = new MyFoodAdapter(drinks, getContext());


        lvMenu.setAdapter(adapter_main_food);

        lvTag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        lvMenu.setAdapter(adapter_main_food);
                        currentPage = 0;
                        break;
                    case 1:
                        lvMenu.setAdapter(adapter_drink);
                        currentPage = 1;
                        break;
                    case 2:
                        lvMenu.setAdapter(adapter_snack);
                        currentPage = 2;
                        break;
                    default:
                        break;
                }
            }
        });

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 在这里执行刷新数据的操作
                switch (currentPage) {
                    case 0:
                        main_food = db.queryFoodByCategory("主食");
                        adapter_main_food = new MyFoodAdapter(main_food, getContext());
                        lvMenu.setAdapter(adapter_main_food);
                        break;
                    case 1:
                        drinks = db.queryFoodByCategory("饮料");
                        adapter_drink = new MyFoodAdapter(drinks, getContext());
                        lvMenu.setAdapter(adapter_drink);
                        break;
                    case 2:
                        snacks = db.queryFoodByCategory("小食");
                        adapter_snack = new MyFoodAdapter(snacks, getContext());
                        lvMenu.setAdapter(adapter_snack);
                        break;
                    default:
                        break;
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        viewModel = new ViewModelProvider(requireActivity(),
                new ViewModelProvider.NewInstanceFactory()).get(MyViewModel.class);
        adapter_drink.viewModel = viewModel;
        adapter_snack.viewModel = viewModel;
        adapter_main_food.viewModel = viewModel;

        return view;
    }
}