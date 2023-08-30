package org.endlessos.testapp;

import java.io.File;

import android.content.Context;
import android.util.Log;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

public class KolibriUtils {
    private static final String TAG = Constants.TAG;

    public static File getKolibriHome(Context context) {
        return new File(context.getFilesDir(), "kolibri");
    }

    public static void setupKolibri(Context context) {
        final String kolibriHome = getKolibriHome(context).toString();

        Python python = Python.getInstance();
        PyObject mainModule = python.getModule("testapp.main");
        Log.i(TAG, "Setting up Kolibri in " + kolibriHome);
        mainModule.callAttr("setup", kolibriHome);
    }
}
