package com.Mohammad.ac.test3g;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class downloadFileService extends Service {
    public downloadFileService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String file_url = "http://download.thinkbroadband.com/20MB.zip";
        final String upLoadServerUri = "http://3gtest.net76.net/en/upload.php";

        new DownloadFileFromURL(this).execute(file_url);
        new uploadFileToURL(this).execute(upLoadServerUri);
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


}
