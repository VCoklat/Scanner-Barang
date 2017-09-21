package com.example.lanch.scanner.camera_scan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lanch.scanner.R;
import com.example.lanch.scanner.data.DBHelper;
import com.example.lanch.scanner.data.DataBarcode;
import com.example.lanch.scanner.dialogFragment.CameraSelectorDialogFragment;
import com.example.lanch.scanner.dialogFragment.FormatSelectorDialogFragment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler,
        FormatSelectorDialogFragment.FormatSelectorDialogListener,
        CameraSelectorDialogFragment.CameraSelectorDialogListener
{
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private static final String JUMBLAH_SCAN = "JUMBLAH_SCAN";
    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;
    ArrayList<String> hasilscan = new ArrayList<String>();
    private TextView text;
    int jumlah_scan = 0;
    MediaPlayer mp = new MediaPlayer();
    DBHelper dbHelper;

    @Override
    public void onCreate(Bundle state)
    {
        super.onCreate(state);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_scan);
        text = (TextView) findViewById(R.id.textView);
        mp = MediaPlayer.create(this, R.raw.barcode_sound);

        dbHelper = DBHelper.getmInstance(this);
        try
        {
            hasilscan.addAll(dbHelper.getAllHaventBeenSentData());
            for(String data: hasilscan)
            {
                Log.d("data ", data);
            }

            jumlah_scan = hasilscan.size();
            Log.d("hasilScan.size()", String.valueOf(jumlah_scan));
            text.setText(String.valueOf(jumlah_scan));
        }
        catch (IndexOutOfBoundsException exception)
        {
            Log.d("INDEX OUT OF BOUND","INDEX OUT OF BOUND INDEX OUT OF BOUND INDEX OUT OF BOUND");
            jumlah_scan = 0;
        }




        if (state != null)
        {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
            mCameraId = state.getInt(CAMERA_ID, -1);
            jumlah_scan = state.getInt(JUMBLAH_SCAN,jumlah_scan);
            text.setText(String.valueOf(jumlah_scan));
        }
        else
        {
            mFlash = false;
            mAutoFocus = true;
            mSelectedIndices = null;
            mCameraId = -1;

        }

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.Scan);
        mScannerView = new ZXingScannerView(this);
        setupFormats();
        contentFrame.addView(mScannerView);
        Typeface roboto = Typeface.createFromAsset(this.getAssets(),
                "font/Roboto-Light.ttf");
        TextView text2 = (TextView) findViewById(R.id.textView2);
        TextView text = (TextView) findViewById(R.id.textView);
        text2.setTypeface(roboto);
        text.setTypeface(roboto);

        Button next = (Button) findViewById(R.id.button);
        next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(ScanActivity.this, PhotoUploadActivity.class);
                i.putExtra("yourlist", hasilscan);
                i.putExtra("dateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", DateFormatSymbols.getInstance()).format(new Date()));
                //Change the activity.
                startActivity(i);
            }
        });
        next.setTypeface(roboto);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
        outState.putInt(JUMBLAH_SCAN,jumlah_scan);
    }
    

    @Override
    public void handleResult(Result rawResult)
    {
        Log.d("Result", rawResult.getText());
        String assetNumber = rawResult.getText().trim();
        String date = new SimpleDateFormat("EEEE, dd-MM-yyyy / H:m:s", DateFormatSymbols.getInstance()).format(new Date());
        hasilscan.add(assetNumber);
        final Toast toast = Toast.makeText(getApplicationContext(), "Berhasil Scan", Toast.LENGTH_SHORT);
        toast.show();
        mp.start();
        jumlah_scan++;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("asset_number", assetNumber);
        editor.putString("date", date);
        editor.apply();
        
        DataBarcode dataBarcode = new DataBarcode(assetNumber,
                new SimpleDateFormat("EEEE, dd-MM-yyyy / H:m:s", DateFormatSymbols.getInstance()).format(new Date()),
                "Photo Barcode Scan", "0");
        
        dbHelper.addDataBarcode(dataBarcode);

        text.setText(String.valueOf(jumlah_scan));


        mScannerView.resumeCameraPreview(ScanActivity.this);

    }

    

    public void closeMessageDialog()
    {
        closeDialog("scan_results");
    }

    public void closeFormatsDialog()
    {
        closeDialog("format_selector");
    }

    public void closeDialog(String dialogName)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag(dialogName);
        if (fragment != null)
        {
            fragment.dismiss();
        }
    }


    @Override
    public void onFormatsSaved(ArrayList<Integer> selectedIndices)
    {
        mSelectedIndices = selectedIndices;
        setupFormats();
    }

    @Override
    public void onCameraSelected(int cameraId)
    {
        mCameraId = cameraId;
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    public void setupFormats()
    {
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
        if (mSelectedIndices == null || mSelectedIndices.isEmpty())
        {
            mSelectedIndices = new ArrayList<Integer>();
            for (int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++)
            {
                mSelectedIndices.add(i);
            }
        }

        for (int index : mSelectedIndices)
        {
            formats.add(ZXingScannerView.ALL_FORMATS.get(index));
        }
        if (mScannerView != null)
        {
            mScannerView.setFormats(formats);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mScannerView.stopCamera();
        closeMessageDialog();
        closeFormatsDialog();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}