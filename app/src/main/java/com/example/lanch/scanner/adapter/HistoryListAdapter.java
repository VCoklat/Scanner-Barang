package com.example.lanch.scanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.lanch.scanner.R;
import com.example.lanch.scanner.data.DataBarcode;

import java.util.List;

/**
 * Created by mac on 8/8/16.
 */
public class HistoryListAdapter extends ArrayAdapter<DataBarcode>
{
    public HistoryListAdapter(Context context, int resource)
    {
        super(context, resource);
    }

    public HistoryListAdapter(Context context, int resource, List<DataBarcode> dataBarcodes)
    {
        super(context, resource, dataBarcodes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;

        if(v == null)
        {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(R.layout.table_list_history, parent,false);
        }

        DataBarcode dataBarcodeItem = getItem(position);

        if(dataBarcodeItem != null)
        {
            TextView date = (TextView) v.findViewById(R.id.dateId);
            TextView assetNumber = (TextView) v.findViewById(R.id.assetNumberId);
            TextView scanType = (TextView) v.findViewById(R.id.scanTypeId);


            if(date != null)
            {
                date.setText(dataBarcodeItem.getDateTime());
            }
            if(assetNumber != null)
            {
                assetNumber.setText(dataBarcodeItem.getAssetNumber());
            }
            if(scanType != null)
            {
                scanType.setText(dataBarcodeItem.getScanType());
            }
        }

        return v;
    }
}
