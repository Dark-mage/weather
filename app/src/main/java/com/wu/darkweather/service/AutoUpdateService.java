package com.wu.darkweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.wu.darkweather.gson.Weather;
import com.wu.darkweather.util.HttpUtil;
import com.wu.darkweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateImg();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour=8*60*60*1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent intent1 = new Intent(this, AutoUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(AutoUpdateService.this, 0, intent1, 0);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }
    private void updateImg(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String info = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_img",info);
                editor.apply();
            }
        });
    }
    private void updateWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather",null);
        if(weatherString!=null){
            Weather weather = Utility.handleWeatherInfo(weatherString);
            String weatherId = weather.basic.weatherId;
            String address = "https://free-api.heweather.com/v5/weather?city="+weatherId+"&key=4faec94328c04b4dbd770be93768e60b";
            HttpUtil.sendRequest(address, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String info = response.body().string();
                    Weather weather1 = Utility.handleWeatherInfo(info);
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                    editor.putString("weather",info);
                    editor.apply();
                }
            });
        }else{

        }
    }
}
