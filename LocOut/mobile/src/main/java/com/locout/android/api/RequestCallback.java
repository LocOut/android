package com.locout.android.api;

public interface RequestCallback {

    public abstract void onRequestSuccess(int requestId, String response);
    public abstract void onRequestFailed(int requestId, String response);

}
