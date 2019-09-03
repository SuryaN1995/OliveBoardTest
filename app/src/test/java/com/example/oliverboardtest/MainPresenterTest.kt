package com.example.oliverboardtest

import com.example.oliverboardtest.api.helper.ApiHelper
import com.example.oliverboardtest.api.helper.AppApiHelper
import com.example.oliverboardtest.api.model.SearchResponseModel
import com.example.oliverboardtest.ui.contract.MainContract
import com.example.oliverboardtest.ui.presenter.MainPresenter
import com.example.oliverboardtest.utility.BaseTest
import com.example.oliverboardtest.utility.JsonToObject
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.verify


class MainPresenterTest : BaseTest() {

    private var searchResponseModel: SearchResponseModel? = null

    private var presenter: MainPresenter? = null

    @Mock
    private lateinit var appiHelper: AppApiHelper

    @Mock
    private lateinit var apiHelper: ApiHelper

    @Mock
    private lateinit var view: MainContract.View


    override fun setUp() {
        super.setUp()
        presenter = MainPresenter(appiHelper, view)
        searchResponseModel = JsonToObject.getResponse(
            "search_response",
            SearchResponseModel::class.java
        ) as SearchResponseModel
    }

    @Test
    fun testHandleTextEmpty() {
        presenter?.handleSearch("")
        verify(view, atLeastOnce()).hideKeyBoard()
    }

    @Test
    fun testHandleTextSpaceAtBegin() {
        presenter?.handleSearch(" ")
        verify(view, atLeastOnce()).updateEditView(ArgumentMatchers.anyString())
    }

    @Test
    fun testHandleSearchWithTextSuccess() {
        val listener = ArgumentCaptor.forClass(ApiHelper.SearchResultCallback::class.java)
        presenter?.handleSearch("KFC")
        verify(apiHelper).searchRequest(
            ArgumentMatchers.anyString(), ArgumentMatchers.anyDouble(),
            ArgumentMatchers.anyDouble(), ArgumentMatchers.anyDouble(), listener.capture()
        )
        searchResponseModel?.let { listener.value.onSuccess(it) }
        verify(view, atLeastOnce()).hideKeyBoard()
        verify(view, atLeastOnce()).showLoading(ArgumentMatchers.anyBoolean())
    }
}
