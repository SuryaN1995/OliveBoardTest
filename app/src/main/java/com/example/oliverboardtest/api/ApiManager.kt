package com.example.oliverboardtest.api

import android.content.Context
import com.example.oliverboardtest.AppConstants
import com.example.oliverboardtest.api.exception.NoConnectivityException
import com.example.oliverboardtest.api.exception.RxErrorHandlingFactory
import com.example.oliverboardtest.ui.AppUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Surya N on 30/09/19.
 */
object ApiManager {

    var apiService: ApiService? = null


    private fun createApiService(context: Context): ApiService {

        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val connectivityInterceptor = Interceptor { chain ->
            if (!AppUtils.isInternetConnected(context)) {
                throw NoConnectivityException("Please check your network connection and try again.")
            }
            chain.proceed(chain.request())
        }

        val client = OkHttpClient.Builder().apply {
            networkInterceptors().add(Interceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader(AppConstants.HEADER_KEY, AppConstants.API_KEY)
                request.method(original.method(), original.body())
                val requestBuilder = request.build()
                val response = chain.proceed(requestBuilder)

                response.newBuilder()
                    .body(
                        ResponseBody.create(
                            response.body()?.contentType(), response?.body()?.toString()
                                ?: ""
                        )
                    )
                    .build()
                response
            })
            this.addInterceptor(interceptor)
            this.addInterceptor(connectivityInterceptor)
        }.connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS)

        val retrofit = Retrofit.Builder().baseUrl(AppConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxErrorHandlingFactory.create())
            .client(client.build())
            .build()

        return retrofit.create(ApiService::class.java)
    }

    @Synchronized
    fun getApiService(context: Context): ApiService? {
        if (apiService == null) {
            apiService = createApiService(context)
        }
        return apiService
    }

    fun handleSearch(
        searchText: String?,
        lat: Double?,
        lon: Double?,
        radius: Double?, context: Context
    ) =
        getApiService(context)?.search(
            searchText,
            lat,
            lon,
            radius
        )?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())


    fun restaurantDetails(resId: Int,context: Context) =
        getApiService(context)?.getRestaurantDetails(resId)?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())

}