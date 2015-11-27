package com.locout.android.api;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class RequestHelper {

    public static final String TAG = RequestHelper.class.getSimpleName();

    public static final int REQUEST_GET_USER = 1;
    public static final int REQUEST_SET_TRUST_LEVEL = 2;
    public static final int REQUEST_ADD_DEVICE = 3;

    public static String getRequest(String url) throws Exception {
        boolean success = false;
        String response = null;

        try {
            URL requestUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            try {
                int status = urlConnection.getResponseCode();
                InputStream in;
                if(status >= HttpURLConnection.HTTP_BAD_REQUEST) {
                    in = new BufferedInputStream(urlConnection.getErrorStream());
                } else {
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    success = true;
                }

                response = readStreamToString(in);
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!success) {
            throw new Exception(response);
        } else {
            return response;
        }
    }

    public static String postRequest(String url, String data) throws Exception {
        boolean success = false;
        String response = null;

        URL requestUrl = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) requestUrl.openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json");

        urlConnection.setDoOutput(true);
        urlConnection.setChunkedStreamingMode(0);

        try {
            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            out.write(convertStringToByteArray(data));
            out.close();

            int status = urlConnection.getResponseCode();

            InputStream in;
            if(status >= HttpURLConnection.HTTP_BAD_REQUEST) {
                in = new BufferedInputStream(urlConnection.getErrorStream());
            } else {
                in = new BufferedInputStream(urlConnection.getInputStream());
                success = true;
            }

            response = readStreamToString(in);
        } finally {
            urlConnection.disconnect();
        }

        if (!success) {
            throw new Exception(response);
        } else {
            return response;
        }
    }

    public static void getUserJson(final int requestId, final long userId, final RequestCallback callback) {
        Log.d(TAG, "Requesting user: " + userId);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String url = EndpointHelper.ENDPOINT_GET_USER + userId;
                    String response = getRequest(url);
                    Log.d(TAG, "Request response: " + response);
                    if (callback != null) {
                        callback.onRequestSuccess(requestId, response);
                    }
                } catch (Exception ex) {
                    Log.w(TAG, "Request failed: " + ex.getMessage());
                    if (callback != null) {
                        callback.onRequestFailed(requestId, ex.getMessage());
                    }
                }
            }
        };
        new Thread(runnable).start();
    }

    public static void setTrustLevel(final int requestId, final long deviceId, final float trustLevel, final RequestCallback callback) {
        Log.d(TAG, "Setting trust level for device: " + deviceId + " to " + trustLevel);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String url = EndpointHelper.ENDPOINT_SET_TRUST_LEVEL + deviceId + "?trustLevel=" + trustLevel;
                    String response = getRequest(url);
                    Log.d(TAG, "Request response: " + response);
                    if (callback != null) {
                        callback.onRequestSuccess(requestId, response);
                    }
                } catch (Exception ex) {
                    Log.w(TAG, "Request failed: " + ex.getMessage());
                    if (callback != null) {
                        callback.onRequestFailed(requestId, ex.getMessage());
                    }
                }
            }
        };
        new Thread(runnable).start();
    }

    public static void addDevice(final int requestId, final long userId, final String name, final double latitude, final double longitude, final RequestCallback callback) {
        Log.d(TAG, "Adding new device");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String url = EndpointHelper.ENDPOINT_ADD_DEVICE + userId + "?lat=" + latitude + "&long=" + longitude + "&name=" + name;
                    String response = getRequest(url);
                    Log.d(TAG, "Request response: " + response);
                    if (callback != null) {
                        callback.onRequestSuccess(requestId, response);
                    }
                } catch (Exception ex) {
                    Log.w(TAG, "Request failed: " + ex.getMessage());
                    if (callback != null) {
                        callback.onRequestFailed(requestId, ex.getMessage());
                    }
                }
            }
        };
        new Thread(runnable).start();
    }

    public static byte[] convertStringToByteArray(String data) {
        return data.getBytes(Charset.forName("UTF-8"));
    }

    public static String readStreamToString(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder out = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
            out.append(newLine);
        }
        return out.toString();
    }


}
