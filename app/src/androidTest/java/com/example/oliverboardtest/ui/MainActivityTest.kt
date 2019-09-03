package com.example.oliverboardtest.ui

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.rule.GrantPermissionRule
import com.example.oliverboardtest.R
import com.example.oliverboardtest.api.helper.ApiManager
import com.example.oliverboardtest.api.helper.ApiService
import com.example.oliverboardtest.api.model.SearchResponseModel
import com.example.oliverboardtest.idlingResouce.MainActivityIdlingResource
import com.example.oliverboardtest.ui.view.MainActivity
import com.example.oliverboardtest.util.AndroidTestUtils
import com.example.oliverboardtest.util.BaseTest
import com.google.gson.Gson
import io.reactivex.Observable
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyDouble
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito

class MainActivityTest : BaseTest() {

    @get:Rule
    var intentsTestRule = IntentsTestRule(MainActivity::class.java, true, false)

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    lateinit var searchResponseModel: SearchResponseModel

    private var idlingResource: MainActivityIdlingResource? = null

    @Mock
    private lateinit var apiService : ApiService

    override fun setUp() {
        super.setUp()
        searchResponseModel = Gson().fromJson(
            AndroidTestUtils.getJsonResponse(
                context,
                "search_response",
                AndroidTestUtils.ResponseStatus.SUCCESS
            ), SearchResponseModel::class.java
        ) as SearchResponseModel

        Handler(Looper.getMainLooper()).postDelayed({
            Mockito.`when`(apiService.search(anyString(), anyDouble(), anyDouble(), anyDouble())).thenAnswer {
                return@thenAnswer Observable.just(searchResponseModel)
            }
            ApiManager.apiService = apiService
        },10)
        intentsTestRule.launchActivity(Intent())
        idlingResource = MainActivityIdlingResource(intentsTestRule.activity)
        enableGPS()

    }

    @Test
    fun launchAppPopulate() {
        IdlingRegistry.getInstance().register(idlingResource)
        onView(AndroidTestUtils.withRecyclerView(R.id.rv_list)
            .atPosition(0)).check(matches(isDisplayed()))
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

}
