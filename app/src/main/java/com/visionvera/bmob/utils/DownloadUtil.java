package com.visionvera.bmob.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.visionvera.bmob.R;
import com.visionvera.bmob.global.App;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Qiao on 2017/2/27.
 */
public class DownloadUtil {
    public static long downloadApk(String apkUrl, final DownloadListener listener) {
        if (TextUtil.isEmpty(apkUrl)) return -1;
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir();
        final DownloadManager downloadManager = (DownloadManager) App.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        final String name = ResUtil.getString(R.string.app_name) + ".apk";
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle(ResUtil.getString(R.string.app_name));
        request.setDescription("正在下载...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/vnd.android.package-archive");
        final long id = downloadManager.enqueue(request);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                LogUtil.d("DownloadUtil", "timer running...");
                DownloadManager.Query query = new DownloadManager.Query();
                Cursor cursor = downloadManager.query(query.setFilterById(id));
                if (cursor != null && cursor.moveToNext()) {
                    final int progress = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    final int total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    final boolean done = progress == total;
                    if (listener != null) {
                        App.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onProgress(progress, total, done);
                            }
                        });
                    }
                    if (done) {
                        cancel();
                    }
                }
            }
        }, 0, 1000);
        return id;
    }

    public interface DownloadListener {
        void onProgress(int progress, int total, boolean done);
    }
}
