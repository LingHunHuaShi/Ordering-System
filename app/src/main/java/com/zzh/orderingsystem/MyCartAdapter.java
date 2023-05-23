package com.zzh.orderingsystem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class MyCartAdapter extends BaseAdapter {
    public ArrayList<Pair<String, Integer>> foodList;
    private Context mContext;

    public MyCartAdapter(ArrayList<Pair<String, Integer>> list, Context context) {
        this.foodList = list;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return foodList.size();
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
        if(view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.cart_item_layout, viewGroup, false);
            holder = new ViewHolder();
            holder.imgCartFood = view.findViewById(R.id.imgCartFood);
            holder.tvCartName = view.findViewById(R.id.tvCartName);
            holder.tvCartNum = view.findViewById(R.id.tvCartNum);
            holder.tvCartPrice = view.findViewById(R.id.tvCartPrice);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tvCartName.setText(foodList.get(i).first);
        holder.tvCartNum.setText("×"+foodList.get(i).second.toString());

        File dir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(dir, foodList.get(i).first+".jpg");
        Bitmap pic = BitmapFactory.decodeFile(file.getAbsolutePath());
        holder.imgCartFood.setImageBitmap(pic);

        String[] args = {foodList.get(i).first};
        String[] column = {"price"};
        OrderSys db_sys = new OrderSys(mContext);
        SQLiteDatabase db = db_sys.getReadableDatabase();
        Cursor cur = db.query("FOODS", column, "food_name = ?", args,null, null, null);
        Double price = 0.0;
        if(cur.moveToFirst())
            price = cur.getDouble(cur.getColumnIndexOrThrow("price"));
        Double totalPrice = price*foodList.get(i).second;
        holder.tvCartPrice.setText(totalPrice.toString());

        return view;
    }

    static class ViewHolder{
        ImageView imgCartFood;
        TextView tvCartName;
        TextView tvCartPrice;
        TextView tvCartNum;
    }
}
