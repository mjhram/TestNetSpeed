package com.Mohammad.ac.test3g;

import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

class DownloadFileFromURL extends AsyncTask<String, Double, String> {
    //String str_unit;
    double rate, minRxRate, maxRxRate;
    //MainActivity theActivity;
    Context cntx;

    public DownloadFileFromURL(Context c) {
        cntx = c;
    }
    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            URL url = new URL(f_url[0]);
            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file
            //OutputStream output = new FileOutputStream("/sdcard/downloadedfile.jpg");

            byte data[] = new byte[1024];
            speedUnit unit = speedUnit.bps;
            long BeforeTime = System.currentTimeMillis();
            long initialTime = System.currentTimeMillis();
            long TotalTxBeforeTest = TrafficStats.getTotalTxBytes();
            long TotalRxBeforeTest = TrafficStats.getTotalRxBytes();
            minRxRate = Double.MAX_VALUE;
            maxRxRate = 0;
            do {
                if (((count = input.read(data)) == -1)) {
                    break;
                }
                long AfterTime = System.currentTimeMillis();
                if (AfterTime - BeforeTime > 500) {
                    rate = 0.0;
                    long TotalTxAfterTest = TrafficStats.getTotalTxBytes();
                    long TotalRxAfterTest = TrafficStats.getTotalRxBytes();
                    double TimeDifference = AfterTime - BeforeTime;
                    double rxDiff = TotalRxAfterTest - TotalRxBeforeTest;
                    double txDiff = TotalTxAfterTest - TotalTxBeforeTest;
                    //if((rxDiff != 0) && (txDiff != 0))
                    if (rxDiff != 0) {
                        double rxBPS = (rxDiff / (TimeDifference / 1000.0)); // total rx bytes per second.
                        double txBPS = (txDiff / (TimeDifference / 1000.0)); // total tx bytes per second.
                        rate = rxBPS * 8;
                    } else {
                        rate = 0.0;
                    }
                    if (AfterTime - initialTime > 25000) {
                        break;
                    }
                    if (rate < minRxRate) {
                        minRxRate = rate;
                    }
                    if (rate > maxRxRate) {
                        maxRxRate = rate;
                    }
                    /*str_unit = "";
                    //double rate = 8.0*1e9*total/delta;
                    if (rate < 1024) {
                        unit = speedUnit.bps;
                    } else if (rate >= 1024 && rate < 1024 * 1024) {
                        rate /= 1024.0;
                        str_unit = "K";
                        unit = speedUnit.Kbps;
                    } else if (rate >= 1024 * 1024 && rate < 1024 * 1024 * 1024) {
                        rate /= 1024.0 * 1024.0;
                        unit = speedUnit.Mbps;
                        str_unit = "M";
                    } else if (rate >= 1024 * 1024 * 1024) {
                        rate /= 1024 * 1024 * 1024.0;
                        unit = speedUnit.Gbps;
                        str_unit = "G";
                    }*/
                    publishProgress(rate, minRxRate, maxRxRate);
                    BeforeTime = System.currentTimeMillis();
                    TotalTxBeforeTest = TrafficStats.getTotalTxBytes();
                    TotalRxBeforeTest = TrafficStats.getTotalRxBytes();
                }
            } while (true);
            if (minRxRate == Double.MAX_VALUE) {
                minRxRate = 0;
            }
            // flushing output
            //output.flush();

            // closing streams
            //output.close();
            input.close();
            //mobInfo.upload();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }
    /**
     * Updating progress bar
     * */
    @Override
    protected void onProgressUpdate(Double... progress) {
        //String tmp = String.format("%.2f",progress[0]) + str_unit;

        Intent resultsIntent=new Intent("com.Mohammad.ac.test3g.PROGRESS");
        resultsIntent.putExtra("RxRATE", progress[0]);
        resultsIntent.putExtra("MinRxRATE", progress[1]);
        resultsIntent.putExtra("MaxRxRATE", progress[2]);
        resultsIntent.putExtra("SHOW_INFO", false);
        LocalBroadcastManager localBroadcastManager =LocalBroadcastManager.getInstance(cntx);
        localBroadcastManager.sendBroadcast(resultsIntent);

        //theActivity.pRateText.setText(tmp);
    }

    @Override
    protected void onPostExecute(String file_url) {
        //theActivity.mobInfo.showInfo(theActivity);
        Intent resultsIntent=new Intent("com.Mohammad.ac.test3g.PROGRESS");
        resultsIntent.putExtra("RxRATE", rate);
        resultsIntent.putExtra("MinRxRATE", minRxRate);
        resultsIntent.putExtra("MaxRxRATE", maxRxRate);
        resultsIntent.putExtra("SHOW_INFO", true);
        LocalBroadcastManager localBroadcastManager =LocalBroadcastManager.getInstance(cntx);
        localBroadcastManager.sendBroadcast(resultsIntent);
    }

}

