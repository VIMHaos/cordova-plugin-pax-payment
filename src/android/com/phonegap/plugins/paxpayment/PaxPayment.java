package com.phonegap.plugins.paxpayment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PaxPayment extends CordovaPlugin {
    public static final int REQUEST_CODE = 0;
    
    private static final String RESULT = "result";
    private static final String CANCELLED = "cancelled";
    
    public static final String LOG_TAG = "PaxPayment";

    private String emulatorName = "sk.kompakts.emulator";
    public boolean emulatorState = false;
    
    private JSONArray requestArgs;
    private CallbackContext callbackContext;
	
    /**
     * Constructor.
     */
    public PaxPayment() {}
    
    /**
     * Executes the request.
     *
     * This method is called from the WebView thread. To do a non-trivial amount of work, use:
     *     cordova.getThreadPool().execute(runnable);
     *
     * To run on the UI thread, use:
     *     cordova.getActivity().runOnUiThread(runnable);
     *
     * @param action          The action to execute.
     * @param args            The exec() arguments.
     * @param callbackContext The callback context used when calling back into JavaScript.
     * @return                Whether the action was valid.
     *
     * @sa https://cordova.apache.org/docs/en/latest/guide/platforms/android/plugin.html
     */

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        this.requestArgs = args;

        if (action.equals("request")) {
            JSONObject obj = args.optJSONObject(0);
            request(obj);
            return true;
        } else if (action.equals("register")) {
            String name = args.getString(0);
            register(name);
            return true;
        }
        return false;
    }

    /**
     * Register Kompakts emulator by Name.
     * @param name
     * @return 
     */
    public void register(String name) {
        this.emulatorState = false;
        
        if("" != name) this.emulatorName = name;
        
        PackageManager pm = cordova.getActivity().getPackageManager();
        boolean service_installed = false;
        try {
            PackageInfo info = pm.getPackageInfo(this.emulatorName, PackageManager.GET_ACTIVITIES);
            service_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            service_installed = false;
        }
        
        if(service_installed){
            this.emulatorState = true;
            
            Log.d(LOG_TAG, "Kompakts sevice " + this.emulatorName + " is registered");
            this.callbackContext.success("Kompakts sevice \"" + this.emulatorName + "\" is registered");
        } else {
            Log.d(LOG_TAG, "Kompakts sevice " + this.emulatorName + " Not Found");
            this.callbackContext.error("Kompakts sevice \"" + this.emulatorName + "\" Not Found");
        }
    }
    
    /**
     * Starts an intent to payment with Kompakts emulator.
     * 
     * @param request An JSONObject, with request options.
    */
    public void request(JSONObject request) throws JSONException {
        if (this.emulatorState) {
            final CordovaPlugin that = this;

            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        Intent intentPayment = new Intent();
                        intentPayment.setComponent(new ComponentName(that.emulatorName, that.emulatorName + ".MainActivity"));

                        // add config as intent extras
                        String requestInJsonFormat = request.toString();
                        intentPayment.putExtra("POS_EMULATOR_EXTRA", requestInJsonFormat);

                        that.cordova.startActivityForResult(that, intentPayment, REQUEST_CODE);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Unexpected error: "+ e.getMessage());
                    }
                }
            });
        } else {
            Log.e(LOG_TAG, "Kompakts emulator is not registered");
            this.callbackContext.error("Kompakts emulator is not registered");
        }
    }
	
    /**
     * Called when the Kompakts emulator Payment intent completes.
     *
     * @param requestCode The request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param intent      An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
    */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE && this.callbackContext != null) {
            if (resultCode == Activity.RESULT_OK) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put(RESULT, new JSONObject(intent.getStringExtra("POS_EMULATOR_RESULT")));
                    obj.put(CANCELLED, false);
                } catch (JSONException e) {
                    Log.d(LOG_TAG, "This should never happen");
                }

                this.callbackContext.success(obj);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put(RESULT, "");
                    obj.put(CANCELLED, true);
                } catch (JSONException e) {
                    Log.d(LOG_TAG, "This should never happen");
                }

                this.callbackContext.success(obj);
            } else {
                Log.e(LOG_TAG, "Unexpected error");
                this.callbackContext.error("Unexpected error");
            }
        }
    }
    
    /**
     * This plugin launches an external Activity when the payment is opened, so we
     * need to implement the save/restore API in case the Activity gets killed
     * by the OS while it's in the background.
     */
    public void onRestoreStateForActivityResult(Bundle state, CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }
}
