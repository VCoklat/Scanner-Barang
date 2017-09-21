package com.example.lanch.scanner.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lanch.scanner.GPS;
import com.example.lanch.scanner.R;
import com.example.lanch.scanner.activity.loginRegisterActivityRequest.LoginActivity;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
{
    private static final int ZXING_CAMERA_PERMISSION = 1;
    private Class<?> mClss;

    private GPS gps = new GPS(this);

    @Override
    public void onCreate(Bundle state)
    {
        super.onCreate(state);
        setContentView(R.layout.activity_main);


        //checking if gps is already turned on
// Get Location Manager and check for GPS & Network location services
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Services Not Active");
            builder.setMessage("Please enable Location Services and GPS");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }


        Typeface roboto = Typeface.createFromAsset(this.getAssets(),
                "font/Roboto-Light.ttf");
        //Button about = (Button) findViewById(R.id.button_about);
        Log.d("date", new SimpleDateFormat("EEEE, dd-MM-yyyy", DateFormatSymbols.getInstance()).format(new Date()));

        TextView text = (TextView) findViewById(R.id.textView6);

        PackageInfo pInfo = null;
        try
        {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        text.setText(String.valueOf(version));
        /*about.setTypeface(roboto);
        about.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(MainActivity.this, Version.class);
                //Change the activity.
                startActivity(i);
            }
        });*/

        final ImageView scanBarang = (ImageView) findViewById(R.id.button_scan);

        scanBarang.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });

        final ImageView inquiry = (ImageView) findViewById(R.id.button_inquiry);

        inquiry.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, InquiryScanActivity.class);
                startActivity(intent);
            }
        });

        ImageView history = (ImageView) findViewById(R.id.button_history);
        history.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });


        final ImageView camera = (ImageView) findViewById(R.id.button_camera);
        camera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, com.example.lanch.scanner.camera_scan.ScanActivity.class);
                startActivity(intent);
            }
        });

        ImageView logout = (ImageView) findViewById(R.id.button_logout);
        logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("finish", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        ImageView scan = (ImageView) findViewById(R.id.scan);
        scan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                TextView text = (TextView) findViewById(R.id.txt_scan);
                scanBarang.setBackgroundResource(R.drawable.multiplefix);
                inquiry.setBackgroundResource(R.drawable.enquiryfix);
                camera.setBackgroundResource(R.drawable.scanfix);
                text.setText("");
            }
        });
    }


    public void launchScanBarang(View v)
    {
        scanBarangActvity(ScanActivity.class);
    }

    public void scanBarangActvity(Class<?> clss)
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            mClss = clss;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        }
        else
        {
            Intent intent = new Intent(this, clss);
            startActivity(intent);
        }
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        gps.getLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(mClss != null)
                    {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                    }
                } else
                {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

}