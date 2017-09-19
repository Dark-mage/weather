package com.wu.darkweather.gson;

import java.util.List;

/**
 * Created by wu on 2017/9/18.
 */

public class Weather {
    public String status;
    public Basic basic;
    public Now now;
    public List<Foreast> foreastList;
    public Suggestion suggestion;
}
