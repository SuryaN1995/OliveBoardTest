package com.example.oliverboardtest.api

import com.example.oliverboardtest.AppConstants
import com.example.oliverboardtest.api.model.Restaurant
import com.example.oliverboardtest.api.model.SearchResponseModel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Surya N on 30/09/19.
 */
interface ApiService {

    @GET(AppConstants.SEARCH_END_POINT)
    fun search(@Query("q") searchText : String?,
               @Query("lat")  lat : Double ?,
               @Query("lon")  lon : Double ?,
               @Query("radius")  radius : Double ?) : Observable<SearchResponseModel>

    @GET(AppConstants.RESTAURANT)
    fun getRestaurantDetails(@Query("res_id") restaurant_id : Int ?) : Observable<Restaurant>

}