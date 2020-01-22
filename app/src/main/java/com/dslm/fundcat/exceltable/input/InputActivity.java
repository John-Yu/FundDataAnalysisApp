package com.dslm.fundcat.exceltable.input;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.dslm.fundcat.FundDAO;
import com.dslm.fundcat.MainActivity;
import com.dslm.fundcat.OpenHelper;
import com.dslm.fundcat.R;
import com.dslm.fundcat.SimpleLOGData;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class InputActivity extends AppCompatActivity implements DateSelectedListener
{
    Spinner fundPicker;
    EditText datePicker;
    EditText editTextF;
    EditText editTextG;
    Switch switchDir;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        Intent intent_accept = getIntent();
        
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        fundPick();
        DatePick();
        editTextF = findViewById(R.id.input_column_F);
        editTextG = findViewById(R.id.input_column_G);
        switchDir = findViewById(R.id.switch1);
    }
    
    public void fundPick()
    {
        fundPicker = (Spinner) findViewById(R.id.input_fund_picker);
        OpenHelper openHelper = MainActivity.openHelper;
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        FundDAO fundDAO = new FundDAO(sqLiteDatabase);
        List<String> funds = fundDAO.getCodeAndNameList();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, funds);
        sqLiteDatabase.close();
        
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fundPicker.setAdapter(adapter);
    }
    
    public void DatePick()
    {
        datePicker = (EditText) findViewById(R.id.input_date_picker);
        datePicker.setInputType(InputType.TYPE_NULL);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        datePicker.setText(sdf.format(new Date(System.currentTimeMillis())));
        datePicker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DateDialogFragment dateDialogFragment = new DateDialogFragment();
                dateDialogFragment.setDateSelectedListener(InputActivity.this);
                android.app.FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                dateDialogFragment.show(getFragmentManager(), "日期选择");
                transaction.commit();
            }
        });
    }
    
    @Override
    public void dateSelected(String date)
    {
        datePicker.setText(date);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_check_button, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            case R.id.id_check_button:
                item.setEnabled(false);
                if (editTextG.getText().toString().equals("") || editTextF.getText().toString().equals("")) {
                } else {
                    String code = String.valueOf(fundPicker.getSelectedItem()).substring(0, 6);
                    String date = datePicker.getText().toString();
                    SimpleLOGData logData = new SimpleLOGData();
                    logData.setCode(code);
                    logData.setDate(date);
                    if (switchDir.isChecked()) {
                        logData.setDirect("卖");
                    } else logData.setDirect("买");
                    DecimalFormat df = new DecimalFormat("#.00");
                    logData.setMoney(
                            Double.valueOf(
                                    df.format(
                                            Double.valueOf(
                                                    editTextF.getText().toString()))));
                    logData.setUnits(
                            Double.valueOf(
                                    df.format(
                                            Double.valueOf(
                                                    editTextG.getText().toString()))));

                    try {
                        OpenHelper openHelper = MainActivity.openHelper;
                        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
                        FundDAO fundDAO = new FundDAO(sqLiteDatabase);
                        fundDAO.insert(logData);
                    } catch (Exception e) {
                        Log.e("写入基金记录问题", "saveData: ", e);
                    }
                }
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
