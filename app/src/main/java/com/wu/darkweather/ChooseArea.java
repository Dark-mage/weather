package com.wu.darkweather;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wu.darkweather.db.City;
import com.wu.darkweather.db.County;
import com.wu.darkweather.db.Province;
import com.wu.darkweather.util.HttpUtil;
import com.wu.darkweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by wu on 2017/9/18.
 */

public class ChooseArea extends Fragment {
    private Button backbtn;
    private TextView chooseTitle;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private Province selectedProvince;
    private City selectedCity;
    private List<Province> provinces;
    private List<City> cities;
    private List<County> counties;
    private List<String> data;
    private final static int LEVEL_PROVINCE=1;
    private final static int LEVEL_CITY=2;
    private final static int LEVEL_COUNTY=3;
    private int current_level;
    private ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        backbtn = (Button) view.findViewById(R.id.back_btn);
        chooseTitle = (TextView) view.findViewById(R.id.choose_title);
        listView = (ListView) view.findViewById(R.id.list_view);
        data = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(current_level==LEVEL_PROVINCE){
                    selectedProvince = provinces.get(position);
                    queryCities();
                }else if(current_level==LEVEL_CITY){
                    selectedCity = cities.get(position);
                    queryCounties();
                }else if(current_level==LEVEL_COUNTY){

                }
            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current_level==LEVEL_CITY){
                    queryProvinces();
                }else if(current_level==LEVEL_COUNTY){
                    queryCities();
                }
            }
        });
        queryProvinces();
    }
    private void queryProvinces(){
        chooseTitle.setText("中国");
        backbtn.setVisibility(View.GONE);
        provinces = DataSupport.findAll(Province.class);
        if(provinces.size()>0){
            data.clear();
            for(Province province:provinces){
                data.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            current_level = LEVEL_PROVINCE;
        }else{
            String address="http://guolin.tech/api/china/";
            queryFromServer(address,"province");
        }
    }
    private void queryCities(){
        chooseTitle.setText(selectedProvince.getProvinceName());
        backbtn.setVisibility(View.VISIBLE);
        cities = DataSupport.where("provinceid=?", String.valueOf(selectedProvince.getId())).find(City.class);
        if(cities.size()>0){
            data.clear();
            for(City city:cities){
                data.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            current_level = LEVEL_CITY;
        }else{
            String address="http://guolin.tech/api/china/"+selectedProvince.getProvinceCode();
            queryFromServer(address,"city");
        }
    }
    private void queryCounties(){
        chooseTitle.setText(selectedCity.getCityName());
        backbtn.setVisibility(View.VISIBLE);
        counties = DataSupport.where("cityid=?", String.valueOf(selectedCity.getId())).find(County.class);
        if(counties.size()>0){
            data.clear();
            for(County county:counties){
                data.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            current_level = LEVEL_COUNTY;
        }else{
            String address="http://guolin.tech/api/china/"+selectedProvince.getProvinceCode()+"/"+selectedCity.getCityCode();
            queryFromServer(address,"county");
        }

    }
    private void queryFromServer(String address, final String type){
        openDialog();
        HttpUtil.sendRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                     closeDialog();
                        Toast.makeText(getActivity(),"获取信息失败!",Toast.LENGTH_SHORT).show();   
                    }
                });                
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String info = response.body().string();
                boolean result=false;
                if("province".equals(type)){
                    result = Utility.handleProvinceInfo(info);
                }else if("city".equals(type)){
                    result = Utility.handleCityInfo(info,selectedProvince.getId());
                }else if("county".equals(type)){
                    result = Utility.handleCountyInfo(info,selectedCity.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }
    private void openDialog(){
        if(progressDialog==null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载……");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}
