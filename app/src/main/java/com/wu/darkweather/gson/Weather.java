package com.wu.darkweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wu on 2017/9/18.
 */

public class Weather {
    public String status;
    public Basic basic;
    public Now now;
    @SerializedName("daily_forecast")
    public List<Foreast> foreastList;
    public Suggestion suggestion;
}
