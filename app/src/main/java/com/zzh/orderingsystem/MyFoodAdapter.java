package com.zzh.orderingsystem;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class MyFoodAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private OrderSys orderSys;
    private DBFunction db;
    ArrayList<foods> mFoodList;
    Context mContext;
    public boolean isBusiness = false;
    public MyViewModel viewModel;

    public MyFoodAdapter(ArrayList<foods> foodList, Context context){
        this.mFoodList = foodList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.menu_item_layout, null);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public int getItemCount() {
        return mFoodList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvFood.setText(mFoodList.get(position).food_name);
        holder.tvFoodPrice.setText("￥"+String.valueOf(mFoodList.get(position).price));
        orderSys = new OrderSys(mContext);
        db = (DBFunction) orderSys;

        File dir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(dir, mFoodList.get(position).food_name+".jpg");
        Bitmap pic = BitmapFactory.decodeFile(file.getAbsolutePath());

        holder.imgFood.setImageBitmap(pic);
        if(this.isBusiness == true){
            holder.itemRoot.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = holder.getAdapterPosition();
                    String foodName = mFoodList.get(pos).food_name;

                    //todo: add delete menu function here
                    orderSys.Delete_food(mFoodList.get(pos).food_id);

                    Toast.makeText(mContext, foodName + " is deleted!", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
        else{
            holder.itemRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getAdapterPosition();
                    String foodName = mFoodList.get(pos).food_name;
                    //todo: add addToCart function here
                    if(viewModel != null){
                        viewModel.addFood(mFoodList.get(pos));
                        Toast.makeText(mContext, "已添加到购物车！", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Log.d("huashi", "viewModel is null!");
                }
            });
        }
    }

}
class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView imgFood;
    TextView tvFood;
    TextView tvFoodPrice;
    LinearLayout itemRoot;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        imgFood = itemView.findViewById(R.id.imgFood);
        tvFood = itemView.findViewById(R.id.tvFood);
        tvFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
        itemRoot = itemView.findViewById(R.id.itemRoot);
    }
}

