package org.endlessos.testapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private static final String TAG = Constants.TAG;

    private WebView view;
    private KolibriService kolibriService;
    private boolean workerStarted;
    private String serverUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "Creating activity");

        view = new WebView(this);
        WebSettings viewSettings = view.getSettings();
        viewSettings.setJavaScriptEnabled(true);
        viewSettings.setDomStorageEnabled(true);
        viewSettings.setAllowFileAccessFromFileURLs(true);
        viewSettings.setAllowUniversalAccessFromFileURLs(true);
        viewSettings.setMediaPlaybackRequiresUserGesture(false);
        view.setWebViewClient(new WebViewClient() {
             @Override
             public boolean shouldOverrideUrlLoading (WebView view,
                                                      WebResourceRequest request) {
                 return false;
             }
        });
        view.loadUrl("file:///android_asset/welcomeScreen/index.html");
        setContentView(view);

        startKolibri();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (kolibriService != null) {
            startWorker();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopWorker();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopKolibri();
    }

    private void startKolibri() {
        Intent intent = new Intent(this, KolibriService.class);
        Log.i(TAG, "Binding Kolibri service");
        if (!bindService(intent, kolibriConnection, Context.BIND_AUTO_CREATE)) {
            Log.e(TAG, "Could not bind to Kolibri service");
        }
    }

    private void stopKolibri() {
        Log.i(TAG, "Unbinding Kolibri service");
        unbindService(kolibriConnection);
    }

    private void startWorker() {
        Intent intent = new Intent(this, WorkerService.class);
        Log.i(TAG, "Binding Worker service");
        if (!bindService(intent, workerConnection, Context.BIND_AUTO_CREATE)) {
            Log.e(TAG, "Could not bind to Worker service");
        }
    }

    private void stopWorker() {
        Log.i(TAG, "Unbinding Worker service");
        unbindService(workerConnection);
    }

    private ServiceConnection kolibriConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder ibinder) {
            Log.d(TAG, "Kolibri service connected");
            KolibriService.KolibriBinder binder = (KolibriService.KolibriBinder) ibinder;
            kolibriService = binder.getService();
            serverUrl = kolibriService.getServerUrl();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "Loading URL " + serverUrl);
                    view.loadUrl(serverUrl);
                }
            });

            if (!workerStarted) {
                startWorker();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Kolibri service disconnected");
            serverUrl = null;
            kolibriService = null;
        }
    };

    private ServiceConnection workerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder ibinder) {
            Log.d(TAG, "Worker service connected");
            workerStarted = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Worker service disconnected");
            workerStarted = false;
        }
    };
}
