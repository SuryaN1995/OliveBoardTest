package com.example.oliverboardtest.api.model

open class SearchResponseModel {

    var restaurants: List<Restaurants>? = null
}

open class Restaurants {

    var restaurant : Restaurant ?=  null

}

open class Restaurant {

    var id: String? = null

    var name: String? = null

    var featured_image: String? = null

    var deeplink: String? = null

    var user_rating: UserRating? = null

    var location : Location ?= null

    var photos : List<Photos> ?= null
}

open class Location {

    var address : String ?= null

    var locality : String ?= null

}

open class Photos {
    var photo : Photo ?= null
}

open class Photo {

    var thumb_url : String ?= null

}

open class UserRating {

    var aggregate_rating: String? = null

}