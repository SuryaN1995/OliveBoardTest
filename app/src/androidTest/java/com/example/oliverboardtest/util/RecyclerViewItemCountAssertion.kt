package com.example.oliverboardtest.util

import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import android.view.View
import androidx.recyclerview.widget.RecyclerView

import org.junit.Assert.assertTrue

class RecyclerViewItemCountAssertion(private val expectedCount: Int) : ViewAssertion {
    private val errorMsg = "Expected count and actual item count do not match"

    override fun check(view: View, noViewFoundException: NoMatchingViewException) {
        if (view !is RecyclerView) {
            throw noViewFoundException
        }
        val itemCount = view.adapter?.itemCount
        assertTrue(errorMsg, itemCount == expectedCount)
    }
}
