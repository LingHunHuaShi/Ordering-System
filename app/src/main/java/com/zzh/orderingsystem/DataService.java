package com.zzh.orderingsystem;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.security.Provider;
import java.util.Calendar;

public class DataService extends Service{

    static class Mbind extends Binder {
        String getDate(){
            Calendar calendar = Calendar.getInstance();
            int YY = calendar.get(Calendar.YEAR);
            int MM = calendar.get(Calendar.MONTH) + 1;
            int DD = calendar.get(Calendar.DAY_OF_MONTH);
            int HH = calendar.get(Calendar.HOUR_OF_DAY);
            int mm = calendar.get(Calendar.MINUTE);
            int SEC = calendar.get(Calendar.SECOND);
            return String.format("%04d-%02d-%02d-%02d-%02d-%02d",YY,MM,DD,HH,mm,SEC);
        }
    }

    Mbind bind = new Mbind();

    @Override
    public IBinder onBind(Intent intent) {
        return this.bind;
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
