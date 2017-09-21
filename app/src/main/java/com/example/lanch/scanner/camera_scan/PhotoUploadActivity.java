package com.example.lanch.scanner.camera_scan;

/**
 * Created by vanhauten on 26/08/16.
 */

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.example.lanch.scanner.GPS;
import com.example.lanch.scanner.MyApplication;
import com.example.lanch.scanner.R;
import com.example.lanch.scanner.network.CustomJSONRequest;
import com.example.lanch.scanner.network.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoUploadActivity extends AppCompatActivity implements View.OnClickListener,Response.Listener<NetworkResponse>,
        Response.ErrorListener
{

    private static final int CAPTURE_IMAGES_FROM_CAMERA = 1;
    private LinearLayout lnrImages;
    private Button btnAddPhots;
    private Button btnUploadImages;
    private String[] imagesPath;
    private ProgressDialog loading;
    private int dataUploadedIndex = 0;
    private ArrayList<String> list;

    private int image_count_before;
    private ArrayList<Bitmap> bitmapArrayList;

    ////////////////////////////////////////////////////////////////////////////////
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String mimeType = "multipart/form-data; boundary=" + boundary;
    private byte[] multipartBody;
    private JSONArray responseResult;
    Double latitude1,longitude1;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d("onCreate", "onCreate called");
        if(savedInstanceState == null)
        {
            Log.d("savedInstanceState", "savedInstanceState equals to null");
        }
        else
        {
            Log.d("savedInstanceState", "savedInstanceState equals does not equals to  null");
            image_count_before = savedInstanceState.getInt("image_count_before");
        }
        setContentView(R.layout.activity_main2);
        lnrImages = (LinearLayout)findViewById(R.id.lnrImages);
        btnAddPhots = (Button)findViewById(R.id.btnAddPhots);
        btnUploadImages = (Button)findViewById(R.id.btnUploadImages);
        btnAddPhots.setOnClickListener(this);
        btnUploadImages.setOnClickListener(this);
        loading = new ProgressDialog(this);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Log.d("onSaveInstanceState", "onSaveInstanceState called");
        super.onSaveInstanceState(outState);
        outState.putInt("image_count_before", image_count_before);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d("onDestroy", "onDestroy called");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d("onResume", "onResume called");
    }

    @Override
    protected void onPause()
    {       
        super.onPause();
        Log.d("onPause", "onPause called");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d("onStart", "onStart called");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.d("onStop", "onStop called");
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////




    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnAddPhots:
                startCameraActivity();
                break;
            case R.id.btnUploadImages:
                if(imagesPath != null)
                {
                    if(bitmapArrayList.size() >= 1)
                    {
                        try
                        {
                            uploadImage();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Toast.makeText(PhotoUploadActivity.this, imagesPath.length + " no of image are selected", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(PhotoUploadActivity.this," no images are selected", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void uploadImage() throws IOException
    {

        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll1);
        LinearLayout linearLayout = (LinearLayout) scrollView.getChildAt(0); //get linearlayout from its parent
        int imageCount = linearLayout.getChildCount(); // count how many images going to be uploaded

        Toast.makeText(PhotoUploadActivity.this,  imageCount + " images are selected", Toast.LENGTH_SHORT).show();
        loading.setMessage("Uploading " + imageCount + " data");
        loading.setMax(imageCount);
        loading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        loading.setIndeterminate(false);
        loading.setProgressPercentFormat(null);
        loading.setCancelable(false);
        loading.setProgress(0);
        loading.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                loading.dismiss();
            }
        });
        loading.show();


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        //build multipart body from  photos
        
        dataUploadedIndex = imageCount;
        for(int i = 0; i < imageCount ; i++)
        {
            ImageView imageView = (ImageView) linearLayout.getChildAt(i); //get image view from linear layout
            Log.d("imageView" + i, "imageV " + i);


            Log.d("upload Image", "UPLOADING !!!");
            byte[] imageFile = getImageBytes(((BitmapDrawable)imageView.getDrawable()).getBitmap()); //get array of byte from image bitmap
            
            try
            {
                buildPart(dos, imageFile, "images " + i);
                
            } catch (IOException e)
            {
                Log.d("ERROR MULTIPART", "ERROR MULTIPART !!!!!!!!!!!!!!!!!!!");
                e.printStackTrace();
            }
        }

        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
        multipartBody = baos.toByteArray();
        
        String UPLOAD_URL = "http://afixa.anabatic.com/api/opname/post";
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(UPLOAD_URL, null, mimeType, 
                multipartBody,this,this);
        
        //set timeout to 50 s
        multipartRequest.setRetryPolicy(new RetryPolicy()
        {
            //50 s to handle poor network connection
            @Override
            public int getCurrentTimeout()
            {
                return 50000;
            } 

            @Override
            public int getCurrentRetryCount()
            {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError
            {
                Log.d("retry error", error.toString());
            }
        });
        
        
        //Adding request to the queue
        MyApplication.getInstance().addToReqQueue(multipartRequest);
    }

    
    private void buildPart(DataOutputStream dataOutputStream, byte[] imageData, String fileName) throws IOException
    {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + fileName + ".jpg"+ "\"" +
         lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageData);
        int bytesAvailable = byteArrayInputStream.available();
        
        int maxBufferSize = 1024*1024;
        int bufferSize = Math.min(bytesAvailable,maxBufferSize);
        byte[] buffer = new byte[bufferSize];
        
        //read file and write it into form
        int bytesRead = byteArrayInputStream.read(buffer, 0, bufferSize);
        
        while(bytesRead > 0)
        {
            dataOutputStream.write(buffer,0,bufferSize);
            bytesAvailable = byteArrayInputStream.available();
            bufferSize = Math.min(bytesAvailable,maxBufferSize);
            bytesRead = byteArrayInputStream.read(buffer,0,bufferSize);
        }
        
        
        dataOutputStream.writeBytes(lineEnd);
    }
    
    private byte[] getImageBytes(Bitmap bmp)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos); //compress to jpg
        Log.d("Byte Count", String.valueOf(bmp.getByteCount()));
        return baos.toByteArray();
    }

    
    @Override
    public void onResponse(NetworkResponse response)
    {
        String respon = new String(response.data);
        try
        {
            loading.setProgress(dataUploadedIndex);
            dataUploadedIndex = 0;
            responseResult = new JSONArray(respon);
            Log.d("Multipart response",responseResult.toString());
            Log.d("dataUploadedIndex", String.valueOf(dataUploadedIndex));
            sendReport();
            
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error)
    {
        Toast.makeText(PhotoUploadActivity.this, "error uplot" , Toast.LENGTH_LONG).show();
        Log.d("error", error.toString());
    }

    private void sendReport() throws JSONException
    {

        list = (ArrayList<String>) getIntent().getSerializableExtra("yourlist");

        GPS gps = new GPS(this);
        if(gps.canGetLocation()) { // gps enabled} // return boolean true/false
            latitude1 = gps.getLatitude(); // returns latitude
            longitude1 = gps.getLongitude(); // returns longitude
        }


        String longi1 = Double.toString(longitude1);
        String lati1 = Double.toString(latitude1);
        for (int index = 0; index < list.size(); index++)
        {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
            Log.d("PhotoUpload context", String.valueOf(this.getApplicationContext()));
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("AssetNumber",sharedPreferences.getString("asset_number",list.get(index)));
            jsonRequest.put("OpNameDate",sharedPreferences.getString("date", String.valueOf(getIntent().getSerializableExtra("dateTime"))));
            jsonRequest.put("UserOpName",sharedPreferences.getString("Username","unidentified"));
            jsonRequest.put("Longtitude",sharedPreferences.getString("longtitude",longi1));
            jsonRequest.put("Latitude",sharedPreferences.getString("latitude",lati1));

            JSONArray jsonArray = new JSONArray();
            for(int i = 0; i < responseResult.length(); i++)
            {
                Log.d("responseResult length", String.valueOf(responseResult.length()));
                JSONObject jsonObject = new JSONObject(String.valueOf(responseResult.get(i)));
                Log.d("JSONObject",jsonObject.toString());
                Log.d("JSONOBJECT length", String.valueOf(jsonObject.length()));

                JSONObject jsonFile = new JSONObject();
                jsonFile.put("FileName",jsonObject.get("Name"));
                jsonFile.put("FileSize", jsonObject.get("Size"));
                jsonFile.put("FileType",jsonObject.get("Extension"));
                jsonArray.put(jsonFile);
                jsonRequest.put("File",jsonArray);

            }
            jsonRequest.put("State",1);
            Log.d("JSON REQUEST", jsonRequest.toString());
            CustomJSONRequest customJSONRequest = new CustomJSONRequest(Request.Method.POST,
                    "http://afixa.anabatic.com/api/Opname/PostOpnameSingle", jsonRequest, new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response)
                {
                    Log.d("Report response",response.toString());
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Log.d("Report error",error.toString());
                }
            });
            MyApplication.getInstance().addToReqQueue(customJSONRequest);
        }

    
    }
    
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void startCameraActivity()
    {
        Cursor cursor = loadCursor();

        image_count_before = cursor.getCount();

        Log.d("image_count_before", String.valueOf(image_count_before));
        cursor.close();

        Intent cameraIntent = new Intent(
                MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        cameraIntent = Intent.createChooser(cameraIntent, "Select Camera");
        List<ResolveInfo> activities = getPackageManager()
                .queryIntentActivities(cameraIntent, 0);
        if (activities.size() > 0)
            startActivityForResult(cameraIntent, CAPTURE_IMAGES_FROM_CAMERA);
        else
            Toast.makeText(this, "No Camera application", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if(resultCode == RESULT_CANCELED)
        {
            switch (requestCode)
            {

                case CAPTURE_IMAGES_FROM_CAMERA:

                    Cursor cursor = loadCursor();
                    Log.d("image_count_result", String.valueOf(image_count_before));
                    String[] paths = getImagePaths(cursor, image_count_before);
                    cursor.close();
                    imagesPath = paths;

                    bitmapArrayList = new ArrayList<>();
                    new AsyncTask<Void, Bitmap, Bitmap>()
                    {
                        @Override
                        protected Bitmap doInBackground(Void... voids)
                        {
                            Bitmap bitmap = null;

                            Log.d("imagesPath.length", String.valueOf(imagesPath.length));
                            for (int i = 0; i < imagesPath.length; i++)
                            {
                                try
                                {
                                    bitmap = Glide.with(PhotoUploadActivity.this)
                                            .load(new File(imagesPath[i]))
                                            .asBitmap().into(200, 200)
                                            .get();
                                    Log.d("path image " + i, imagesPath[i]);
                                    bitmapArrayList.add(bitmap);
                                    publishProgress(bitmap);
                                }
                                catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                } catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            
                            return bitmap;
                        }

                        @Override
                        protected void onProgressUpdate(Bitmap... values)
                        {
                            final ImageView imageView = new ImageView(PhotoUploadActivity.this);
                            imageView.setAdjustViewBounds(true);
                            imageView.setPadding(10, 10, 10, 10);
                            imageView.setImageBitmap(values[0]);
                            lnrImages.addView(imageView);
                            lnrImages.setPadding(10, 10, 10, 10);
                            super.onProgressUpdate(values);
                        }

                        @Override
                        protected void onPostExecute(Bitmap bitmap)
                        {
                            super.onPostExecute(bitmap);
                        }
                    }.execute();
                    
                    break;
                default:
                    break;

            }
        }
        else
        {
            Toast.makeText(this, "Try again", Toast.LENGTH_LONG).show();
        }
    }

    private Cursor loadCursor()
    {

        final String[] columns = { MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID };

        final String orderBy = MediaStore.Images.Media.DATE_ADDED;

        return getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
    }

    private String[] getImagePaths(Cursor cursor, int startPosition)
    {

        if(startPosition == 0)
        {
            Log.d("start position = 0 !!! ", String.valueOf(startPosition));
            startPosition = cursor.getCount();
        }

        int size = cursor.getCount() - startPosition;

        if (size <= 0)
            return null;

        String[] paths = new String[size];

        int dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        try
        {
            for (int i = startPosition; i < cursor.getCount(); i++)
            {

                cursor.moveToPosition(i);

                paths[i - startPosition] = cursor.getString(dataColumnIndex);
            }
        }
        finally
        {
            cursor.close();
        }

        Log.d("start position is ", String.valueOf(startPosition));
        Log.d("size ", String.valueOf(size));
        return paths;
    }


   
}