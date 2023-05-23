package com.zzh.orderingsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class users{
    public int uuid;
    //UUID 唯一标识用户
    public int phone_num;
    // 手机号
    public String username;
    // 用户名
    public String passwd;
    // 密码
    public boolean authorization;
    // 身份校验
    users(int uuid,int phone_num,String username,String passwd,boolean authorization){
        this.uuid = uuid;
        this.phone_num = phone_num;
        this.username = username;
        this.passwd = passwd;
        this.authorization = authorization;
    }

    users(){};
}


class orders{
    public int order_id;
    public String address;
    public int phone_num;
    public String date;
    public Map<String, Integer> Foods;

    public Boolean finish;

    public int uuid;
    public double total;

    orders(int order_id,String address,int phone_num,String date,String[] Foods,Integer[] Foods_id,boolean finish,double total,int uuid){
        assert (Foods.length==Foods_id.length || Foods.length==0||Foods_id.length==0);
        this.order_id = order_id;
        this.address = address;
        this.phone_num = phone_num;
        this.date = date;
        this.Foods = new HashMap<>();
        for(int i = 0;i<Foods.length;i++){
            this.Foods.put(Foods[i],Foods_id[i]);
        }
        this.finish = finish;
        this.total = total;
        this.uuid = uuid;
    }

}


class foods{
    public String food_name;
    public int food_id;
    public boolean empty;
    public String description;

    public String category;

    public double price ;
    foods(String food_name,int food_id,boolean empty,String description,double price,String category){
        this.food_id = food_id;
        this.empty = empty;
        this.description = description;
        this.food_name = food_name;
        this.price = price;
        this.category = category;
    }
}


public class OrderSys extends SQLiteOpenHelper implements DBFunction{

    static final String DB_NAME = "OrderSys";
    // 数据库名
    static final String TABLE1 = "USERS";
    // 客户身份表
    static final String TABLE2 = "ORDERS";
    // 订单表
    static final String TABLE3 = "FOODS";
    // 食物表

    static final String USERS = "CREATE TABLE " + TABLE1 +
            " (uuid INTEGER PRIMARY KEY AUTOINCREMENT," +
            "phone_num INTEGER UNIQUE, " +
            "username TEXT, " +
            "passwd TEXT, " +
            "authorization INTEGER)";
    static final String ORDERS = "CREATE TABLE " + TABLE2 +
            " (order_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "address TEXT, " +
            "phone_num INTEGER, " +
            "date TEXT, " +
            "Foods JSON, " +
            "total REAL," +
            "uuid INTEGER,"+
            "finish INTEGER)";

    static final String FOODS = "CREATE TABLE " + TABLE3 +
            " (food_name TEXT, " +
            "food_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "category TEXT,"+
            "empty INTEGER, " +
            "price REAL,"+
            "description TEXT)";



    public OrderSys(Context context){
        super(context,DB_NAME,null,1);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(USERS);
        sqLiteDatabase.execSQL(ORDERS);
        sqLiteDatabase.execSQL(FOODS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //place holder
    }


    public boolean insertFood(foods food){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put("food_name",food.food_name);
        vals.put("empty",food.empty);
        vals.put("description",food.description);
        vals.put("price",food.price);
        vals.put("category",food.category);
        long ret = db.insert(TABLE3,null,vals);
        return ret!=-1;
    }

    public boolean createUsers(users user){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put("phone_num",user.phone_num);
        vals.put("username",user.username);
        vals.put("passwd",user.passwd);
        vals.put("authorization",user.authorization);
        long ret = db.insert(TABLE1,null,vals);
        return ret!=-1;
    };

    public boolean createOrders(orders order){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues vals = new ContentValues();

        JSONObject foodsInfo = new JSONObject(order.Foods);
        vals.put("address",order.address);
        vals.put("date",order.date);
        vals.put("finish",order.finish);
        vals.put("phone_num",order.phone_num);
        vals.put("Foods", foodsInfo.toString());
        vals.put("total",order.total);
        vals.put("uuid",order.uuid);
        long ret = db.insert(TABLE2,null,vals);
        return ret!=-1;
    }

    @Override
    public ArrayList<users> queryAllUsers(){
        ArrayList<users> users = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE1, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int uuid = cursor.getInt(cursor.getColumnIndexOrThrow("uuid"));
                int phoneNum = cursor.getInt(cursor.getColumnIndexOrThrow("phone_num"));
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String passwd = cursor.getString(cursor.getColumnIndexOrThrow("passwd"));
                boolean authorization = cursor.getInt(cursor.getColumnIndexOrThrow("authorization")) == 1;

                users user = new users(uuid, phoneNum, username, passwd, authorization);
                users.add(user);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return users;
    }

    @Override
    public ArrayList<foods> queryAllfoods(){
        ArrayList<foods> foods = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE3, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String foodName = cursor.getString(cursor.getColumnIndexOrThrow("food_name"));
                int foodId = cursor.getInt(cursor.getColumnIndexOrThrow("food_id"));
                boolean empty = cursor.getInt(cursor.getColumnIndexOrThrow("empty")) == 1;
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));

