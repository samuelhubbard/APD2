// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.Utility;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class VerifyConnection {

    // member variables
    private static ConnectivityManager mManager;
    private static NetworkInfo mNetInfo;

    public static boolean checkNetwork(ConnectivityManager _manager) {

        // put the connectivity manager into the mManager variable
        mManager = _manager;

        // as long as the manager isn't null
        if (mManager != null) {
            // check what the active network state is
            mNetInfo = mManager.getActiveNetworkInfo();

            // if the net info isn't null and is available
            if (mNetInfo != null && mNetInfo.isAvailable()) {
                // return true to indicate a network connection
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
