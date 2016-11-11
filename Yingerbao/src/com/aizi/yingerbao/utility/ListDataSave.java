package com.aizi.yingerbao.utility;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

import com.aizi.yingerbao.logging.SLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ListDataSave {
    private static final String TAG = ListDataSave.class.getSimpleName();
    
    private SharedPreferences preferences;  
    private SharedPreferences.Editor editor;  
  
    public ListDataSave(Context mContext, String preferenceName) {  
        preferences = mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);  
        editor = preferences.edit();  
    }  
  
    /** 
     * 保存List 
     * @param tag 
     * @param datalist 
     */  
    public <T> void setDataList(String tag, List<T> datalist) {  
        if (null == datalist || datalist.size() <= 0)  
            return;  
  
        Gson gson = new Gson();  
        //转换成json数据，再保存  
        String strJson = gson.toJson(datalist);  
        editor.clear();  
        editor.putString(tag, strJson);  
        editor.commit();  
  
    }  
  
    /** 
     * 获取List 
     * @param tag 
     * @return 
     */  
    public <T> List<T> getDataList(String tag) {  
        List<T> datalist=new ArrayList<T>();  
        try {
            String strJson = preferences.getString(tag, null);  
            if (null == strJson) {  
                return datalist;  
            }  
            Gson gson = new Gson();  
            datalist = gson.fromJson(strJson, new TypeToken<List<T>>() {  
            }.getType());  
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
        return datalist;  
    }  
}
