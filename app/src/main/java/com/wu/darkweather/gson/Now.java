package com.wu.darkweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wu on 2017/9/18.
 */

public class Now {
    public Cond cond;
    @SerializedName("tmp")
    public String temperature;
    public Wind wind;
    public class Wind{
        @SerializedName("dir")
        public String windDirection;
        @SerializedName("sc")
        public String windForce;
    }
    public class Cond{
        @SerializedName("txt")
        public String currentCond;
    }
}
