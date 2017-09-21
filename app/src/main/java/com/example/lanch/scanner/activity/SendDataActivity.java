package com.example.lanch.scanner.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.lanch.scanner.GPS;
import com.example.lanch.scanner.MyApplication;
import com.example.lanch.scanner.R;
import com.example.lanch.scanner.data.DBHelper;
import com.example.lanch.scanner.network.CustomJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vanhauten on 17/07/16.
 */
public class SendDataActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener
{

    private ListView lv;
    private String url = "http://afixa.anabatic.com/api/Opname/PostOpnameSingle";
    private ArrayList<String> list;

    private ArrayAdapter<String> arrayAdapter;
    ProgressDialog progressDialog;
    DBHelper db;
    private int dataSentIndex;
    boolean isDataSent = false;
    Double latitude1,longitude1;

//    @Override
//    public void onBackPressed()
//    {
//        Log.d("onBackPressed", "onBackPressed is called. " + "dataBarcodeList.size() = " + dataBarcodesList.size());
//        //cek jika user menekan tombol back dan tidak mengirim data
//        //untuk mencegah memasukkan data yang sama 2 kali
//        if(dataBarcodesList.size() != 0)
//        {
//            for (DataBarcode dataBarcode : dataBarcodesList)
//            {
//                db.addDataBarcode(dataBarcode);
//                Log.d("insert data backPressed" + dataBarcode.getAssetNumber(), "success");
//            }
//        }
//        finish();
//    }

    @Override
    public void onCreate(Bundle state)
    {
        super.onCreate(state);

        setContentView(R.layout.sent_data);
        db = DBHelper.getmInstance(this);
        lv = (ListView) findViewById(R.id.listView);
        list = (ArrayList<String>) getIntent().getSerializableExtra("yourlist");

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(arrayAdapter);

        progressDialog = new ProgressDialog(this);


        Typeface roboto = Typeface.createFromAsset(this.getAssets(),
                "font/Roboto-Light.ttf");
        TextView text = (TextView) findViewById(R.id.textView3);
        text.setTypeface(roboto);

        dataSentIndex = 1;
        Button back = (Button) findViewById(R.id.back);
        back.setTypeface(roboto);
        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
            }
        });

        GPS gps = new GPS(this);
        if(gps.canGetLocation()) { // gps enabled} // return boolean true/false
            latitude1 = gps.getLatitude(); // returns latitude
            longitude1 = gps.getLongitude(); // returns longitude
        }


        String longi1 = Double.toString(longitude1);
        String lati1 = Double.toString(latitude1);
        final String TAG = "lokasi";
        Log.e(TAG, longi1);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String username = sharedPreferences.getString("Username", "no name");
        final String longitude = sharedPreferences.getString("longitude",longi1);
        final String latitude = sharedPreferences.getString("latitude",lati1);
        Button submit = (Button) findViewById(R.id.button_submit);
        submit.setTypeface(roboto);
        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                isDataSent = true;
                progressDialog.setMessage("Sending " + list.size() + " data");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setIndeterminate(false);
                progressDialog.setProgressPercentFormat(null);
                progressDialog.setProgress(0);
                progressDialog.setMax(list.size());
                for (int index = 0; index < list.size(); index++)
                {
                    final JSONObject jsonBody = new JSONObject();
                    try
                    {
                        jsonBody.put("AssetNumber", list.get(index));
                        jsonBody.put("UserOpname", username);
                        jsonBody.put("OpnameDate", getIntent().getSerializableExtra("dateTime"));
                        jsonBody.put("Longtitude", longitude);
                        jsonBody.put("Latitude", latitude);
                        jsonBody.put("State", 1);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    Log.d("json", jsonBody.toString());
                    CustomJSONRequest customJSONRequest = new CustomJSONRequest(Request.Method.POST, url, jsonBody, SendDataActivity.this, SendDataActivity.this);
                    customJSONRequest.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    MyApplication.getInstance().getReqQueue().add(customJSONRequest);
                }
                progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        progressDialog.dismiss();
                    }
                });
                progressDialog.show();
                db.dumpingDatabase();
            }

        });
        db.close();


    }


    @Override
    public void onErrorResponse(VolleyError error)
    {
        Toast.makeText(getApplicationContext(), "Tidak Terkirim ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response)
    {

        try
        {
            JSONObject jsonObject = new JSONObject(response.toString());
            String assetNumber = jsonObject.getString("AssetNumber");

//            if(dataBarcodesList.size() == 0)
//            {
//                DataBarcode dataBarcode = new DataBarcode(assetNumber,
//                        new SimpleDateFormat("EEEE, dd-MM-yyyy / H:m:s", DateFormatSymbols.getInstance()).format(new Date()),
//                        "Single Scan", "1");
//                db.addDataBarcode(dataBarcode);
//                Log.d("insert data ", jsonObject.getString("AssetNumber"));
//            }
//            else
//            {
            db.updateDataBarcode(assetNumber);
            Log.d("update data ", jsonObject.getString("AssetNumber"));
//            }

            Log.d("response", response.toString());


            progressDialog.setProgress(dataSentIndex++);
            if (list.size() != 0)
            {
                Log.d("list size", String.valueOf(list.size()));
                list.remove(jsonObject.getString("AssetNumber"));
                arrayAdapter.notifyDataSetChanged();
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            e.printStackTrace();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
