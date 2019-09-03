package com.example.oliverboardtest.util

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations


@RunWith(AndroidJUnit4::class)
open class BaseTest {

    protected open lateinit var context: Context

    @Before
    @Throws(Exception::class)
    open fun setUp() {
        MockitoAnnotations.initMocks(this)
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }


    /**
     * [tapTurnOnGpsBtn] will perform the action
     *
     */
    @Throws(UiObjectNotFoundException::class)
    fun tapTurnOnGpsBtn() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val allowGpsBtn = device.findObject(UiSelector()
                .className("android.widget.Button").packageName("com.google.android.gms")
                .resourceId("android:id/button1")
                .clickable(true).checkable(false))
        device.pressDelete() // just in case to turn ON blur screen (not a wake up) for some devices like HTC and some other
        if (allowGpsBtn.exists() && allowGpsBtn.isEnabled) {
            do {
                allowGpsBtn.click()
            } while (allowGpsBtn.exists())
        }
    }

    fun enableGPS() {
        try {
            tapTurnOnGpsBtn()
        } catch (e: UiObjectNotFoundException) {
            e.printStackTrace()
        }

    }


}
