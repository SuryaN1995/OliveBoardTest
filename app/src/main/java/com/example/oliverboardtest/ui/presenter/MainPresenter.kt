package com.example.oliverboardtest.ui.presenter

import com.example.oliverboardtest.api.ApiHelper
import com.example.oliverboardtest.api.exception.RetrofitException
import com.example.oliverboardtest.api.model.AppApiHelper
import com.example.oliverboardtest.api.model.SearchResponseModel
import com.example.oliverboardtest.ui.baseModel.LocationHelper
import com.example.oliverboardtest.ui.contract.MainContract

open class MainPresenter(private val apiHelper: AppApiHelper?,private val view: MainContract.View): MainContract.Presenter {

    private var searchText : String? = null
    private var lat : Double ?= null
    private var lon : Double ?= null
    private var radius : Double ?= null

    override fun startLocationUpdates(locationHelper: LocationHelper?, callerId: Int) {
        locationHelper?.startLocationUpdates(callerId)
    }

    override fun stopLocationUpdates(locationHelper: LocationHelper?) {
        locationHelper?.stopLocationUpdates()
    }

    override fun onErrorSearch() {
        searchRestaurant(searchText, lat, lon, radius)
    }

    override fun handleSearch(text: String) {
        if(text.isNotEmpty()){
            val trimmedText = text.trimStart()
            if(text[0].toString() == " "){
                view.updateEditView(text.trimStart())
            }else{
                searchContent(trimmedText)
            }
        }else{
            searchContent(null)
        }
    }

    private fun searchContent(text: String?){
        view.hideKeyBoard()
        searchRestaurant(text,null,null,200.toDouble())
    }


    override fun searchRestaurant(
        searchText: String?,
        lat: Double?,
        lon: Double?,
        radius: Double?
    ) {
        this.let {
            it.searchText = searchText
            it.lat = lat
            it.lon = lon
            it.radius = radius
        }
        view.showLoading(true)
        apiHelper?.searchRequest(searchText,lat,lon,radius,object : ApiHelper.SearchResultCallback{
            override fun onSuccess(response: SearchResponseModel) {
                view.showLoading(false)
                val list = response.restaurants
                if(list?.isNotEmpty() == true){
                    view.showList(list)
                }else{
                    view.handleEmpty()
                }
            }

            override fun onError(throwable: Throwable?) {
                view.showLoading(false)
                val re = throwable as RetrofitException
                if (re.kind == RetrofitException.Kind.CONNECTIVITY) {
                    view.noInternetError()
                } else {
                    view.showError(re.message)
                }
            }

        })

    }

}