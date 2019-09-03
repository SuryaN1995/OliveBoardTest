package com.example.oliverboardtest.ui.presenter

import com.example.oliverboardtest.api.ApiHelper
import com.example.oliverboardtest.api.exception.RetrofitException
import com.example.oliverboardtest.api.model.AppApiHelper
import com.example.oliverboardtest.api.model.Restaurant
import com.example.oliverboardtest.ui.contract.RestaurantContract

open class RestaurantPresenter(private val apiHelper: AppApiHelper?, private val view: RestaurantContract.View) : RestaurantContract.Presenter {

    override fun restaurant(resId: Int?) {
        view.showLoading(true)
        apiHelper?.restaurantDetails(resId?:0,object : ApiHelper.RestaurantDetailsCallback{
            override fun onSuccess(response: Restaurant) {
                view.showLoading(false)
                view.showData(response)
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