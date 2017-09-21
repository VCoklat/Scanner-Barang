package com.example.lanch.scanner.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.example.lanch.scanner.R;
import com.example.lanch.scanner.adapter.HistoryListAdapter;
import com.example.lanch.scanner.data.DBHelper;
import com.example.lanch.scanner.data.DataBarcode;

import java.util.ArrayList;

/**
 * Created by mac on 8/3/16.
 */
public class HistoryActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        DBHelper db = DBHelper.getmInstance(this);

        ListView lv = (ListView) findViewById(R.id.list);
        ArrayList<DataBarcode> list = (ArrayList<DataBarcode>) db.getAllData();


        HistoryListAdapter historyListAdapter = new HistoryListAdapter(this, R.layout.table_list_history, list);
        lv.setAdapter(historyListAdapter);

        for(DataBarcode dataBarcode : list)
        {
            String log = "AssetNumber: " + dataBarcode.getAssetNumber() + " DateTime: " + dataBarcode.getDateTime()
                    + " ScanType: " + dataBarcode.getScanType() + " Flag " + dataBarcode.getFlag();
            Log.d("Data barcode:", log);
        }
    }
}
