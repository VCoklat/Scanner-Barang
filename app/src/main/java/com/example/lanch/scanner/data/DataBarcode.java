package com.example.lanch.scanner.data;

/**
 * Created by mac on 8/1/16.
 */
public class DataBarcode
{
    String assetNumber;
    String dateTime;
    String scanType;
    String flag; // 0 belum terkirim  1 terkirim


    public DataBarcode(String assetNumber, String dayDateTime, String scanType,String flag)
    {
        this.assetNumber = assetNumber;
        this.dateTime = dayDateTime;
        this.scanType = scanType;
        this.flag = flag;
    }

    public DataBarcode()
    {

    }

    public String getAssetNumber()
    {
        return assetNumber;
    }

    public void setAssetNumber(String assetNumber)
    {
        this.assetNumber = assetNumber;
    }

    public String getDateTime()
    {
        return dateTime;
    }

    public void setDateTime(String dateTime)
    {
        this.dateTime = dateTime;
    }

    public String getScanType()
    {
        return scanType;
    }

    public void setScanType(String scanType)
    {
        this.scanType = scanType;
    }

    public String getFlag()
    {
        return flag;
    }

    public void setFlag(String flag)
    {
        this.flag = flag;
    }
}