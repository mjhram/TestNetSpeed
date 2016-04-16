package com.Mohammad.ac.test3g;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationServices;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

enum speedUnit {bps, Kbps, Mbps, Gbps};

public class MainActivity extends Activity {
    private Context mAppContext;
    static TelephonyManager        mTelephonyMgr;
    MyPhoneStateListener    MyListener;
    public c_Info mobInfo;
    public TextView txt_netclass;
    public TextView txt_netname;
    public TextView txt_model;
    public TextView txt_cellid;
    public TextView txt_rnc;
    public TextView txt_lac;
    public TextView txt_rssi;
    public TextView txt_minmaxrx;
    public TextView txt_latitude, txt_longitude;
    int serverResponseCode = 0;
    //String upLoadServerUri = "http://3gtest.net76.net/en/upload.php";
    static final String MOB_INFO = "mobInfo";
    MainActivity thisActivity;

    // button to show progress dialog
    Button btnStartTest;
    Button btnHistory;
    //Button btnUpdate;
    //Button btnUpload;

    // Progress Dialog
    //private ProgressDialog pDialog;
    public TextView txt_rxRateText;
    public TextView txt_txRateText;
    public TextView txt_minmaxtx;
    //public GaugeView mGaugeView2;

    // File url to download
    private static String file_url = "http://download.thinkbroadband.com/20MB.zip";
    //http://api.androidhive.info/progressdialog/hive.jpg";
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(MOB_INFO, mobInfo);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = this;
        setContentView(R.layout.activity_main);
        myUtility.OrientationUtils.lockOrientationPortrait(this);

        txt_rxRateText = (TextView) findViewById(R.id.rateText_id);
        btnStartTest = (Button) findViewById(R.id.btnStartTest);
        btnHistory = (Button) findViewById(R.id.id_BtnHistory);
        //btnUpdate = (Button) findViewById(R.id.btnUpdate);
        //btnUpload = (Button) findViewById(R.id.btnUpload);
        txt_model = (TextView) findViewById(R.id.textViewModel);
        txt_netclass = (TextView) findViewById(R.id.id_netclass);
        txt_netname = (TextView) findViewById(R.id.id_netname);
        txt_cellid = (TextView) findViewById(R.id.id_cellid);
        txt_rnc = (TextView) findViewById(R.id.id_rnc);
        txt_lac = (TextView) findViewById(R.id.id_lac);
        txt_rssi = (TextView) findViewById(R.id.id_rssi);
        txt_minmaxrx = (TextView) findViewById(R.id.id_minmaxrate);
        txt_latitude = (TextView) findViewById(R.id.id_lat);
        txt_longitude = (TextView) findViewById(R.id.id_lon);
        txt_txRateText = (TextView) findViewById(R.id.txRateText_id);
        txt_minmaxtx = (TextView) findViewById(R.id.id_minmaxTxrate);
        //mGaugeView2 = (GaugeView) findViewById(R.id.gauge_view2);

