package com.example.taxiapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FetchURL {
    private Context mContext;
    private String directionMode = "driving";
    private ExecutorService executorService;
    private Handler handler;

    public FetchURL(Context mContext) {
        this.mContext = mContext;
        this.executorService = Executors.newSingleThreadExecutor();
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void execute(String url, String directionMode) {
        this.directionMode = directionMode;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final String data = downloadUrl(url);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onPostExecute(data);
                        }
                    });
                } catch (IOException e) {
                    Log.e("FetchURL", "Exception downloading URL", e);
                }
            }
        });
    }

    private void onPostExecute(String result) {
        PointsParser parserTask = new PointsParser(mContext, directionMode);
        parserTask.execute(result);
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("mylog", "Exception downloading URL: " + e.toString());
        } finally {
            if (iStream != null) {
                iStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return data;
    }
}
