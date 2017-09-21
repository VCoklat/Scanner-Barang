package com.example.lanch.scanner;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
/**
 * Created by vanhauten on 28/07/16.
 */
public class Version extends AppCompatActivity
{
    @Override
    public void onCreate(Bundle state)
    {
        super.onCreate(state);

        setContentView(R.layout.version);
        TextView text = (TextView) findViewById(R.id.textView5);
        PackageInfo pinfo = null;
        try
        {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
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
    }
}
