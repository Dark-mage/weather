package com.wu.darkweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wu.darkweather.gson.Foreast;
import com.wu.darkweather.gson.Weather;
import com.wu.darkweather.service.AutoUpdateService;
import com.wu.darkweather.util.HttpUtil;
import com.wu.darkweather.util.Utility;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ImageView bg;
    public DrawerLayout drawerLayout;
    public SwipeRefreshLayout swipeRefreshLayout;
    private ScrollView weatherLayout;
    private Button homeBtn;
    private TextView title;
    private TextView updateTime;
    private TextView nowCond;
    private TextView nowTmp;
    private TextView nowWind;
    private LinearLayout forecastLayout;
    private TextView suggestionAir;
    private TextView suggestionComf;
    private TextView suggestionCarwash;
    private TextView suggestionSport;
    private String mWeatherId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        //初始化信息
        init();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=preferences.getString("weather",null);
        //有缓存时直接解析天气数据
        if(weatherString!=null){
            Weather weather = Utility.handleWeatherInfo(weatherString);
            mWeatherId = weather.basic.weatherId;
            setWeatherInfo(weather);
        }else{// 无缓存时去服务器查询天气
            mWeatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        //设置刷新时请求数据
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        //根据是否有图片缓存加载图片
        String bingPic = preferences.getString("bing_img",null);
        if(bingPic!=null){
            Glide.with(this).load(bingPic).into(bg);
        }else{
            loadImg();
        }

    }
    private void init(){
        bg = (ImageView) findViewById(R.id.bg_img);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        homeBtn = (Button) findViewById(R.id.home);
        title = (TextView) findViewById(R.id.title);
        updateTime = (TextView) findViewById(R.id.update_time);
        nowCond = (TextView) findViewById(R.id.now_cond);
        nowTmp = (TextView) findViewById(R.id.now_tmp);
        nowWind = (TextView) findViewById(R.id.now_wind);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        suggestionAir = (TextView) findViewById(R.id.suggestion_air);
        suggestionComf = (TextView) findViewById(R.id.suggestion_comf);
        suggestionCarwash = (TextView) findViewById(R.id.suggestion_cw);
        suggestionSport = (TextView) findViewById(R.id.suggestion_sport);
    }
    private void loadImg(){
        String address="http://guolin.tech/api/bing_pic";
        HttpUtil.sendRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bing_img = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_img",bing_img);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bing_img).into(bg);
                    }
                });
            }
        });
    }
    public void requestWeather(String weatherId){
        String address = "https://free-api.heweather.com/v5/weather?city="+weatherId+"&key=4faec94328c04b4dbd770be93768e60b";
        HttpUtil.sendRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取信息失败!",Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String info = response.body().string();
                final Weather weather = Utility.handleWeatherInfo(info);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){
                            mWeatherId = weather.basic.weatherId;
                            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            edit.putString("weather", info);
                            edit.apply();
                            setWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取信息失败!",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }
    private void setWeatherInfo(Weather weather){
        //设置title部分信息
        String titleInfo=weather.basic.city;
        String updateTimeInfo=weather.basic.update.updateTime.split(" ")[1];
        title.setText(titleInfo);
        updateTime.setText(updateTimeInfo);
        //设置now部分信息
        String nowCondInfo=weather.now.cond.currentCond;
        String nowTmpInfo = weather.now.temperature+"℃";
        String nowWindDir=weather.now.wind.windDirection;
        String nowWindForce=weather.now.wind.windForce;
        nowCond.setText(nowCondInfo);
        nowTmp.setText(nowTmpInfo);
        nowWind.setText("风向："+nowWindDir+",风力:"+nowWindForce);
        //设置forecast部分信息
        forecastLayout.removeAllViews();
        List<Foreast> foreastList = weather.foreastList;
        if(foreastList.size()>0){
            for(Foreast foreast:foreastList){
                View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
                TextView forecastCond = (TextView) view.findViewById(R.id.forecast_cond);
                TextView forecastDate = (TextView) view.findViewById(R.id.forecast_date);
                TextView forecastTmpMax = (TextView) view.findViewById(R.id.forecast_tmp_max);
                TextView forecastTmpMin = (TextView) view.findViewById(R.id.forecast_tmp_min);
                forecastCond.setText(foreast.cond.day+"转"+foreast.cond.night);
                forecastDate.setText(foreast.date);
                forecastTmpMax.setText(foreast.temperature.max);
                forecastTmpMin.setText(foreast.temperature.min);
                forecastLayout.addView(view);
            }
        }
        //设置suggestion部分信息
        String airInfo = "空气状况：" + weather.suggestion.air.txt;
        String comInfo = "舒适度：" + weather.suggestion.comfortable.txt;
        String carWashInfo = "洗车建议：" + weather.suggestion.carWash.txt;
        String sportInfo = "运动建议：" + weather.suggestion.sport.txt;
        suggestionAir.setText(airInfo);
        suggestionCarwash.setText(carWashInfo);
        suggestionComf.setText(comInfo);
        suggestionSport.setText(sportInfo);
        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }
}
