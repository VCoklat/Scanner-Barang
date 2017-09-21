package com.example.lanch.scanner.network;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CustomJSONRequest extends JsonObjectRequest
{

    public CustomJSONRequest(int method, String url, JSONObject requestBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener)
    {
        super(method, url, requestBody, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError
    {
        HashMap<String,String> header = new HashMap<>();
        header.put("Content-Type", "application/json; charset=utf-8");
        return super.getHeaders();
    }

    @Override
    public RetryPolicy getRetryPolicy()
    {
        return super.getRetryPolicy();
    }
}
