package com.dslm.fundcat;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

//数据库操作类
public class FundDAO
{
    private SQLiteDatabase database;
    
    public FundDAO(SQLiteDatabase sqLiteDatabase)
    {
        this.database = sqLiteDatabase;
    }
    
    public boolean insert(SimpleFundData fundData)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("fund_code", fundData.getCode());
        contentValues.put("name", fundData.getName());
        contentValues.put("newest_date", fundData.getDate());
        contentValues.put("net_worth_trend", fundData.getNetWorthTrend());
        contentValues.put("equity_return", fundData.getEquityReturn());
        long insertResult = database.insert("fund_list", null, contentValues);
        return insertResult != -1;
    }

    public boolean insert(SimpleLOGData fundData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("fund_code", fundData.getCode());
        contentValues.put("direct", fundData.getDirect());
        contentValues.put("log_date", fundData.getDate());
        contentValues.put("units", fundData.getUnits());
        contentValues.put("money", fundData.getMoney());
        long insertResult = database.insert("fund_log", null, contentValues);
        return insertResult != -1;
    }

    public boolean delete(String code)
    {
        int deleteResult = database.delete("fund_list", "fund_code=?", new String[] {code});
        return deleteResult != 0;
    }
    
    public boolean update(SimpleFundData fundData)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("newest_date", fundData.getDate());
        contentValues.put("net_worth_trend", fundData.getNetWorthTrend());
        contentValues.put("equity_return", fundData.getEquityReturn());
        int updateResult = database.update("fund_list", contentValues,
                "fund_code=?", new String[]{fundData.getCode() + ""});
        return updateResult != 0;
    }
    
    public boolean exchange(int fromPosition, int toPosition)
    {
        Cursor cursorF = database.query("fund_list", null,
                "fund_order=?", new String[]{(fromPosition + 1) + ""}, null,
                null, null, null);
        ContentValues contentValueF = new ContentValues();
        while (cursorF.moveToNext())
        {
            contentValueF.put("fund_code", cursorF.getString(1));
            contentValueF.put("name", cursorF.getString(2));
            contentValueF.put("newest_date", cursorF.getString(3));
            contentValueF.put("net_worth_trend", cursorF.getDouble(4));
            contentValueF.put("equity_return", cursorF.getDouble(5));
        }
        cursorF.close();

        Cursor cursorT = database.query("fund_list", null,
                "fund_order=?", new String[]{(toPosition + 1) + ""}, null,
                null, null, null);
        ContentValues contentValueT = new ContentValues();
        while (cursorT.moveToNext())
        {
            contentValueT.put("fund_code", cursorT.getString(1));
            contentValueT.put("name", cursorT.getString(2));
            contentValueT.put("newest_date", cursorT.getString(3));
            contentValueT.put("net_worth_trend", cursorT.getDouble(4));
            contentValueT.put("equity_return", cursorT.getDouble(5));
        }
        cursorT.close();
    
        database.update("fund_list", contentValueF,
                "fund_order=?", new String[]{(toPosition + 1) + ""});
        database.update("fund_list", contentValueT,
                "fund_order=?", new String[]{(fromPosition + 1) + ""});
        
        return false;
    }
    
    public boolean move(int fromPosition, int toPosition)
    {
        
        return false;
    }
    
    
    public SimpleFundData existedName(SimpleFundData fundData)
    {
        Cursor cursor = database.query("fund_list", null,
                "fund_code=?", new String[]{fundData.getCode()}, null,
                null, null, null);
        while (cursor.moveToNext())
        {
            fundData.setName(cursor.getString(2));
        }
        cursor.close();
        return fundData;
    }
    
    public List<SimpleFundData> queryAll()
    {
        List<SimpleFundData> fundDataList = new ArrayList<>();
            Cursor cursor = database.query("fund_list", null,
                    null, null, null,
                    null, null, null);
        while (cursor.moveToNext())
        {
            SimpleFundData fundData = new SimpleFundData();
            fundData.setCode(cursor.getString(1));
            fundData.setName(cursor.getString(2));
            fundData.setDate(cursor.getString(3));
            fundData.setNetWorthTrend(cursor.getDouble(4));
            fundData.setEquityReturn(cursor.getDouble(5));
            List<SimpleLOGData> logDataList = query(fundData.getCode());
            double totalUnits = 0.0;
            double totalCost = 0.0;
            double totalReturn = 0.0;
            for (int i = 0; i < logDataList.size(); i++) {
                SimpleLOGData logData = logDataList.get(i);
                if (logData.getDirect().equals("买")) {
                    totalUnits += logData.getUnits();
                    totalCost += logData.getMoney();
                } else {
                    assert totalUnits > 0.0;
                    double d = totalCost / totalUnits;
                    totalReturn += logData.getMoney() - d * logData.getUnits();
                    totalUnits -= logData.getUnits();
                    totalCost -= d * logData.getMoney();
                }
            }
            fundData.setTotalCost(totalCost);
            fundData.setTotalEquityReturn(totalReturn);
            fundData.setUnits(totalUnits);
            fundDataList.add(fundData);
        }
        cursor.close();
        return fundDataList;
    }

    public List<SimpleLOGData> query(String code) {
        List<SimpleLOGData> fundDataList = new ArrayList<>();
        Cursor cursor = database.query("fund_log", null, "fund_code=?", new String[]{code}, null, null, "log_date", null);
        while (cursor.moveToNext()) {
            SimpleLOGData fundData = new SimpleLOGData();
            fundData.setCode(cursor.getString(1));
            fundData.setDate(cursor.getString(2));
            fundData.setDirect(cursor.getString(3));
            fundData.setUnits(cursor.getDouble(4));
            fundData.setMoney(cursor.getDouble(5));
            fundDataList.add(fundData);
        }
        return fundDataList;
    }

 
    public List<String> getCodeList()
    {
        List<String> codeList = new ArrayList<>();
        Cursor cursor = database.query("fund_list", null,
                null, null, null,
                null, null, null);
        while (cursor.moveToNext())
        {
            codeList.add(cursor.getString(1));
        }
        cursor.close();
        return codeList;
    }
    
    public List<String> getCodeAndNameList()
    {
        List<String> codeAndNameList = new ArrayList<>();
        Cursor cursor = database.query("fund_list", null,
                null, null, null,
                null, null, null);
        while (cursor.moveToNext())
        {
            codeAndNameList.add(cursor.getString(1) + " "
            +cursor.getString(2));
        }
        cursor.close();
        return codeAndNameList;
    }
}
