package com.zzh.orderingsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyOrderAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<orders> mOrderList;

    public MyOrderAdapter(Context context, ArrayList<orders> orderList) {
        this.mOrderList = orderList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mOrderList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.order_item_layout, viewGroup, false);
            holder = new ViewHolder();
            holder.tvFood = view.findViewById(R.id.tvOrderFood);
            holder.tvPhone = view.findViewById(R.id.tvOrderPhone);
            holder.tvId = view.findViewById(R.id.tvOrderId);
            holder.tvPrice = view.findViewById(R.id.tvOrderPrice);
            holder.tvTime = view.findViewById(R.id.tvOrderTime);
            view.setTag(holder);
        }
        else
            holder =(ViewHolder) view.getTag();

        orders curOrder = mOrderList.get(i);
        holder.tvId.setText("订单ID:"+String.valueOf(curOrder.order_id));
        holder.tvPhone.setText("用户手机号:"+String.valueOf(curOrder.phone_num));
        holder.tvPrice.setText("订单总金额:"+String.valueOf(curOrder.total));
        holder.tvTime.setText("下单时间:"+curOrder.date);
        //process food list
        String foodList = "";
        for(String k:curOrder.Foods.keySet()){
            foodList += k+" ";
            foodList += curOrder.Foods.get(k)+"件";
            foodList += "\n";
        }
        holder.tvFood.setText(foodList);

        return view;
    }

    static class ViewHolder {
        TextView tvId;
        TextView tvPhone;
        TextView tvFood;
        TextView tvPrice;
        TextView tvTime;
    }
}
