package com.example.oliverboardtest.ui.contract

import com.example.oliverboardtest.api.model.Restaurant
import com.example.oliverboardtest.ui.helperClass.BaseView

interface RestaurantContract {

    interface View : BaseView<Presenter>{
        fun showLoading(isLoading: Boolean)

        fun showData(response : Restaurant)

        fun showError(error: String?)

        fun noInternetError()
    }

    interface Presenter {
        fun restaurant(resId : Int?)

    }
}