        btnHistory.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  if(mobInfo.deviceId == null) {
                      collectInitInfo();
                      mobInfo.showInfo(thisActivity);
                  }
                  String url = "http://3gtest.net76.net/en/index.php?dev="+mobInfo.deviceId + "&ver=Feb12";
                  Intent i = new Intent(Intent.ACTION_VIEW);
                  i.setData(Uri.parse(url));
                  startActivity(i);
              }
        });

        btnStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectInitInfo();
                mobInfo.showInfo(thisActivity);
                // starting new Async Task
                btnStartTest.setVisibility(View.GONE);
                btnHistory.setVisibility(View.GONE);
                Intent i= new Intent(thisActivity, downloadFileService.class);
                thisActivity.startService(i);
                //new DownloadFileFromURL(thisActivity).execute(file_url);
                //new uploadFileToURL().execute(upLoadServerUri);
            }
        });

        /*btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectInitInfo();
                mobInfo.showInfo(thisActivity);
            }
        });*/

        //btnUpdate.setVisibility(View.GONE);
        if (savedInstanceState != null) {
            mobInfo = savedInstanceState.getParcelable(MOB_INFO);
            mobInfo.showInfo(thisActivity);
        } else if(mobInfo == null) {
            mobInfo = new c_Info();
        }

        mAppContext = getApplicationContext();
        mTelephonyMgr = ( TelephonyManager )getSystemService(Context.TELEPHONY_SERVICE);

        /* Update the listener, and start it */
        MyListener   = new MyPhoneStateListener();
        mTelephonyMgr.listen(MyListener ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        collectInitInfo();
        mobInfo.showInfo(thisActivity);
    }

    private void isDualSimOrNot(){
        myDualSim myDualSimInfo = myDualSim.getInstance(this);

        String imeiSIM1 = myDualSimInfo.getImeiSIM1();
        String imeiSIM2 = myDualSimInfo.getImeiSIM2();

        boolean isSIM1Ready = myDualSimInfo.isSIM1Ready();
        boolean isSIM2Ready = myDualSimInfo.isSIM2Ready();

        boolean isDualSIM = myDualSimInfo.isDualSIM();
        Log.i("Dual = "," IME1 : " + imeiSIM1 + "\n" +
                " IME2 : " + imeiSIM2 + "\n" +
                " IS DUAL SIM : " + isDualSIM + "\n" +
                " IS SIM1 READY : " + isSIM1Ready + "\n" +
                " IS SIM2 READY : " + isSIM2Ready + "\n");
    }
    /**
     * Showing Dialog
     * */
    /*@Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    private String getMy10DigitPhoneNumber(){
        //TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String s = mTelephonyMgr.getLine1Number();
        return s != null && s.length() > 2 ? s.substring(2) : null;
    }
    String isMobileEnabled(){
        boolean mob_avail = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) mAppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        mob_avail = info.isAvailable();
        //Log.d("msg", "Mobile Connectivity:"+ info.getState()); // NetworkInfo.State.CONNECTED
        /*try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mob_avail = (Boolean)method.invoke(cm);

        } catch (Exception e) {

        }*/
        NetworkInfo.State state;
        //if(mob_avail)
        {
            state = info.getState();
        }
        return state.toString();
    }
    public String getNetworkClass(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            default:
                return "Unknown";
        }
    }

    /*public class c_Info{
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
        public double minRxRate;
        public double maxRxRate;
        public double minTxRate;
        public double maxTxRate;
        public double pingTime;
        public double lat, lon;//Location info
        public int ber;
        public int ecio;

        void upload(){
            String url_dbsite = "http://3gtest.net76.net/addTest.php";
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

            jsonParser.makeHttpRequest(url_dbsite, "POST", params);
        }
        void showInfo() {
            txt_model.setText(manuf.toUpperCase()+"/"+model);
            txt_netclass.setText(netClass+" - "+netClass2);
            txt_netname.setText(netName+" - "+netName2);
            if(netClass.equals("2G")) {
                txt_cellid.setText(""+cid);
                txt_rnc.setText("");
            }else {
                txt_cellid.setText(String.format("%04d",cid_3g));
                txt_rnc.setText(""+rnc);
            }
            txt_lac.setText(""+lac);
            txt_rssi.setText(""+rssi);
            txt_minmaxrx.setText(getRateWithUnit(minRxRate)+", "+getRateWithUnit(maxRxRate));
            txt_minmaxtx.setText(getRateWithUnit(minTxRate)+", "+getRateWithUnit(maxTxRate));
        }
    };*/

    private void collectInitInfo() {
        //for Dual SIM
        myDualSim myDualSimInfo = myDualSim.getInstance(this);
        if(myDualSimInfo.isDualSIM()) {
            mobInfo.deviceId=myDualSimInfo.getImeiSIM1();
            mobInfo.imsi=myDualSimInfo.imsi1;
            mobInfo.phoneNumber = myDualSimInfo.phoneNumber1;
            mobInfo.netType = myDualSimInfo.netType1;
            mobInfo.netClass = getNetworkClass(mobInfo.netType);
            mobInfo.netOperator = myDualSimInfo.netOperator1;
            mobInfo.netName = myDualSimInfo.netName1;

            mobInfo.deviceId2=myDualSimInfo.getImeiSIM2();
            mobInfo.imsi2=myDualSimInfo.imsi2;
            mobInfo.phoneNumber2 = myDualSimInfo.phoneNumber2;
            mobInfo.netType2 = myDualSimInfo.netType2;
            mobInfo.netClass2 = getNetworkClass(mobInfo.netType2);
            mobInfo.netOperator2 = myDualSimInfo.netOperator2;
            mobInfo.netName2 = myDualSimInfo.netName2;
        } else {
            mobInfo.deviceId=mTelephonyMgr.getDeviceId();
            mobInfo.imsi = mTelephonyMgr.getSubscriberId();
            mobInfo.phoneNumber = mTelephonyMgr.getLine1Number();
            mobInfo.netType = mTelephonyMgr.getNetworkType();
            mobInfo.netClass = getNetworkClass(mobInfo.netType);
            mobInfo.netOperator = mTelephonyMgr.getSimOperator();
            mobInfo.netName = mTelephonyMgr.getNetworkOperatorName();
        }
        //TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);


        //mobInfo.imei = android.os.SystemProperties.get(android.telephony.TelephonyProperties.PROPERTY_IMSI);
        //TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        mobInfo.mobileState = isMobileEnabled();

        mobInfo.phoneType = mTelephonyMgr.getPhoneType();

        mobInfo.brand = Build.BRAND;
        mobInfo.manuf = Build.MANUFACTURER;
        mobInfo.product = Build.PRODUCT;
        mobInfo.model = Build.MODEL;

        GsmCellLocation cellLocation = (GsmCellLocation) mTelephonyMgr.getCellLocation();
        if(cellLocation != null) {
            mobInfo.cid = cellLocation.getCid();
            mobInfo.cid_3g = mobInfo.cid & 0xffff;
            mobInfo.rnc = (mobInfo.cid & 0xffff0000) >> 16;
            mobInfo.lac = cellLocation.getLac();
        }
        mTelephonyMgr.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        //Geo Locatoion:
        Location loc = getLastKnownLocation();//getLocation();
        if(loc != null) {
            mobInfo.lon = loc.getLongitude();
            mobInfo.lat = loc.getLatitude();
        }
    }
    /* Called when the application is minimized */
    @Override
    protected void onPause()
    {
        super.onPause();
        //mTelephonyMgr.listen(MyListener, PhoneStateListener.LISTEN_NONE);
    }

    /* Called when the application resumes */
    @Override
    protected void onResume()
    {
        super.onResume();
        progressReceiver receiver = new progressReceiver();
        IntentFilter filter= new IntentFilter("com.Mohammad.ac.test3g.PROGRESS");
        LocalBroadcastManager.getInstance(this).registerReceiver (receiver, filter);

        filter= new IntentFilter("com.Mohammad.ac.test3g.U_PROGRESS");
        LocalBroadcastManager.getInstance(this).registerReceiver (receiver, filter);

        filter= new IntentFilter("com.Mohammad.ac.test3g.DONE");
        LocalBroadcastManager.getInstance(this).registerReceiver (receiver, filter);
        //Log.d("dev", mobInfo.deviceId);
        System.out.print("dev"+mobInfo.deviceId);
        //mTelephonyMgr.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }
    /* —————————– */
    /* Start the PhoneState listener */
    /* —————————– */
    private class MyPhoneStateListener extends PhoneStateListener
    {
        /* Get the Signal strength from the provider, each tiome there is an update */
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength)
        {
            super.onSignalStrengthsChanged(signalStrength);

            /*{

                Class tClass = signalStrength.getClass();
                Method[] methods = tClass.getMethods();
                for (int i = 0; i < methods.length; i++) {
                    System.out.println("public method: " + methods[i]);
                }
            }*/
            mobInfo.SignalStrengths = signalStrength.toString();
            int oldRssi = mobInfo.rssi;
            if (signalStrength.isGsm()) {
                if (signalStrength.getGsmSignalStrength() != 99)
                    mobInfo.rssi = signalStrength.getGsmSignalStrength() * 2 - 113;
                else
                    mobInfo.rssi = signalStrength.getGsmSignalStrength();
            } else {
                mobInfo.rssi = signalStrength.getCdmaDbm();
            }
            if(mobInfo.rssi != oldRssi) {
                txt_rssi.setText("" + mobInfo.rssi);
            }
            //mTelephonyMgr.listen(MyListener, PhoneStateListener.LISTEN_NONE);
        }

    };/* End of private Class */


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static String getRateWithUnit(Double value) {
        String str_unit="";
        speedUnit theunit = speedUnit.bps;

        //double rate = 8.0*1e9*total/delta;
        if(value < 1024) {
            theunit = speedUnit.bps;
        } else if (value >= 1024 && value <1024*1024) {
            value /=1024;
            str_unit="K";
            theunit = speedUnit.Kbps;
        }else if (value >= 1024*1024 && value <1024*1024*1024) {
            value /=1024*1024;
            theunit = speedUnit.Mbps;
            str_unit="M";
        }else if (value >= 1024*1024*1024) {
            value /= 1024*1024*1024;
            theunit = speedUnit.Gbps;
            str_unit="G";
        }
        return(String.format("%.2f%s", value, str_unit));
    }

  /*  class DownloadFileFromURL extends AsyncTask<String, Double, String> {
        String str_unit;
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
                mobInfo.minRxRate = Double.MAX_VALUE;
                mobInfo.maxRxRate = 0;
                do {
                    if(((count = input.read(data)) == -1)) {
                        break;
                    }
                    long AfterTime = System.currentTimeMillis();
                    if(AfterTime - BeforeTime > 500) {
                        double rate=0.0;
                        long TotalTxAfterTest = TrafficStats.getTotalTxBytes();
                        long TotalRxAfterTest = TrafficStats.getTotalRxBytes();
                        double TimeDifference = AfterTime - BeforeTime;
                        double rxDiff = TotalRxAfterTest - TotalRxBeforeTest;
                        double txDiff = TotalTxAfterTest - TotalTxBeforeTest;
                        //if((rxDiff != 0) && (txDiff != 0))
                        if(rxDiff != 0) {
                            double rxBPS = (rxDiff / (TimeDifference/1000.0)); // total rx bytes per second.
                            double txBPS = (txDiff / (TimeDifference/1000.0)); // total tx bytes per second.
                            rate = rxBPS*8;
                        }
                        else {
                            rate=0.0;
                        }
                        if(AfterTime - initialTime > 25000) {
                            break;
                        }
                        if(rate < mobInfo.minRxRate ) {
                            mobInfo.minRxRate = rate;
                        }
                        if(rate > mobInfo.maxRxRate ){
                            mobInfo.maxRxRate = rate;
                        }
                        //getRateWithUnit(rate);
                        str_unit="";
                        //double rate = 8.0*1e9*total/delta;
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
                        }
                        publishProgress(rate);
                        BeforeTime = System.currentTimeMillis();
                        TotalTxBeforeTest = TrafficStats.getTotalTxBytes();
                        TotalRxBeforeTest = TrafficStats.getTotalRxBytes();
                    }
                } while (true);
                if(mobInfo.minRxRate == Double.MAX_VALUE) {
                    mobInfo.minRxRate = 0;
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


         @Override
         protected void onProgressUpdate(Double... progress) {
            // setting progress percentage
            //pDialog.setProgress(Integer.parseInt(progress[0]));
            String tmp = String.format("%.2f",progress[0]) + str_unit;
             //System.out.println(tmp);
             pRateText.setText(tmp);
             //mGaugeView2.setUnit(str_unit);
             //mGaugeView2.setTargetValue(progress[0].intValue());
        }


        @Override
        protected void onPostExecute(String file_url) {
            //isDownloadUpload = false;
            mobInfo.showInfo(thisActivity);
        }

    }

    class uploadFileToURL extends AsyncTask<String, Double, String> {
        String str_unit;
        speedUnit unit;
        long BeforeTime, initialTime, TotalTxBeforeTest;

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
                mobInfo.minTxRate = Double.MAX_VALUE;
                mobInfo.maxTxRate = 0;
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
                if(rate < mobInfo.minTxRate ) {
                    mobInfo.minTxRate = rate;
                }
                if(rate > mobInfo.maxTxRate ){
                    mobInfo.maxTxRate = rate;
                }
                if(AfterTime - initialTime > 25000) {
                    return true;
                }
                str_unit="";
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
                }
                publishProgress(rate);
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
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();
                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);
                dos.flush();
                dos.close();
                if(mobInfo.minTxRate == Double.MAX_VALUE) {
                    mobInfo.minTxRate = 0;
                }
                mobInfo.upload();
            } catch (MalformedURLException ex) {

                //dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        // messageText.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(MainActivity.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                //dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        //messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(MainActivity.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server Exception", "Exception : "
                        + e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Double... progress) {
            String tmp = String.format("%.2f",progress[0]) + str_unit;
            txt_rxRateText.setText(tmp);
        }

        @Override
        protected void onPostExecute(String file_url) {
            //isDownloadUpload = false;
            mobInfo.showInfo(thisActivity);
            btnStartTest.setVisibility(View.VISIBLE);
            btnHistory.setVisibility(View.VISIBLE);
            //btnUpdate.setVisibility(View.VISIBLE);
            //btnUpload.setVisibility(View.VISIBLE);
        }

    }*/


    public Location getLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocationGPS != null) {
                return lastKnownLocationGPS;
            } else {
                Location loc =  locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                //System.out.println("1::"+loc);
                //System.out.println("2::"+loc.getLatitude());
                return loc;
            }
        } else {
            return null;
        }
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    public void onMainClick(View v) {
        collectInitInfo();
        mobInfo.showInfo(thisActivity);
    }

    public void onTestClick(View v) {
        Intent i = new Intent(MainActivity.this, downloadFileService.class);
        MainActivity.this.startService(i);
    }
    public void onStopClick(View v) {
        Intent i = new Intent(MainActivity.this, downloadFileService.class);
        MainActivity.this.stopService(i);
    }

    private class progressReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //mobInfo.minRxRate = intent.getDoubleExtra("MIN_RATE",0);
            //mobInfo.maxRxRate = intent.getDoubleExtra("MAX_RATE",0);
            switch(intent.getAction()) {
                case "com.Mohammad.ac.test3g.PROGRESS":
                    mobInfo.rxRate = intent.getDoubleExtra("RxRATE",0);
                    mobInfo.minRxRate = intent.getDoubleExtra("MinRxRATE",0);
                    mobInfo.maxRxRate = intent.getDoubleExtra("MaxRxRATE",0);
                    txt_rxRateText.setText(getRateWithUnit(mobInfo.rxRate));
                    boolean showInf = intent.getBooleanExtra("SHOW_INFO",false);
                    if(showInf) {
                        mobInfo.showInfo(thisActivity);
                    }
                    break;
                case "com.Mohammad.ac.test3g.U_PROGRESS":
                    mobInfo.txRate = intent.getDoubleExtra("TxRATE",0);
                    mobInfo.minTxRate = intent.getDoubleExtra("MinTxRATE",0);
                    mobInfo.maxTxRate = intent.getDoubleExtra("MaxTxRATE",0);
                    txt_txRateText.setText(getRateWithUnit(mobInfo.txRate));
                    showInf = intent.getBooleanExtra("SHOW_INFO",false);
                    if(showInf) {
                        mobInfo.showInfo(thisActivity);
                    }
                    boolean uploadDone = intent.getBooleanExtra("UL_DONE",false);
                    if(uploadDone) {
                        mobInfo.upload(thisActivity);
                    }
                    break;
                case "com.Mohammad.ac.test3g.DONE":
                     uploadDone = intent.getBooleanExtra("DONE",false);
                    if(uploadDone) {
                        btnStartTest.setVisibility(View.VISIBLE);
                        btnHistory.setVisibility(View.VISIBLE);
                    }
                    break;
            }

        }

    }
}
