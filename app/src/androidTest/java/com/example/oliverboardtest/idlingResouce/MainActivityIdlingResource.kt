package com.example.oliverboardtest.idlingResouce

import androidx.test.espresso.IdlingResource
import com.example.oliverboardtest.ui.view.MainActivity

class MainActivityIdlingResource(internal var activity: MainActivity) : IdlingResource {
    private var callback: IdlingResource.ResourceCallback? = null

    init {
        this.activity.dataListener = object : MainActivity.DataListener {

            override fun onDataLoaded() {
                if (callback != null && activity.isDataReady) {
                    callback?.onTransitionToIdle()
                }
            }
        }
    }

    override fun getName(): String {
        return "mainActivity"
    }

    override fun isIdleNow(): Boolean {
        return activity.isDataReady
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        this.callback = callback
    }
}