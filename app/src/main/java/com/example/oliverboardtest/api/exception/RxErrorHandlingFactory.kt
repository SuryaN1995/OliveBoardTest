package com.example.oliverboardtest.api.exception

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.*
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException
import java.lang.reflect.Type

open class RxErrorHandlingFactory : CallAdapter.Factory() {

    private val _original by lazy {
        RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
    }

    companion object {
        fun create() : CallAdapter.Factory = RxErrorHandlingFactory()
    }

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *> {
        val wrapped = _original.get(returnType, annotations, retrofit) as CallAdapter<out Any, *>
        return RxCallAdapterWrapper(retrofit, wrapped)
    }

    private class RxCallAdapterWrapper<R>(val _retrofit: Retrofit,
                                          val _wrappedCallAdapter: CallAdapter<R, *>
    ): CallAdapter<R, Observable<R>> {

        override fun responseType(): Type = _wrappedCallAdapter.responseType()


        @Suppress("UNCHECKED_CAST")
        override fun adapt(call: Call<R>): Observable<R> {
            return ((_wrappedCallAdapter.adapt(call) as Observable<R>)).onErrorResumeNext { throwable: Throwable ->
                Observable.error(asRetrofitException(throwable))
            }

        }

        private fun asRetrofitException(throwable: Throwable): RetrofitException {
            // We had non-200 http error
            if (throwable is HttpException) {

                val response = throwable.response() as Response<*>

                return RetrofitException.httpError(response, _retrofit, throwable)
            }
            // A network error happened
            if (throwable is IOException) {

                return RetrofitException.networkError(throwable)
            }

            // A connectivity error happened
            return if (throwable is NoConnectivityException) {
                RetrofitException.connectivityError(throwable)
            } else
                RetrofitException.unexpectedError(Exception("We have encountered an unexpected error"))

            // We don't know what happened. We need to simply convert to an unknown error

        }

    }
}