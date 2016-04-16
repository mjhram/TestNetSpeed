package com.Mohammad.ac.test3g;

import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mohammad.haider on 2/19/2015.
 */
class uploadFileToURL extends AsyncTask<String, Double, String> {
    //String str_unit;
    speedUnit unit;
    long BeforeTime, initialTime, TotalTxBeforeTest;
    double rate, minTxRate, maxTxRate;
    Context cntx;

    public uploadFileToURL(Context c) {
        cntx = c;
    }

    @Override
    protected void onPreExecute() {
        //isDownloadUpload = true;
    }

    boolean uploadRate2(boolean init)//return true to stop
    {
        if(init == true) {
            unit = speedUnit.bps;
            BeforeTime = System.currentTimeMillis();
            initialTime = System.currentTimeMillis();
            TotalTxBeforeTest = TrafficStats.getTotalTxBytes();
            minTxRate = Double.MAX_VALUE;
            maxTxRate = 0;
            return false;
        }

        long AfterTime = System.currentTimeMillis();
        if(AfterTime - BeforeTime > 500) {
            double rate=0.0;
            long TotalTxAfterTest = TrafficStats.getTotalTxBytes();
            double TimeDifference = AfterTime - BeforeTime;
            double txDiff = TotalTxAfterTest - TotalTxBeforeTest;
            if(txDiff != 0) {
                double txBPS = (txDiff / (TimeDifference/1000.0)); // total tx bytes per second.
                rate = txBPS*8;
            }
            else {
                rate=0.0;
            }
            if(rate < minTxRate ) {
                minTxRate = rate;
            }
            if(rate > maxTxRate ){
                maxTxRate = rate;
            }
            if(AfterTime - initialTime > 25000) {
                return true;
            }
            /*str_unit="";
            if(rate < 1024) {
                unit = speedUnit.bps;
            } else if (rate >= 1024 && rate <1024*1024) {
                rate /=1024.0;
                str_unit="K";
                unit = speedUnit.Kbps;
            }else if (rate >= 1024*1024 && rate <1024*1024*1024) {
                rate /=1024.0*1024.0;
                unit = speedUnit.Mbps;
                str_unit="M";
            }else if (rate >= 1024*1024*1024) {
                rate /= 1024*1024*1024.0;
                unit = speedUnit.Gbps;
                str_unit="G";
            }*/
            publishProgress(rate, minTxRate, maxTxRate);
            BeforeTime = System.currentTimeMillis();
            TotalTxBeforeTest = TrafficStats.getTotalTxBytes();
        }
        return false;
    }

    @Override
    protected String doInBackground(String... f_url) {
        //int count;
        String fileName = "tmp.bin";
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        byte[] buffer;
        int maxBufferSize = 20 * 1024;
        int contentLen = 100*maxBufferSize;
        try {
            String strHeader = "Content-Disposition: form-data; name='ufile';filename='"
                    + fileName + "'" + lineEnd;
            URL url = new URL(f_url[0]);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("ufile", fileName);
            int len = contentLen+strHeader.length()+11;//+Header
            len += 13;//Footer
            conn.setFixedLengthStreamingMode(len);
            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes(strHeader);
            dos.writeBytes(lineEnd);

            buffer = new byte[maxBufferSize];
            int totalBytes2 =0;
            uploadRate2(true);
            do {
                dos.write(buffer, 0, maxBufferSize);
                totalBytes2 += maxBufferSize;
                if(uploadRate2(false)) {
                    //break;
                }
                dos.flush();
            } while (totalBytes2 < contentLen);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            int serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();
            Log.i("uploadFile", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);
            dos.flush();
            dos.close();
            if(minTxRate == Double.MAX_VALUE) {
                minTxRate = 0;
            }
            //mobInfo.upload();
        } catch (MalformedURLException ex) {
            //dialog.dismiss();
            ex.printStackTrace();
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {

            //dialog.dismiss();
            e.printStackTrace();
            Log.e("Upload file to server Exception", "Exception : "
                    + e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Double... progress) {
        //String tmp = String.format("%.2f",progress[0]) + str_unit;
        //txt_rxRateText.setText(tmp);
        Intent resultsIntent=new Intent("com.Mohammad.ac.test3g.U_PROGRESS");
        resultsIntent.putExtra("TxRATE", progress[0]);
        resultsIntent.putExtra("MinTxRATE", progress[1]);
        resultsIntent.putExtra("MaxTxRATE", progress[2]);
        resultsIntent.putExtra("SHOW_INFO", false);
        LocalBroadcastManager localBroadcastManager =LocalBroadcastManager.getInstance(cntx);
        localBroadcastManager.sendBroadcast(resultsIntent);
    }

    /**
     * After completing background task
     * Dismiss the progress dialog
     * **/
    @Override
    protected void onPostExecute(String file_url) {
        //isDownloadUpload = false;
        /*mobInfo.showInfo(thisActivity);
        btnStartTest.setVisibility(View.VISIBLE);
        btnHistory.setVisibility(View.VISIBLE);*/

        Intent resultsIntent=new Intent("com.Mohammad.ac.test3g.U_PROGRESS");
        resultsIntent.putExtra("TxRATE", rate);
        resultsIntent.putExtra("MinTxRATE", minTxRate);
        resultsIntent.putExtra("MaxTxRATE", maxTxRate);
        resultsIntent.putExtra("SHOW_INFO", true);
        resultsIntent.putExtra("UL_DONE", true);
        LocalBroadcastManager localBroadcastManager =LocalBroadcastManager.getInstance(cntx);
        localBroadcastManager.sendBroadcast(resultsIntent);
    }

}
