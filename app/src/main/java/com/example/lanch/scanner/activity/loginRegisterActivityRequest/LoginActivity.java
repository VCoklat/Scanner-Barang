package com.example.lanch.scanner.activity.loginRegisterActivityRequest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.lanch.scanner.MyApplication;
import com.example.lanch.scanner.R;
import com.example.lanch.scanner.activity.MainActivity;
import com.example.lanch.scanner.network.CustomJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements Response.Listener<JSONObject>,Response.ErrorListener
{
    private static final String LOGIN_REQUEST_URL = "http://afixa.anabatic.com/api/login";
    private ProgressDialog progressDialog;
    private String username = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Typeface roboto = Typeface.createFromAsset(this.getAssets(),
                "font/Roboto-Light.ttf");

        final EditText etUsername = (EditText) findViewById(R.id.etUsername);

        etUsername.setTypeface(roboto);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);

        etPassword.setTypeface(roboto);





        final Button bLogin = (Button) findViewById(R.id.bSignIn);
        bLogin.setTypeface(roboto);

        bLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                username = etUsername.getText().toString();
                password = etPassword.getText().toString();


                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Username", username);
                editor.apply();

                Log.d("username password", username + password);


                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setTitle("Please wait");
                progressDialog.setMessage("Fetching Data");
                progressDialog.show();
                progressDialog.setCancelable(false);
                try
                {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("Username", username);
                    jsonBody.put("Password", password);
                    jsonBody.put("grant_type", "password");

                    CustomJSONRequest jsonRequest = new CustomJSONRequest(Request.Method.POST, LOGIN_REQUEST_URL,jsonBody,
                            LoginActivity.this,LoginActivity.this);
                    MyApplication.getInstance().addToReqQueue(jsonRequest);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
               // Intent intent = new Intent(LoginActivity.this, MainActivity.class);
               // startActivity(intent);

            }
        });
    }


    @Override
    public void onErrorResponse(VolleyError error)
    {
        progressDialog.dismiss();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        alertDialog.setMessage("Login Failed").setNegativeButton("Retry", null).create().show();
        Log.d("error response", "error " + error.getMessage());
    }

    @Override
    public void onResponse(JSONObject response)
    {
        try
        {
            if(response.getString("Message").equals("Success"))
            {
                Log.d("berhasil", "yes berhasil login");
                progressDialog.dismiss();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else
            {
                progressDialog.dismiss();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
                alertDialog.setMessage("Login Failed").setNegativeButton("Retry", null).create().show();
            }
        } catch (JSONException e)
        {
            progressDialog.dismiss();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
            alertDialog.setMessage("Login Failed").setNegativeButton("Retry", null).create().show();
            e.printStackTrace();
        }
    }
}
