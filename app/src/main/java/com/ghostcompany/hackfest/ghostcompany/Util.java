package com.ghostcompany.hackfest.ghostcompany;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Rabbit on 7/23/2017.
 */

public class Util {

    /*Esse metodo calcula a dist?ncia em K, M ou N : */

    public static double distance(String latitude1, String longitude1,
                            String latitude2, String longitude2, char unit) {

        double lat1 = Double.valueOf(latitude1).doubleValue();
        double lon1 = Double.valueOf(longitude1).doubleValue();
        double lat2 = Double.valueOf(latitude2).doubleValue();
        double lon2 = Double.valueOf(longitude2).doubleValue();
        double dist = 0.0;
        double R = 6372.8; // In kilometers

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
                * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));

        dist = R * c;

        if (unit == 'K') {
            dist = dist * 1.609344;

        } else if (unit == 'N') {
            dist = dist * 0.8684;

        } else if (unit == 'M') {
            dist = dist * 1000.0;
        }
        return (dist);
    }

    public static boolean isNetworkAvaiable(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

}