                foods food = new foods(foodName, foodId, empty, description, price, category);
                foods.add(food);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return foods;
    }

    @Override
    public ArrayList<foods> queryFoodByCategory(String Category){
        ArrayList<foods> foods = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] Args = {Category};
        Cursor cursor = db.query(TABLE3, null,"category = ?",Args,null,null,null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String foodName = cursor.getString(cursor.getColumnIndexOrThrow("food_name"));
                int foodId = cursor.getInt(cursor.getColumnIndexOrThrow("food_id"));
                boolean empty = cursor.getInt(cursor.getColumnIndexOrThrow("empty")) == 1;
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));

                foods food = new foods(foodName, foodId, empty, description, price, category);
                foods.add(food);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return foods;
    }

    @Override
    public ArrayList<users> queryByUUID(int id) {
        ArrayList<users> users = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] Args = {String.valueOf(id)};
        Cursor cursor = db.query(TABLE1, null, "uuid = ?", Args, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int uuid = cursor.getInt(cursor.getColumnIndexOrThrow("uuid"));
                int phoneNum = cursor.getInt(cursor.getColumnIndexOrThrow("phone_num"));
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String password = cursor.getString(cursor.getColumnIndexOrThrow("passwd"));
                boolean authorization = cursor.getInt(cursor.getColumnIndexOrThrow("authorization")) == 1;
                users user = new users(uuid, phoneNum, username, password, authorization);
                users.add(user);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return users;
    }

    @Override
    public ArrayList<orders> queryOrders() throws JSONException {
        ArrayList<orders> orders = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE2, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int order_id = cursor.getInt(cursor.getColumnIndexOrThrow("order_id"));
                String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                int phone_num = cursor.getInt(cursor.getColumnIndexOrThrow("phone_num"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String foodsJson = cursor.getString(cursor.getColumnIndexOrThrow("Foods"));
                boolean finish = cursor.getInt(cursor.getColumnIndexOrThrow("finish")) == 1;
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
                int uuid = cursor.getInt(cursor.getColumnIndexOrThrow("uuid"));

                Map<String, Integer> foods = new HashMap<>();

                assert(foodsJson.length()==0);
                JSONObject foods_ = new JSONObject(foodsJson);
                Iterator<String> keys = foods_.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    int value;
                    value = foods_.getInt(key);
                    foods.put(key, value);
                }
                orders order = new orders(order_id, address, phone_num, date, null, null, finish, total,uuid);
                order.Foods = foods;
                orders.add(order);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return orders;
    }

    @Override
    public ArrayList<orders> queryOrdersByUUID(int uuid) throws JSONException {
        ArrayList<orders> orders = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] Args = {String.valueOf(uuid)};
        Cursor cursor = db.query(TABLE2,null,"uuid = ?",Args,null,null,null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int order_id = cursor.getInt(cursor.getColumnIndexOrThrow("order_id"));
                String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                int phone_num = cursor.getInt(cursor.getColumnIndexOrThrow("phone_num"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String foodsJson = cursor.getString(cursor.getColumnIndexOrThrow("Foods"));
                boolean finish = cursor.getInt(cursor.getColumnIndexOrThrow("finish")) == 1;
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
                int uuid_ = cursor.getInt(cursor.getColumnIndexOrThrow("uuid"));

                Map<String, Integer> foods = new HashMap<>();

                assert(foodsJson.length()==0);
                JSONObject foods_ = new JSONObject(foodsJson);
                Iterator<String> keys = foods_.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    int value;
                    value = foods_.getInt(key);
                    foods.put(key, value);
                }
                orders order = new orders(order_id, address, phone_num, date, null, null, finish, total,uuid_);
                order.Foods = foods;
                orders.add(order);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return orders;
    }

    @Override
    public orders queryOrdersbyOrderID(int orderid) throws JSONException {

        SQLiteDatabase db = getReadableDatabase();
        String[] Args = {String.valueOf(orderid)};
        Cursor cursor = db.query(TABLE2,null,"order_id = ?",Args,null,null,null);
        if (cursor != null && cursor.moveToFirst()) {
            int order_id = cursor.getInt(cursor.getColumnIndexOrThrow("order_id"));
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            int phone_num = cursor.getInt(cursor.getColumnIndexOrThrow("phone_num"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String foodsJson = cursor.getString(cursor.getColumnIndexOrThrow("Foods"));
            boolean finish = cursor.getInt(cursor.getColumnIndexOrThrow("finish")) == 1;
            double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
            int uuid_ = cursor.getInt(cursor.getColumnIndexOrThrow("uuid"));

            Map<String, Integer> foods = new HashMap<>();

            assert(foodsJson.length()==0);
            JSONObject foods_ = new JSONObject(foodsJson);
            Iterator<String> keys = foods_.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                int value;
                value = foods_.getInt(key);
                foods.put(key, value);
            }
            orders order = new orders(order_id, address, phone_num, date, null, null, finish, total,uuid_);
            order.Foods = foods;
            cursor.close();
            return order;
        }
        else return null;
    }

    @Override
    public int Login(int Phonenum,String password){
        SQLiteDatabase db = getReadableDatabase();
        final String query_statement = "SELECT phone_num, passwd FROM " + TABLE1
                + " WHERE phone_num = '" + String.valueOf(Phonenum) + "'";
        Cursor cursor = db.rawQuery(query_statement,null);
        if(cursor!=null && cursor.moveToFirst()){
            String pw = cursor.getString(cursor.getColumnIndexOrThrow("passwd"));
            cursor.close();
            if(pw.equals(password))
                return 0;//密码正确
            else
                return -2;//密码错误
        }
        else{
            // 该用户未注册
            return -1;
        }
    }

    @Override
    public boolean update_users(int uuid,users new_info){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put("phone_num",new_info.phone_num);
        vals.put("username",new_info.username);
        vals.put("passwd",new_info.passwd);
        vals.put("authorization",new_info.authorization);

        final String[] Args = {String.valueOf(uuid)};

        int ret = db.update(TABLE1,vals,"uuid = ?",Args);
        return ret>0;
    }

    public boolean update_password(int Phonenum,String password){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put("passwd", password);

        final String[] Args = {String.valueOf(Phonenum)};

        int ret = db.update(TABLE1,vals,"phone_num = ?",Args);
        return ret>0;
    }

    @Override
    public users find_user(int Phonenum){
        SQLiteDatabase db = getReadableDatabase();
        String[] Args = {String.valueOf(Phonenum)};

        Cursor cursor = db.query(TABLE1,null,"phone_num = ?",Args,null,null,null);
        if(cursor!=null&&cursor.moveToFirst()){
            int uuid = cursor.getInt(cursor.getColumnIndexOrThrow("uuid"));
            int phoneNum = cursor.getInt(cursor.getColumnIndexOrThrow("phone_num"));
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String passwd = cursor.getString(cursor.getColumnIndexOrThrow("passwd"));
            boolean authorization = cursor.getInt(cursor.getColumnIndexOrThrow("authorization")) == 1;
            cursor.close();
            return new users(uuid,phoneNum,username,passwd,authorization);
        }
        else
            return null;
    }

    @Override
    public boolean update_orders(int order_id ,orders new_order){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues vals = new ContentValues();

        JSONObject foodsInfo = new JSONObject(new_order.Foods);
        vals.put("address",new_order.address);
        vals.put("date",new_order.date);
        vals.put("finish",new_order.finish);
        vals.put("phone_num",new_order.phone_num);
        vals.put("Foods", foodsInfo.toString());
        vals.put("total",new_order.total);
        vals.put("uuid",new_order.uuid);

        final String[] Args = {String.valueOf(order_id)};

        int ret = db.update(TABLE2,vals,"order_id = ?",Args);
        return ret>0;
    }

    @Override
    public boolean update_foods(int food_id,foods new_food){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put("food_name",new_food.food_name);
        vals.put("empty",new_food.empty);
        vals.put("description",new_food.description);
        vals.put("price",new_food.price);
        vals.put("category",new_food.category);

        final String[] Args = {String.valueOf(food_id)};
        int ret = db.update(TABLE3,vals,"food_id = ?",Args);
        return ret>0;
    }

    @Override
    public foods find_food(int food_id)
    {
        SQLiteDatabase db = getReadableDatabase();
        String[] Args = {String.valueOf(food_id)};
        Cursor cursor = db.query(TABLE3, null,"food_id = ?",Args,null,null,null);
        if (cursor != null && cursor.moveToFirst())
        {
            String foodName = cursor.getString(cursor.getColumnIndexOrThrow("food_name"));
            int foodId = cursor.getInt(cursor.getColumnIndexOrThrow("food_id"));
            boolean empty = cursor.getInt(cursor.getColumnIndexOrThrow("empty")) == 1;
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
            String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));

            foods food = new foods(foodName, foodId, empty, description, price, category);
            cursor.close();
            return food;
        }
        else return null;
    }

    @Override
    public boolean Delete_account(int uuid){
        SQLiteDatabase db = getWritableDatabase();
        String[] Args = {String.valueOf(uuid)};

        int ret = db.delete(TABLE1,"uuid = ?",Args);
        return ret > 0;
    }


    @Override
    public boolean Delete_order(int order_id){
        SQLiteDatabase db = getWritableDatabase();
        String[] Args = {String.valueOf(order_id)};

        int ret = db.delete(TABLE2,"order_id = ?",Args);
        return ret > 0;
    }

    @Override
    public boolean Delete_food(int food_id){
        SQLiteDatabase db = getWritableDatabase();
        String[] Args = {String.valueOf(food_id)};

        int ret = db.delete(TABLE3,"food_id = ?",Args);
        return ret > 0;
    }
}




interface DBFunction{
    ArrayList<users> queryAllUsers();
    ArrayList<foods> queryAllfoods();

    ArrayList<foods> queryFoodByCategory(String Category);

    ArrayList<users> queryByUUID(int id);

    ArrayList<orders> queryOrders() throws JSONException;

    orders queryOrdersbyOrderID(int orderid) throws JSONException;

    ArrayList<orders> queryOrdersByUUID(int uuid) throws JSONException;
    boolean createUsers(users user);
    boolean insertFood(foods food);
    boolean createOrders(orders order);

    int Login(int Phonenum,String password);

    boolean update_users(int uuid, users new_info);

    boolean update_orders(int order_id ,orders new_order);

    boolean update_foods(int food_id,foods new_food);

    users find_user(int Phonenum);

    foods find_food(int food_id);


    boolean Delete_account(int uuid);

    boolean Delete_order(int order_id);

    boolean Delete_food(int food_id);

}
