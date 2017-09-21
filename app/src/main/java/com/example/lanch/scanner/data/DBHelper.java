package com.example.lanch.scanner.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 8/1/16.
 */

//singleton sqlite class
public class DBHelper extends SQLiteOpenHelper
{

    private static DBHelper mInstance = null;
    //database version
    private static final int DATABASE_VERSION = 1;

    //database name
    private static final String DATABASE_NAME = "data_barcode_information.db";

    //table name
    private static final String DATA_BARCODE = "data_barcode";

    //table columns
    private static final String KEY_ID = "_id";
    private static final String KEY_AssetNumber = "asset_number";
    private static final String KEY_DateTime = "date_time";
    private static final String KEY_ScanType = "scan_type";
    private static final String KEY_FLAG = "flag"; // 0 belum terkirim  1 terkirim


    private Context mCtx;

    private DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mCtx = context;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + DATA_BARCODE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_AssetNumber + " TEXT,"
                + KEY_DateTime + " TEXT," + KEY_ScanType + " TEXT," + KEY_FLAG + " INTEGER" + ")";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATA_BARCODE);
        onCreate(sqLiteDatabase);
    }

    public static synchronized  DBHelper getmInstance(Context context)
    {
        if(mInstance == null)
        {
            mInstance = new DBHelper(context);
        }
        return mInstance;
    }


    public void addDataBarcode(DataBarcode dataBarcode)
    {
        Log.d("addDataBarcode","addDataBarcode called");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_AssetNumber,dataBarcode.getAssetNumber());
        values.put(KEY_DateTime, dataBarcode.getDateTime());
        values.put(KEY_ScanType, dataBarcode.getScanType());
        values.put(KEY_FLAG, dataBarcode.getFlag());

        db.insert(DATA_BARCODE,null,values);
        db.close();
    }

    public void insertDataBarcode(DataBarcode dataBarcode)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_AssetNumber, dataBarcode.getAssetNumber());
        values.put(KEY_DateTime,dataBarcode.getDateTime());
        values.put(KEY_ScanType, dataBarcode.getScanType());
        values.put(KEY_FLAG, "0");

        db.insert(DATA_BARCODE,null,values);
        db.close();
    }

    public void updateDataBarcode(String assetNumber)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FLAG, "1");

        String[] args = new String[]{assetNumber,String.valueOf(0)};
        sqLiteDatabase.update(DATA_BARCODE,values, KEY_AssetNumber + " = ?" +
                " AND " + KEY_FLAG + " = ?", args);
        sqLiteDatabase.close();
    }

    public List<String> getAllHaventBeenSentData()
    {
        List<String> listDataBarcodes = new ArrayList<>();
        String query = "SELECT " + KEY_AssetNumber + " FROM " + DATA_BARCODE + " WHERE " + KEY_FLAG + " = 0";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            Log.d("query berhasil", "query berhasil");
            do
            {
                listDataBarcodes.add(cursor.getString(0));
                Log.d("KEY_ASSET_NUMBER", listDataBarcodes.get(listDataBarcodes.indexOf(cursor.getString(0))));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        return listDataBarcodes;
    }


    public int getAllHaventBeenSentDataCount()
    {
        String selectQuery = "SELECT COUNT(" + KEY_FLAG + ") FROM " + DATA_BARCODE + " WHERE " + KEY_FLAG + " = 0";

        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.moveToFirst())
        {
            Log.d("count ada", "ada");
            count = cursor.getInt(0);
        }

        Log.d("count", String.valueOf(count));

        cursor.close();
        return count;
    }


    public List<DataBarcode> getAllData()
    {
        List<DataBarcode> barcodes = new ArrayList<DataBarcode>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + DATA_BARCODE  + " WHERE " + KEY_FLAG + " = 1 " +" ORDER BY " +KEY_DateTime;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                DataBarcode data = new DataBarcode();
                data.setAssetNumber(cursor.getString(1));
                data.setDateTime(cursor.getString(2));
                data.setScanType(cursor.getString(3));
                data.setFlag(cursor.getString(4));
                // Adding contact to list
                barcodes.add(data);
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        return barcodes;
    }

    public void dumpingDatabase()
    {
        try
        {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite())
            {
                String currentDBPath = "//data//" + "com.example.lanch.scanner" + "//databases//" + "data_barcode_information.db";
                String backupDBPath = "data_barcode_information.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                Log.d("currentDB", String.valueOf(currentDB));
                Log.d("backupDB", String.valueOf(backupDB));
                if (currentDB.exists())
                {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src,0,src.size());
                    src.close();
                    dst.close();
                    Log.d("current DB exists", "current DB exists");
                }
            }
            else
            {
                Log.d("can't write", "can't write");
            }

        }
        catch (Exception e)
        {

        }

    }

}
