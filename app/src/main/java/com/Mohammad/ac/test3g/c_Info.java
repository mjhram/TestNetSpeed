package com.Mohammad.ac.test3g;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohammad.haider on 2/16/2015.
 */
public class c_Info implements Parcelable{
    //private MainActivity theActivity;
    private String serverUri;
    public String deviceId;
    public String deviceId2;
    public String manuf;
    public String brand;
    public String model;
    public String product;
    public String imsi;
    public String imsi2;
    public String phoneNumber;
    public String phoneNumber2;
    public String imei;
    public String netOperator;
    public String netOperator2;
    public String netName;
    public String netName2;
    public int netType;
    public int netType2;
    public String netClass;
    public String netClass2;
    public int phoneType;
    public String mobileState;
    public int cid;
    public int cid_3g;
    public int rnc;
    public int lac;
    public int rssi;//signal Strength
    public String SignalStrengths;
    public double rxRate, txRate;
    public double minRxRate;
    public double maxRxRate;
    public double minTxRate;
    public double maxTxRate;
    public double pingTime;
    public double lat, lon;//Location info
    public int cdmaDbm;
    public int cdmaEcio;
    public String neighboringCells;
    public String tmp;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deviceId);
        dest.writeString(deviceId2);
        dest.writeString(manuf);
        dest.writeString(brand);
        dest.writeString(model);
        dest.writeString(product);
        dest.writeString(imsi);
        dest.writeString(imsi2);
        dest.writeString(phoneNumber);
        dest.writeString(phoneNumber2);
        dest.writeString(imei);
        dest.writeString(netOperator);
        dest.writeString(netOperator2);
        dest.writeString(netName);
        dest.writeString(netName2);
        dest.writeInt(netType);
        dest.writeInt(netType2);
        dest.writeString(netClass);
        dest.writeString(netClass2);
        dest.writeInt(phoneType);
        dest.writeString(mobileState);
        dest.writeInt(cid);
        dest.writeInt(cid_3g);
        dest.writeInt(rnc);
        dest.writeInt(lac);
        dest.writeInt(rssi);
        dest.writeString(SignalStrengths);
        dest.writeDouble(rxRate);
        dest.writeDouble(txRate);
        dest.writeDouble(minRxRate);
        dest.writeDouble(maxRxRate);
        dest.writeDouble(minTxRate);
        dest.writeDouble(maxTxRate);
        dest.writeDouble(pingTime);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeString(this.neighboringCells);
        dest.writeInt(cdmaDbm);
        dest.writeInt(cdmaEcio);
        dest.writeString(tmp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel in) {
        deviceId=in.readString();
        deviceId2=in.readString();
        manuf=in.readString();
        brand=in.readString();
        model=in.readString();
        product=in.readString();
        imsi=in.readString();
        imsi2=in.readString();
        phoneNumber=in.readString();
        phoneNumber2=in.readString();
        imei=in.readString();
        netOperator=in.readString();
        netOperator2=in.readString();
        netName=in.readString();
        netName2=in.readString();
        netType=in.readInt();
        netType2=in.readInt();
        netClass=in.readString();
        netClass2=in.readString();
        phoneType=in.readInt();
        mobileState=in.readString();
        cid=in.readInt();
        cid_3g=in.readInt();
        rnc=in.readInt();
        lac=in.readInt();
        rssi=in.readInt();
        SignalStrengths=in.readString();
        rxRate = in.readDouble();
        txRate = in.readDouble();
        minRxRate=in.readDouble();
        maxRxRate=in.readDouble();
        minTxRate=in.readDouble();
        maxTxRate=in.readDouble();
        pingTime=in.readDouble();
        lat=in.readDouble();
        lon=in.readDouble();
        neighboringCells = in.readString();
        cdmaDbm=in.readInt();
        cdmaEcio = in.readInt();
        tmp = in.readString();
    }

    public c_Info(String uri) {
        //theActivity = activity;
        serverUri = uri;
    }

    public c_Info(Parcel in){
        //theActivity = activity;
        readFromParcel(in);
    }

    public static final Creator<c_Info> CREATOR = new Creator<c_Info>() {

        @Override
        public c_Info createFromParcel(Parcel source) {
            return new c_Info(source);
        }

        @Override
        public c_Info[] newArray(int size) {
            return new c_Info[size];
        }
    };

    void upload(Context cntx){
        new uploadInfo(cntx).execute();
    }
    class uploadInfo extends AsyncTask<Void, Void, Void> {
        Context cntx;

        public uploadInfo(Context c) {
            cntx = c;

        }
        @Override
        protected Void doInBackground(Void... a) {
            String url_dbsite = serverUri + "/addTest.php";
            JSONParser jsonParser = new JSONParser();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("deviceId", deviceId));
            params.add(new BasicNameValuePair("imsi", imsi));
            params.add(new BasicNameValuePair("phoneNumber", phoneNumber));
            params.add(new BasicNameValuePair("imei", imei));
            params.add(new BasicNameValuePair("netOperator", netOperator));
            params.add(new BasicNameValuePair("netName", netName));
            params.add(new BasicNameValuePair("netType", Integer.toString(netType)));
            params.add(new BasicNameValuePair("netClass", netClass));
            params.add(new BasicNameValuePair("phoneType", Integer.toString(phoneType)));
            params.add(new BasicNameValuePair("mobileState", mobileState));
            params.add(new BasicNameValuePair("cid", Integer.toString(cid)));
            params.add(new BasicNameValuePair("cid_3g", Integer.toString(cid_3g)));
            params.add(new BasicNameValuePair("rnc", Integer.toString(rnc)));
            params.add(new BasicNameValuePair("lac", Integer.toString(lac)));
            params.add(new BasicNameValuePair("rssi", Integer.toString(rssi)));
            params.add(new BasicNameValuePair("SignalStrengths",SignalStrengths));
            String tmp = String.format("%.2f", minRxRate);
            params.add(new BasicNameValuePair("minRxRate", tmp));
            params.add(new BasicNameValuePair("maxRxRate", String.format("%.2f", maxRxRate)));
            params.add(new BasicNameValuePair("minTxRate", String.format("%.2f", minTxRate)));
            params.add(new BasicNameValuePair("maxTxRate", String.format("%.2f", maxTxRate)));
            params.add(new BasicNameValuePair("lon", Double.toString(lon)));
            params.add(new BasicNameValuePair("lat", Double.toString(lat)));
            params.add(new BasicNameValuePair("brand", brand));
            params.add(new BasicNameValuePair("manuf", manuf));
            params.add(new BasicNameValuePair("product", product));
            params.add(new BasicNameValuePair("model", model));

            params.add(new BasicNameValuePair("deviceId2", deviceId2));
            params.add(new BasicNameValuePair("imsi2", imsi2));
            params.add(new BasicNameValuePair("phoneNum2", phoneNumber2));
            params.add(new BasicNameValuePair("netOperator2", netOperator2));
            params.add(new BasicNameValuePair("netName2", netName2));
            params.add(new BasicNameValuePair("netType2", Integer.toString(netType2)));
            params.add(new BasicNameValuePair("netClass2", netClass2));

            params.add(new BasicNameValuePair("nei", neighboringCells));
            params.add(new BasicNameValuePair("cdmaDbm", Integer.toString(cdmaDbm)));
            params.add(new BasicNameValuePair("cdmaEcio", Integer.toString(cdmaEcio)));

            jsonParser.makeHttpRequest(url_dbsite, "POST", params);
            return null;
        }
        @Override
        protected void onPostExecute(Void v) {
            Intent resultsIntent=new Intent("com.Mohammad.ac.test3g.DONE");
            resultsIntent.putExtra("DONE", true);
            LocalBroadcastManager localBroadcastManager =LocalBroadcastManager.getInstance(cntx);
            localBroadcastManager.sendBroadcast(resultsIntent);
        }
    }

    public void showInfo(MainActivity theActivity) {
        theActivity.txt_model.setText(manuf.toUpperCase() + "/" + model);
        theActivity.txt_netclass.setText(netClass+" - "+netClass2);
        theActivity.txt_netname.setText(netName+" - "+netName2);
        if(netClass.equals("2G")) {
            theActivity.txt_cellid.setText(""+cid);
            theActivity.txt_rnc.setText("");
        }else {
            theActivity.txt_cellid.setText(String.format("%04d",cid_3g));
            theActivity.txt_rnc.setText(""+rnc);
        }
        theActivity.txt_lac.setText(""+lac);
        theActivity.txt_rssi.setText(""+rssi);
        theActivity.txt_minmaxrx.setText(MainActivity.getRateWithUnit(minRxRate)+", "+MainActivity.getRateWithUnit(maxRxRate));
        theActivity.txt_minmaxtx.setText(MainActivity.getRateWithUnit(minTxRate)+", "+MainActivity.getRateWithUnit(maxTxRate));
        theActivity.txt_latitude.setText(""+lat + ", " + +lon);

        theActivity.txt_neighboring.setText(this.neighboringCells);
        theActivity.txt_cdmaDbm.setText("" + this.cdmaDbm);
        theActivity.txt_cdmaEcio.setText("" + this.cdmaEcio);

    }
}
