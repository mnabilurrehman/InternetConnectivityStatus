package com.draculatech.internetconnectivitystatus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*

 */
public class InternetStatus {

    private Context context;
    private String status = "Calculating";

    public InternetStatus(Context context) {
        this.context = context;
        managerStatus();
    }

    private void managerStatus() {

        final Boolean firstLink;
        final Boolean secondLink;
        final Boolean thirdLink;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            firstLink = isServerReachable("https://www.google.com/");
            secondLink = isServerReachable("https://www.facebook.com/");
            thirdLink = isServerReachable("https://www.youtube.com/");
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if ((firstLink && secondLink) || (firstLink && thirdLink) || (thirdLink && secondLink)) {
                        status = "Internet Working";
                    } else if ((!firstLink && !secondLink) || (!firstLink && !thirdLink) || (!thirdLink && !secondLink)) {
                        status = "Internet Not Working";
                    }
                }
            }, 3000);
        } else {
            status = "Not Connected";
        }
    }

    private boolean isServerReachable(String URL) {
        ConnectivityManager connMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL urlServer = new URL(URL);
                HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
                urlConn.setConnectTimeout(3000); //<- 3Seconds Timeout
                urlConn.connect();
                if (urlConn.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Status --> Meaning
     * Calculating --> Its Calculating Internet Status.
     * Not Connected --> Mobile not connected to network.
     * Internet Working --> Internet is working properly.
     * Internet Not Working --> Connected to network but Internet is not working.
     *
     * @return
     */
    public String getStatus(){
        return status;
    }
}
