package com.example.oliverboardtest.ui

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object AppUtils {

    /**
     * Check is permission granted
     */
    fun isPermissionGranted(context: Context?, permission: ArrayList<String>): Boolean {
        if (context == null) {
            return false
        }
        var grant = true
        permission.forEach {
            grant = grant && (context.checkCallingOrSelfPermission(it) == PackageManager.PERMISSION_GRANTED)
        }
        return grant
    }

    fun isInternetConnected(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                    return when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                }
            } else {
                val networkInfo = connectivityManager.activeNetworkInfo
                return networkInfo != null && networkInfo.isConnected
            }
        }
        return false
    }
}