package com.dslm.fundcat;

public class SimpleLOGData
{
    private String code;
    private String direct;
    private String date;
    private double units;
    private double money;
    
    public SimpleLOGData()
    {
        this(null, null, null, 0, 0);
    }
    
    public SimpleLOGData(String code,String direct,String date,double number,double money)
    {
        this.code = code;
        this.direct = direct;
        this.date = date;
        this.units = number;
        this.money = money;
    }
    
    public double getUnits()
    {
        return units;
    }
    
    public double getMoney()
    {
        return money;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public String getDate()
    {
        return date;
    }
    
    public String getDirect()
    {
        return direct;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public void setDate(String date)
    {
        this.date = date;
    }
    
    public void setUnits(double number)
    {
        this.units = number;
    }
    
    public void setDirect(String direct)
    {
        this.direct = direct;
    }
    
    public void setMoney(double money)
    {
        this.money = money;
    }
}