package br.com.apkdoandroid.sistemadeamarzenamentodelog.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import androidx.core.app.ActivityCompat;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ExclucaoDeLogs {
    private static  String LOG_DIRECTORY = "";
    private static final String LOG_FILE_EXTENSION = ".txt";
    private static final int MAX_LOG_DAYS = 28;

    public static void deleteOldLogs(Context context , String nomePasta) {
        LOG_DIRECTORY = nomePasta;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
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
        File externalStorageDir = Environment.getExternalStorageDirectory();
        File appDir = new File(externalStorageDir, LOG_DIRECTORY);
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
        return lastModified < maxLogDate.getTime();
    }
}