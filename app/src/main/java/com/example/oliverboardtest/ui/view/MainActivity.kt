package com.example.oliverboardtest.ui.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.oliverboardtest.utils.AppConstants
import com.example.oliverboardtest.R
import com.example.oliverboardtest.api.helper.AppApiHelper
import com.example.oliverboardtest.api.model.Restaurants
import com.example.oliverboardtest.databinding.ActivityMainBinding
import com.example.oliverboardtest.utils.AppUtils
import com.example.oliverboardtest.ui.helperClass.LocationHelper
import com.example.oliverboardtest.ui.helperClass.LocationResultCallback
import com.example.oliverboardtest.ui.helperClass.RecyclerViewClick
import com.example.oliverboardtest.ui.contract.MainContract
import com.example.oliverboardtest.ui.presenter.MainPresenter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task

open class MainActivity : AppCompatActivity(), MainContract.View, LocationResultCallback,
    RecyclerViewClick {

    override var presenter: MainContract.Presenter? = null

    private var binding: ActivityMainBinding? = null

    private var adapter: MainAdapter? = null

    private var locationHelper: LocationHelper? = null

    private var apiHelper: AppApiHelper? = null

    private var restaurantList = ArrayList<Restaurants>()

    var dataListener: DataListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        apiHelper = AppApiHelper(this)
        presenter = MainPresenter(apiHelper, this)
        locationHelper = LocationHelper(this, this)
        binding?.isError = false
        binding?.isLoading = false
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            AppConstants.LOCATION_CODE_REQUEST
        )
        initRv()
        binding?.tvErrorBtn?.setOnClickListener {
            presenter?.onErrorSearch()
        }
        binding?.searchBar?.setOnEditorActionListener(editActionListener)
        presenter?.searchRestaurant(null, null, null, null)
    }

    private fun initRv() {
        adapter = MainAdapter(restaurantList, this)
        binding?.rvList?.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = this@MainActivity.adapter
            /**
             * To avoid multiple item click of the recycler view
             */
            isMotionEventSplittingEnabled = false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {
            if (requestCode == AppConstants.LOCATION_CODE_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (locationHelper?.isLocationEnabled() == true) {
                    presenter?.startLocationUpdates(locationHelper, AppConstants.LOCATION_REQUEST)
                } else {
                    changeLocationSettings()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.LOCATION_GPS_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                presenter?.startLocationUpdates(locationHelper, AppConstants.LOCATION_REQUEST)
            }
        }
    }

    /**
     *
     * To enable location dialog.
     *
     */
    private fun changeLocationSettings() {
        val builder = locationHelper?.createLocationRequest()?.let {
            LocationSettingsRequest.Builder()
                .addLocationRequest(it)
        }
        this.let {
            val client: SettingsClient = LocationServices.getSettingsClient(it)
            val task: Task<LocationSettingsResponse> =
                client.checkLocationSettings(builder?.build())
            task.addOnSuccessListener {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                if (AppUtils.isInternetConnected(this)) {
                    presenter?.startLocationUpdates(locationHelper, AppConstants.LOCATION_REQUEST)
                }
            }

            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        exception.startResolutionForResult(
                            this,
                            AppConstants.LOCATION_GPS_REQUEST
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                        sendEx.printStackTrace()
                    }
                }
            }
        }

    }

    /**
     *
     * OnKeyboard IME_ACTION_SEARCH click, triggers this callback
     *
     */
    private val editActionListener = TextView.OnEditorActionListener { textView, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            presenter?.handleSearch(binding?.searchBar?.text?.toString() ?: "")
        }
        textView.imeOptions = EditorInfo.IME_ACTION_SEARCH
        false
    }

    override fun updateEditView(text: String) {
        binding?.searchBar?.apply {
            setSelection(text.length)
            setText(text)
        }
        hideKeyBoard()
    }

    override fun showLoading(isLoading: Boolean) {
        binding?.isLoading = isLoading
    }

    override fun handleEmpty() {
        binding?.isError = true
        binding?.tvErrorBtn?.visibility = View.GONE
        binding?.tvError?.text = getString(R.string.empty_msg)
        isDataReady = true
    }

    override fun hideKeyBoard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding?.searchBar?.windowToken, 0)
    }

    override fun showList(list: List<Restaurants>) {
        restaurantList.clear()
        restaurantList.addAll(list)
        binding?.isError = false
        adapter?.notifyDataSetChanged()
        isDataReady =true
        dataListener?.onDataLoaded()
    }

    override fun showError(error: String?) {
        binding?.isError = true
        binding?.tvError?.text = error
        isDataReady = true
        dataListener?.onDataLoaded()
    }

    override fun noInternetError() {
        binding?.isError = true
        binding?.tvError?.text = getString(R.string.network_error_msg)
        isDataReady = true
        dataListener?.onDataLoaded()
    }

    override fun onItemClickPosition(position: Int) {
        if (position in 0 until restaurantList.size) {
            launchRestaurantDetail(restaurantList[position].restaurant?.id)
        }
    }

    /**
     * Launch the fragment Restaurant
     */
    private fun launchRestaurantDetail(id: String?) {
        val fragment = RestaurantFragment()
        val bundle = Bundle()
        bundle.putInt(AppConstants.RESTAURANT_ID, id?.toInt() ?: 0)
        fragment.arguments = bundle
        supportFragmentManager?.beginTransaction()?.add(android.R.id.content, fragment)
            ?.addToBackStack(fragment.tag)
            ?.commit()
    }

    override fun onLocationResult(location: Location, callerId: Int) {
        presenter?.stopLocationUpdates(locationHelper)
        presenter?.searchRestaurant(null, location.latitude, location.longitude, 2000.toDouble())
    }

    /**
     * Adding for IdlingResource test
     */
    var isDataReady: Boolean = false

    interface DataListener {
        fun onDataLoaded()
    }
}
