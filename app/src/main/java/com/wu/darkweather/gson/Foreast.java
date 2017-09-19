package com.wu.darkweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wu on 2017/9/18.
 */

public class Foreast {
    public Cond cond;
    public String date;
    @SerializedName("tmp")
    public Temperature temperature;

    public class Temperature{
        public String max;
        public String min;
    }
    public class Cond {
        @SerializedName("txt_d")
        public String day;
        @SerializedName("txt_n")
        public String night;
    }
}
