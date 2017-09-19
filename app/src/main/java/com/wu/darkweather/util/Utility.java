package com.wu.darkweather.util;

import android.text.TextUtils;

import com.wu.darkweather.db.City;
import com.wu.darkweather.db.County;
import com.wu.darkweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wu on 2017/9/18.
 */

public class Utility {
    /**
     * 处理省信息
     * @param info
     * @return
     */
    public static boolean handleProvinceInfo(String info){
        if(!TextUtils.isEmpty(info)){
            try {
                JSONArray jsonArray = new JSONArray(info);
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(jsonObject.getString("name"));
                    province.setProvinceCode(jsonObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析城市信息
     * @param info
     * @param provinceId
     * @return
     */
    public static boolean handleCityInfo(String info,int provinceId){
        if(!TextUtils.isEmpty(info)){
            try {
                JSONArray jsonArray = new JSONArray(info);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    City city = new City();
                    city.setProvinceId(provinceId);
                    city.setCityCode(jsonObject.getInt("id"));
                    city.setCityName(jsonObject.getString("name"));
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析镇区信息
     * @param info
     * @param cityId
     * @return
     */
    public static  boolean handleCountyInfo(String info,int cityId){
        if(!TextUtils.isEmpty(info)){
            try {
                JSONArray jsonArray = new JSONArray(info);
                for (int i = 0; i <jsonArray.length() ; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    County county = new County();
                    county.setCityId(cityId);
                    county.setWeatherId(jsonObject.getString("weather_id"));
                    county.setCountyName(jsonObject.getString("name"));
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
