package com.example.androiddatadatabinding.Activity;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;


public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    private Context context;
    public FingerprintHandler(Context context){
        this.context= context;
    }
    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){
        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal,0 ,this, null);

    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {

        this.update("There was Auth Error"+errString, false);
    }

    @Override
    public void onAuthenticationFailed() {
        this.update("Auth Failed", false);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        this.update("Error "+helpString, false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("Auth is Sucses you can use App", true);
    }

    private void update(String s, boolean b) {
        System.out.println("SUKES"+s);
    }
}
