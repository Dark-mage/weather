package com.wu.darkweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wu on 2017/9/18.
 */

public class Basic {
    public String city;
    @SerializedName("id")
    public String weatherId;
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
