package org.endlessos.testapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

public class KolibriService extends Service {
    private static final String TAG = Constants.TAG;

    private final IBinder binder = new KolibriBinder();
    private Python python;
    private PyObject serverBus;
    private String serverUrl;

    public class KolibriBinder extends Binder {
        KolibriService getService() {
            return KolibriService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        python = Python.getInstance();

        KolibriUtils.setupKolibri(this);

        PyObject serverModule = python.getModule("testapp.server");
        serverBus = serverModule.callAttr("AppProcessBus", this);

        Log.i(TAG, "Starting Kolibri server");
        serverBus.callAttr("start");
        serverUrl = serverBus.callAttr("get_url").toString();
        Log.d(TAG, "Server started on " + serverUrl);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Stopping Kolibri server");
        serverBus.callAttr("stop");
        Log.d(TAG, "Server stopped");
    }

    public String getServerUrl() {
        return serverUrl;
    }
}
