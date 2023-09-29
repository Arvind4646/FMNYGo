package com.fmnygo.models;


import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.fmnygo.CallActivity;
import com.fmnygo.MainActivity;

public class InterfaceJava {

    CallActivity callActivity;

    public InterfaceJava(CallActivity callActivity){
        this.callActivity = callActivity;
    }

    @JavascriptInterface
    public void onPeerConnected(){
        callActivity.onPeerConnected();
    }


}
