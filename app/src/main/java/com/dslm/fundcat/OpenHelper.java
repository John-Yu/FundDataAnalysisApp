package com.dslm.fundcat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OpenHelper extends SQLiteOpenHelper
{
    private static final String name = "fund_list.db";
    private static final int version = 3;
    public OpenHelper(Context context)
    {
        super(context, name, null, version);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("Create Table If Not Exists fund_list (" +
                "fund_order Integer primary key autoincrement," +
                "fund_code varchar(255) not null," +
                "name varchar(255)," +
                "newest_date date," +
                "net_worth_trend decimal(10,2)," +
                "equity_return decimal(10,2)," +
                "R Integer," +
                "return Integer)");
        db.execSQL("Create Table If Not Exists fund_log (" +
                "log_order Integer primary key autoincrement," +
                "fund_code varchar(25) not null," +
                "log_date date," +
                "direct varchar(25)," +
                "units decimal(10,2)," +
                "money decimal(10,2))");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }
}
