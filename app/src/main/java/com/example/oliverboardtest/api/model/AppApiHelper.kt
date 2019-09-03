package com.example.oliverboardtest.api.model

import android.content.Context
import com.example.oliverboardtest.api.ApiHelper
import com.example.oliverboardtest.api.ApiManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


open class AppApiHelper(private val context: Context) : ApiHelper {

    private var compositeDisposable: CompositeDisposable? = null
    private var searchDisposable: Disposable? = null

    init {
        compositeDisposable = CompositeDisposable()
    }

    override fun searchRequest(searchText: String?, lat: Double?, lon: Double?, radius: Double?,callback: ApiHelper.SearchResultCallback) {
        searchDisposable?.dispose()
        searchDisposable = ApiManager.handleSearch(searchText, lat, lon, radius,context)?.subscribe({
            callback.onSuccess(it)
        }, {
            callback.onError(it)
        })
        searchDisposable?.let { compositeDisposable?.add(it) }
    }

    override fun restaurantDetails(resId: Int, callback: ApiHelper.RestaurantDetailsCallback) {
        val disposable = ApiManager.restaurantDetails(resId, context)?.subscribe({
            callback.onSuccess(it)
        },{
            callback.onError(it)
        })
        disposable?.let { compositeDisposable?.add(it) }
    }
}