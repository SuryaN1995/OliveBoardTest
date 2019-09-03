package com.example.oliverboardtest.ui.contract

import com.example.oliverboardtest.api.model.Restaurants
import com.example.oliverboardtest.ui.baseModel.BaseView
import com.example.oliverboardtest.ui.baseModel.LocationHelper

interface MainContract {

    interface Presenter {

        fun searchRestaurant(searchText: String?, lat: Double?, lon: Double?, radius: Double?)

        fun startLocationUpdates(locationHelper: LocationHelper?, callerId: Int)

        fun stopLocationUpdates(locationHelper: LocationHelper?)

        fun handleSearch(text: String)

        fun onErrorSearch()

    }

    interface View : BaseView<Presenter> {
        fun showLoading(isLoading: Boolean)

        fun showList(list: List<Restaurants>)

        fun showError(error: String?)

        fun noInternetError()

        fun handleEmpty()

        fun updateEditView(text: String)

        fun hideKeyBoard()
    }

}