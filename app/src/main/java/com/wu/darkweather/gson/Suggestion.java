package com.wu.darkweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wu on 2017/9/18.
 */

public class Suggestion {
    public Air air;
    @SerializedName("comf")
    public Comfortable comfortable;
    @SerializedName("cw")
    public CarWash carWash;
    public Sport sport;
    public class Sport{
        public String brf;
        public String txt;
    }
    public class CarWash{
        public String brf;
        public String txt;
    }
    public class Comfortable{
        public String brf;
        public String txt;
    }
    public class Air{
        public String brf;
        public String txt;
    }
}
