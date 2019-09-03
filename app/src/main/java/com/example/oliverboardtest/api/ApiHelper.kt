package com.example.oliverboardtest.api

import com.example.oliverboardtest.api.model.Restaurant
import com.example.oliverboardtest.api.model.SearchResponseModel

interface ApiHelper {

    interface SearchResultCallback{
        fun onSuccess(response : SearchResponseModel)
        fun onError(throwable: Throwable?)
    }

    interface RestaurantDetailsCallback {
        fun onSuccess(response : Restaurant)
        fun onError(throwable: Throwable?)
    }

    fun searchRequest(searchText: String?,
                      lat: Double?,
                      lon: Double?,
                      radius: Double?,callback: SearchResultCallback)

    fun restaurantDetails(resId : Int,callback: RestaurantDetailsCallback)

}