package com.dslm.fundcat;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

//数据下载，数据库以及excel储存类
public class DataProcess
{
    public static void saveData(Handler handler, String data)
    {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(
                                data.getBytes(
                                        Charset.forName("utf8"))),
                        Charset.forName("utf8")));
        String line;
    
        String name = null;
        String code = null;
        long newestDate = 0;
        double newestNetWorthTrend = 0;
        double newestEquityReturn = 0;
        ArrayList<Long> dates = new ArrayList<>();
        HashMap<Long, Double> netWorthTrend = new HashMap<Long, Double>();
        HashMap<Long, Double> equityReturn = new HashMap<Long, Double>();
        HashMap<Long, Double> acWorthTrend = new HashMap<Long, Double>();
        
        try
        {
            while ((line = br.readLine()) != null)
            {
                if (line.length() < 10) continue;
                if (line.startsWith("var fS_name = "))
                {
                    name = line.substring(15, line.length() - 1);
                } else if (line.startsWith("var fS_code = "))
                {
                    code = line.substring(15, line.length() - 1);
                } else if (line.startsWith("var Data_netWorthTrend = "))
                {
                    JSONArray analysis_1 = new JSONArray(line.substring(25, line.length()));
                    for(int i = 0; i < analysis_1.length(); i++)
                    {
                        JSONObject jo = analysis_1.optJSONObject(i);
                        dates.add(jo.getLong("x"));
                        netWorthTrend.put(dates.get(i), jo.getDouble("y"));
                        equityReturn.put(dates.get(i), jo.getDouble("equityReturn"));
                    }
                    newestDate = dates.get(analysis_1.length() - 1);
                    newestNetWorthTrend = netWorthTrend.get(newestDate);
                    newestEquityReturn = equityReturn.get(newestDate);
                } else if (line.startsWith("var Data_ACWorthTrend = "))
                {
                    JSONArray analysis_2 = new JSONArray(line.substring(24, line.length()));
                    for(int i = 0; i < analysis_2.length(); i++)
                    {
                        JSONArray ja = analysis_2.optJSONArray(i);
                        acWorthTrend.put(ja.optLong(0), ja.optDouble(1));
                    }
                }
            }
        }
        catch (IOException e)
        {
            Log.e("数据解析", "saveData: ", e);
        }
        catch (JSONException e)
        {
            Log.e("数据解析", "saveData: ", e);
        }
        
        OpenHelper openHelper = MainActivity.openHelper;
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        FundDAO fundDAO = new FundDAO(sqLiteDatabase);
        SimpleFundData fundData = new SimpleFundData();
        fundData.setCode(code);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        fundData.setDate(sdf.format(new Date(newestDate)));
        fundData.setNetWorthTrend(newestNetWorthTrend);
        fundData.setEquityReturn(newestEquityReturn);
    
        Message message = new Message();
        message.obj = fundData;
        if(fundDAO.existedName(fundData).getName() != null)
        {
            fundDAO.update(fundData);
            sqLiteDatabase.close();
            
            message.what = HandlerWhatValue.sameData;
        }
        else
        {
            fundData.setName(name);
            fundDAO.insert(fundData);
            sqLiteDatabase.close();
    
            message.what = HandlerWhatValue.addedData;
        }
        handler.sendMessage(message);
    }
}
