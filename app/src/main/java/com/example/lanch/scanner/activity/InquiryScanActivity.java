package com.example.lanch.scanner.activity;

import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.lanch.scanner.R;
import com.example.lanch.scanner.dialogFragment.CameraSelectorDialogFragment;
import com.example.lanch.scanner.dialogFragment.FormatSelectorDialogFragment;
import com.example.lanch.scanner.network.CustomJSONRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by mac on 8/7/16.
 */
public class InquiryScanActivity extends BaseScannerActivity implements ZXingScannerView.ResultHandler,
        FormatSelectorDialogFragment.FormatSelectorDialogListener,
        CameraSelectorDialogFragment.CameraSelectorDialogListener
{
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMAT";
    private static final String CAMERA_ID = "CAMERA_ID";

    private boolean mAutoFocus;
    private int mCameraId = -1;
    private ZXingScannerView mScannerView;
    private ArrayList<Integer> mSelectedIndices;
    private String url = "http://afixa.anabatic.com/api/opname/enquiry";
    MediaPlayer mediaPlayer = new MediaPlayer();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_scan);


        mediaPlayer = MediaPlayer.create(this,R.raw.barcode_sound);


        if (savedInstanceState != null)
        {
            mAutoFocus = savedInstanceState.getBoolean(AUTO_FOCUS_STATE, true);
            mSelectedIndices = savedInstanceState.getIntegerArrayList(SELECTED_FORMATS);
            mCameraId = savedInstanceState.getInt(CAMERA_ID, -1);
            Toast.makeText(InquiryScanActivity.this, "state does not equal to null", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mAutoFocus = true;
            mSelectedIndices = null;
            mCameraId = -1;
            Toast.makeText(InquiryScanActivity.this, "state equals to null", Toast.LENGTH_SHORT).show();

        }

        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.scanInquiry);
        mScannerView = new ZXingScannerView(this);
        setupFormats();
        viewGroup.addView(mScannerView);

        Typeface roboto = Typeface.createFromAsset(this.getAssets(),
                "font/Roboto-Light.ttf");
        Button button1 = (Button) findViewById(R.id.button_inquiry);
        button1.setTypeface(roboto);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(mCameraId);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
    }

    @Override
    public void handleResult(Result result)
    {
        Log.d("InquiryResult", result.getText());
        final Toast toast = Toast.makeText(getApplicationContext(), "Berhasil Scan", Toast.LENGTH_SHORT);
        toast.show();
        mediaPlayer.start();

        mScannerView.resumeCameraPreview(InquiryScanActivity.this);
        Button submit = (Button) findViewById(R.id.button_inquiry);
        submit.setOnClickListener(new View.OnClickListener()
        {
            JSONObject jsonBody = new JSONObject();

            @Override
            public void onClick(View v)
            {
                CustomJSONRequest customJSONRequest = new CustomJSONRequest(Request.Method.POST, url, null,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response)
                            {

                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {

                            }
                        });
                AlertDialog.Builder builder = new AlertDialog.Builder(InquiryScanActivity.this);

                LayoutInflater inflater = getLayoutInflater();
                inflater.inflate(R.layout.custom_full_image, (ViewGroup) findViewById(R.id.layout_root));
                builder.create().setTitle("Inquiry Detail");
                builder.setPositiveButton("Ok",null);
            }
        });
    }



    @Override
    protected void onPause()
    {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onFormatsSaved(ArrayList<Integer> selectedIndices)
    {
        mSelectedIndices = selectedIndices;
        setupFormats();
    }

    private void setupFormats()
    {
        List<BarcodeFormat> formats = new ArrayList<>();
        if(mSelectedIndices == null|| mSelectedIndices.isEmpty())
        {
            mSelectedIndices = new ArrayList<>();
            for(int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++)
            {
                mSelectedIndices.add(i);
            }
        }

        for(int index : mSelectedIndices)
        {
            formats.add(ZXingScannerView.ALL_FORMATS.get(index));
        }

        if(mScannerView != null)
        {
            mScannerView.setFormats(formats);
        }
    }

    @Override
    public void onCameraSelected(int cameraId)
    {
        mCameraId = cameraId;
    }
}