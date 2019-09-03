package com.example.oliverboardtest.ui.helperClass

import android.location.Location

interface LocationResultCallback {
    fun onLocationResult(location : Location, callerId: Int)
}

interface RecyclerViewClick {
    fun onItemClickPosition(position : Int)
}