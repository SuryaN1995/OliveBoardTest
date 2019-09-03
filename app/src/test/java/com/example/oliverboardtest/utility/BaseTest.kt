package com.example.oliverboardtest.utility

import android.content.Context
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(JUnit4::class)
open class BaseTest {

    @JvmField
    @Rule
    var rule = RxScheduleTestRule()

    @Mock
    lateinit var context: Context

    @Before
    open fun setUp() {
        MockitoAnnotations.initMocks(this)
    }
}