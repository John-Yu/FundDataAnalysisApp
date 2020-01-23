package com.dslm.fundcat;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dslm.fundcat.exceltable.ExcelTableActivity;

import java.util.Collections;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class FundListRecyclerAdapter extends RecyclerView.Adapter
{
    private List<SimpleFundData> fundDataList;
    private Context context;
    
    public FundListRecyclerAdapter(List<SimpleFundData> fundDataList, Context context)
    {
        this.fundDataList = fundDataList;
        this.context = context;
    }
    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        switch (i) {
            case 0:
                return new TotalViewHolder(LayoutInflater.from(context).inflate(R.layout.item_fund_total, viewGroup, false));
            case 1:
                return new HeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.item_fund_header, viewGroup, false));
            default:
                return new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.fund_item_view, viewGroup, false));
        }
    }
    
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i)
    {
        SimpleFundData fundData;
        if (i < 2) {
            if (i == 0) {
                double totalMoney = 0.0;
                double totalReturn = 0.0;
                double totalHoldReturn = 0.0;
                for (int j = 0; j < fundDataList.size(); j++) {
                    fundData = fundDataList.get(j);
                    double money = fundData.getUnits() * fundData.getNetWorthTrend();
                    totalMoney += money;
                    totalHoldReturn += money - fundData.getTotalCost();
                    totalReturn += fundData.getTotalEquityReturn();
                }
                final TotalViewHolder tvTmp = (TotalViewHolder) viewHolder;
                tvTmp.totalMoney.setText(String.format("%.2f", totalMoney));
                tvTmp.holdReturn.setText(String.format("%.2f", totalHoldReturn));
                tvTmp.totalReturn.setText(String.format("%.2f", totalReturn));
            }
            return;
        }
        final CustomViewHolder customViewHolder = (CustomViewHolder) viewHolder;
        fundData = fundDataList.get(i - 2);
        customViewHolder.name.setText(fundData.getName());
        double dHold = fundData.getNetWorthTrend() * fundData.getUnits();
        customViewHolder.netWorthTrend.setText(String.format("%.2f", dHold));
        double dCost = fundData.getTotalCost();
        if (dCost > 0.0) dCost = (dHold - dCost) / dCost;
        else dCost = 0.0;
        customViewHolder.equityReturn.setText(String.format("%.2f%%", dCost));

        if (dCost < 0)
        {
            customViewHolder.netWorthTrend.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
            customViewHolder.equityReturn.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
        } else if (dCost > 0)
        {
            customViewHolder.netWorthTrend.setTextColor(Color.RED);
            customViewHolder.equityReturn.setTextColor(Color.RED);
        }
        else
        {
            customViewHolder.netWorthTrend.setTextColor(Color.BLACK);
            customViewHolder.equityReturn.setTextColor(Color.BLACK);
        }
    
        customViewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!MainActivity.isDragging)
                {
                    Intent toExcelTable = new Intent();
                    toExcelTable.setClass(MainActivity.context, ExcelTableActivity.class);
                    toExcelTable.putExtra("name", customViewHolder.name.getText());
                    MainActivity.context.startActivity(toExcelTable);
                }
            }
        });
        customViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                return false;
            }
        });
    }
    
    @Override
    public int getItemCount()
    {
        return fundDataList.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView netWorthTrend;
        TextView equityReturn;

        public CustomViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.text_name);
            netWorthTrend = (TextView) view.findViewById(R.id.text_net_worth);
            equityReturn = (TextView) view.findViewById(R.id.text_hold_return);
        }

    }

    private class TotalViewHolder extends RecyclerView.ViewHolder
    {
        TextView totalMoney;
        TextView holdReturn;
        TextView totalReturn;

        public TotalViewHolder(View view)
        {
            super(view);
            totalMoney = (TextView) view.findViewById(R.id.id_total_money);
            holdReturn = (TextView) view.findViewById(R.id.id_hold_return);
            totalReturn = (TextView) view.findViewById(R.id.id_total_return);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }

    public void addData(SimpleFundData fundData)
    {
        //在list中添加数据，并通知条目加入一条
        fundDataList.add(fundDataList.size(), fundData);
        //添加动画
        notifyItemInserted(fundDataList.size() - 1);
    }
    
    public void refreshData()
    {
        OpenHelper openHelper = MainActivity.openHelper;
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        FundDAO fundDAO = new FundDAO(sqLiteDatabase);
        fundDataList.clear();
        fundDataList.addAll(fundDAO.queryAll());
        sqLiteDatabase.close();
        notifyDataSetChanged();
    }
    
    public interface OnItemClickListener
    {
        void onClick( int position);
        void onLongClick( int position);
    }
    
    public String getCode(int position)
    {
        return fundDataList.get(position).getCode();
    }
    
    
    public String getName(int position)
    {
        return fundDataList.get(position).getName();
    }
    
    public void exchange(int a, int b)
    {
        Collections.swap(fundDataList, a, b);
    }
}
