package br.com.apkdoandroid.sistemadeamarzenamentodelog.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.Calendar;
import java.util.Date;


public class ExclucaoDeLogs {
    private static  String LOG_DIRECTORY = "";
    private static final String LOG_FILE_EXTENSION = ".txt";
    private static final int MAX_LOG_DAYS = 15; // dias que o log permanece no dispositivo
    private static Context mcontext;
    private static File appDir = null;

    public static void deleteOldLogs(Context context , String nomePasta) {
        LOG_DIRECTORY = nomePasta;
        mcontext = context;
        //Log.d("exclusao","=="+nomePasta);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // para android 11 e superior
            appDir = new File(mcontext.getExternalFilesDir(null), getAppName(mcontext) + "_Log");
            Log.d("exclusao","para android 11 e superior");
        }else{
            File file = Environment.getExternalStorageDirectory();
            appDir = new File(file, getAppName(mcontext).trim() + "_Log");
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            }
        }


        File appDir = getAppDirectory();

        if (appDir == null) {
            return;
        }

        File[] logFiles = appDir.listFiles();
        if (logFiles == null) {

            return;
        }

        for (File logFile : logFiles) {
            if (isOldLog(logFile.lastModified())) {
                logFile.delete();
            }
        }
    }

    private static File getAppDirectory() {


          Log.d("exclusao","2 path : "+(!appDir.exists()));
         Log.d("exclusao","2 path : "+appDir.getPath());
        if (!appDir.exists()) {
            return null;
        }
        return appDir;




    }

    private static boolean isOldLog(long lastModified) {
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, -MAX_LOG_DAYS);
        Date maxLogDate = calendar.getTime();
        Log.d("exclusao",lastModified+"-"+maxLogDate.getTime()+"__"+(lastModified < maxLogDate.getTime()));
        return lastModified < maxLogDate.getTime();
    }

    private static String getAppName(Context context) {
        CharSequence appName = context.getPackageManager().getApplicationLabel(context.getApplicationInfo());
        return (appName != null) ? appName.toString() : "LogApp";
    }
}